package org.jrestful.web.security.auth.token;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jrestful.util.DateUtils;
import org.jrestful.web.security.auth.user.AuthUser;
import org.jrestful.web.security.auth.user.AuthUserService;
import org.jrestful.web.util.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenService<U extends AuthUser<K>, K extends Serializable> {

  private static final int TEN_DAYS = 10;

  private static final int IN_SECONDS = 24 * 60 * 60;

  private final AuthUserService<U, K> userService;

  private final TokenMapper<K> tokenMapper;

  private final String cookieName;

  @Autowired
  public TokenService(AuthUserService<U, K> userService, TokenMapper<K> tokenMapper, @Value("${auth.cookieName}") String cookieName) {
    this.userService = userService;
    this.tokenMapper = tokenMapper;
    this.cookieName = cookieName;
  }

  public void write(U user, HttpServletResponse response) {
    Date expirationDate = DateUtils.addToNow(Calendar.DAY_OF_MONTH, TEN_DAYS);
    Token<K> tokenObject = new Token<K>(user.getId(), expirationDate);
    String tokenString = tokenMapper.serialize(tokenObject);
    RequestUtils.writeCookie(response, cookieName, tokenString, TEN_DAYS * IN_SECONDS);
  }

  public U read(HttpServletRequest request) {
    String tokenString = RequestUtils.readCookie(request, cookieName);
    if (tokenString != null) {
      Token<K> tokenObject = tokenMapper.deserialize(tokenString);
      if (tokenObject != null) {
        return userService.findOne(tokenObject.getId());
      }
    }
    return null;
  }

}
