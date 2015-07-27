package org.jrestful.web.security.auth.token;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jrestful.util.DateUtils;
import org.jrestful.web.security.auth.user.AuthUser;
import org.jrestful.web.security.auth.user.AuthUserService;
import org.jrestful.web.security.auth.user.UserIdConverter;
import org.jrestful.web.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenService<U extends AuthUser<K>, K extends Serializable> {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);

  private static final int TEN_DAYS = 10;

  private static final int IN_SECONDS = 24 * 60 * 60;

  private final AuthUserService<U, K> userService;

  private final TokenMapper tokenMapper;

  private final UserIdConverter<K> userIdConverter;

  private final String cookieName;

  private final String headerName;

  @Autowired
  public TokenService(AuthUserService<U, K> userService, TokenMapper tokenMapper, UserIdConverter<K> userIdConverter,
      @Value("#{secProps['auth.cookieName']}") String cookieName, @Value("#{secProps['auth.headerName']}") String headerName) {
    this.userService = userService;
    this.tokenMapper = tokenMapper;
    this.userIdConverter = userIdConverter;
    this.cookieName = cookieName;
    this.headerName = headerName;
  }

  public void write(U user, HttpServletResponse response) {
    Date expirationDate = DateUtils.addToNow(Calendar.DAY_OF_MONTH, TEN_DAYS);
    Token tokenObject = new Token(user.getId().toString(), expirationDate);
    String tokenString = tokenMapper.serialize(tokenObject);
    HttpUtils.writeHeader(response, headerName, tokenString);
    if (cookieName != null) {
      HttpUtils.writeCookie(response, cookieName, tokenString, TEN_DAYS * IN_SECONDS);
    }
  }

  public U read(HttpServletRequest request) {
    String tokenString = HttpUtils.readHeader(request, headerName);
    if (tokenString == null && cookieName != null) {
      tokenString = HttpUtils.readCookie(request, cookieName);
    }
    if (tokenString != null) {
      Token tokenObject = tokenMapper.deserialize(tokenString);
      if (tokenObject != null) {
        LOGGER.debug("Authentication token found: " + tokenObject);
        return userService.findOne(userIdConverter.convert(tokenObject.getId()));
      }
    }
    return null;
  }

}
