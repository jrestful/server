package org.jrestful.web.security.auth.token;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jrestful.business.converters.UserIdConverter;
import org.jrestful.business.support.GenericAuthUserService;
import org.jrestful.data.documents.support.GenericAuthUser;
import org.jrestful.util.DateUtils;
import org.jrestful.web.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenService<U extends GenericAuthUser<K>, K extends Serializable> {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);

  private final GenericAuthUserService<U, K> userService;

  private final TokenMapper tokenMapper;

  private final UserIdConverter<K> userIdConverter;

  private final String cookieName;
  
  private final boolean securedCookie;

  private final String headerName;

  private final int tokenLifetime;

  @Autowired
  public TokenService(GenericAuthUserService<U, K> userService, TokenMapper tokenMapper, UserIdConverter<K> userIdConverter,
      @Value("#{secProps['auth.headerName']}") String headerName, @Value("#{secProps['auth.cookieName']}") String cookieName,
      @Value("#{secProps['auth.securedCookie'] ?: true}") boolean securedCookie, @Value("#{secProps['auth.tokenLifetime'] ?: 86400}") int tokenLifetime) {
    this.userService = userService;
    this.tokenMapper = tokenMapper;
    this.userIdConverter = userIdConverter;
    this.headerName = headerName;
    this.cookieName = cookieName;
    this.securedCookie = securedCookie;
    this.tokenLifetime = tokenLifetime;
  }

  public void write(U user, HttpServletResponse response) {
    Date expirationDate = DateUtils.addToNow(Calendar.SECOND, tokenLifetime);
    Token tokenObject = new Token(user.getId().toString(), expirationDate);
    String tokenString = tokenMapper.serialize(tokenObject);
    HttpUtils.writeHeader(response, headerName, tokenString);
    if (cookieName != null) {
      Cookie cookie = HttpUtils.writeCookie(response, cookieName, tokenString);
      cookie.setMaxAge(tokenLifetime);
      cookie.setPath("/");
      cookie.setHttpOnly(true);
      cookie.setSecure(securedCookie);
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
