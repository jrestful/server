package org.jrestful.web.security.auth.token;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jrestful.business.converters.UserIdConverter;
import org.jrestful.business.support.GenericAuthUser;
import org.jrestful.business.support.GenericAuthUserService;
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
  
  private static final int TEN_YEARS = 10 * 365 * 24 * 60 * 60;

  private final GenericAuthUserService<U, K> userService;

  private final TokenMapper tokenMapper;

  private final UserIdConverter<K> userIdConverter;

  private final String accessTokenHeaderName;

  private final String accessTokenCookieName;

  private final int accessTokenLifetime;

  private final String refreshTokenHeaderName;

  private final String refreshTokenCookieName;

  private final int refreshTokenLifetime;

  private final boolean securedCookie;

  @Autowired
  public TokenService(GenericAuthUserService<U, K> userService, TokenMapper tokenMapper, UserIdConverter<K> userIdConverter,
      @Value("#{secProps['auth.accessTokenHeaderName']}") String accessTokenHeaderName,
      @Value("#{secProps['auth.accessTokenCookieName']}") String accessTokenCookieName,
      @Value("#{secProps['auth.accessTokenLifetime'] ?: 30 * 60}") int accessTokenLifetime,
      @Value("#{secProps['auth.refreshTokenHeaderName']}") String refreshTokenHeaderName,
      @Value("#{secProps['auth.refreshTokenCookieName']}") String refreshTokenCookieName,
      @Value("#{secProps['auth.refreshTokenLifetime'] ?: 10 * 24 * 60 * 60}") int refreshTokenLifetime,
      @Value("#{secProps['auth.securedCookie'] ?: true}") boolean securedCookie) {
    this.userService = userService;
    this.tokenMapper = tokenMapper;
    this.userIdConverter = userIdConverter;
    this.accessTokenHeaderName = accessTokenHeaderName;
    this.accessTokenCookieName = accessTokenCookieName;
    this.accessTokenLifetime = accessTokenLifetime;
    this.refreshTokenHeaderName = refreshTokenHeaderName;
    this.refreshTokenCookieName = refreshTokenCookieName;
    this.refreshTokenLifetime = refreshTokenLifetime;
    this.securedCookie = securedCookie;
  }

  public void write(U user, HttpServletResponse response) {
    writeAccessToken(user, response);
  }

  private void writeAccessToken(U user, HttpServletResponse response) {
    Date accessTokenExpirationDate = DateUtils.addToNow(Calendar.SECOND, accessTokenLifetime);
    AccessToken accessTokenObject = new AccessToken(user.getId().toString(), accessTokenExpirationDate);
    String accessTokenString = tokenMapper.serialize(accessTokenObject);
    HttpUtils.writeHeader(response, accessTokenHeaderName, accessTokenString);
    LOGGER.debug("New access token added to response header " + accessTokenHeaderName);
    if (accessTokenCookieName != null) {
      Cookie cookie = HttpUtils.writeCookie(response, accessTokenCookieName, accessTokenString);
      cookie.setMaxAge(accessTokenLifetime);
      cookie.setPath("/");
      cookie.setHttpOnly(true);
      cookie.setSecure(securedCookie);
      LOGGER.debug("New access token added to response cookie " + accessTokenCookieName);
    }
    if (refreshTokenHeaderName != null) {
      writeRefreshToken(user, accessTokenExpirationDate, response);
    }
  }

  private void writeRefreshToken(U user, Date accessTokenExpirationDate, HttpServletResponse response) {
    Date refreshTokenExpirationDate = refreshTokenLifetime == -1 ? null : DateUtils.add(accessTokenExpirationDate, Calendar.SECOND, refreshTokenLifetime);
    RefreshToken refreshTokenObject = new RefreshToken(user.getId().toString(), accessTokenExpirationDate, refreshTokenExpirationDate);
    String refreshTokenString = tokenMapper.serialize(refreshTokenObject);
    HttpUtils.writeHeader(response, refreshTokenHeaderName, refreshTokenString);
    LOGGER.debug("New refresh token added to response header " + refreshTokenHeaderName);
    if (refreshTokenCookieName != null) {
      Cookie cookie = HttpUtils.writeCookie(response, refreshTokenCookieName, refreshTokenString);
      cookie.setMaxAge(refreshTokenLifetime == -1 ? TEN_YEARS : accessTokenLifetime + refreshTokenLifetime);
      cookie.setPath("/");
      cookie.setHttpOnly(true);
      cookie.setSecure(securedCookie);
      LOGGER.debug("New refresh token added to response cookie " + refreshTokenCookieName);
    }
    userService.persistRefreshToken(user.getId(), refreshTokenString);
    LOGGER.debug("New refresh token persisted");
  }

  public U read(HttpServletRequest request, HttpServletResponse response) {
    return readAccessToken(request, response);
  }
  
  private U readAccessToken(HttpServletRequest request, HttpServletResponse response) {
    U user = null;
    String accessTokenString = HttpUtils.readHeader(request, accessTokenHeaderName);
    if (accessTokenString == null && accessTokenCookieName != null) {
      accessTokenString = HttpUtils.readCookie(request, accessTokenCookieName);
    }
    if (accessTokenString != null) {
      AccessToken accessTokenObject = tokenMapper.deserialize(accessTokenString, AccessToken.class);
      if (accessTokenObject != null) {
        if (accessTokenObject.isValid()) {
          LOGGER.debug("Valid access token found in request");
          user = userService.findOne(userIdConverter.convert(accessTokenObject.getUserId()));
        } else {
          LOGGER.debug("Invalid access token found in request");
          if (refreshTokenHeaderName != null) {
            user = readRefreshToken(accessTokenObject, request, response);
          }
        }
      }
    }
    return user;
  }
  
  private U readRefreshToken(AccessToken accessTokenObject, HttpServletRequest request, HttpServletResponse response) {
    U user = null;
    String refreshTokenString = HttpUtils.readHeader(request, refreshTokenHeaderName);
    if (refreshTokenString == null && refreshTokenCookieName != null) {
      refreshTokenString = HttpUtils.readCookie(request, refreshTokenCookieName);
    }
    if (refreshTokenString != null) {
      RefreshToken refreshTokenObject = tokenMapper.deserialize(refreshTokenString, RefreshToken.class);
      if (refreshTokenObject != null) {
        if (refreshTokenObject.isValid()) {
          if (refreshTokenObject.getUserId().equals(accessTokenObject.getUserId())) {
            user = userService.findOneByRefreshToken(refreshTokenString);
            if (user != null) {
              if (refreshTokenObject.getUserId().equals(user.getId())) {
                LOGGER.debug("Valid refresh token found in request");
                write(user, response);
              } else {
                LOGGER.warn("Refresh token does not match with database entry: " + refreshTokenObject.getUserId() + " vs " + user.getId());
                user = null;
              }
            }
          } else {
            LOGGER.warn("Refresh token does not match with access token: " + refreshTokenObject.getUserId() + " vs " + accessTokenObject.getUserId());
          }
        } else {
          LOGGER.debug("Invalid refresh token found in request");
        }
      }
    }
    return user;
  }

}
