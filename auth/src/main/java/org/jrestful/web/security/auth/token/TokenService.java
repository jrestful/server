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
    Date accessTokenExpirationDate = DateUtils.addToNow(Calendar.SECOND, accessTokenLifetime);
    AccessToken accessTokenObject = new AccessToken(user.getId().toString(), accessTokenExpirationDate);
    String accessTokenString = tokenMapper.serialize(accessTokenObject);
    HttpUtils.writeHeader(response, accessTokenHeaderName, accessTokenString);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("New AccessToken added to response header " + accessTokenHeaderName);
    }
    if (accessTokenCookieName != null) {
      Cookie cookie = HttpUtils.writeCookie(response, accessTokenCookieName, accessTokenString);
      cookie.setMaxAge(accessTokenLifetime);
      cookie.setPath("/");
      cookie.setHttpOnly(true);
      cookie.setSecure(securedCookie);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("New AccessToken added to response cookie " + accessTokenCookieName);
      }
    }
    if (refreshTokenHeaderName != null) {
      writeRefreshToken(user, accessTokenExpirationDate, response);
    }
  }

  private void writeRefreshToken(U user, Date accessTokenExpirationDate, HttpServletResponse response) {
    Date refreshTokenExpirationDate = refreshTokenLifetime == -1 ? null : DateUtils.add(accessTokenExpirationDate, Calendar.SECOND,
        refreshTokenLifetime);
    RefreshToken refreshTokenObject = new RefreshToken(accessTokenExpirationDate, refreshTokenExpirationDate);
    String refreshTokenString = tokenMapper.serialize(refreshTokenObject);
    userService.persistRefreshToken(user.getId(), refreshTokenString);
    LOGGER.debug("New RefreshToken persisted");
    HttpUtils.writeHeader(response, refreshTokenHeaderName, refreshTokenString);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("New RefreshToken added to response header " + refreshTokenHeaderName);
    }
    if (refreshTokenCookieName != null) {
      Cookie cookie = HttpUtils.writeCookie(response, refreshTokenCookieName, refreshTokenString);
      cookie.setMaxAge(refreshTokenLifetime == -1 ? TEN_YEARS : accessTokenLifetime + refreshTokenLifetime);
      cookie.setPath("/");
      cookie.setHttpOnly(true);
      cookie.setSecure(securedCookie);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("New RefreshToken added to response cookie " + refreshTokenCookieName);
      }
    }
  }

  public U read(HttpServletRequest request, HttpServletResponse response) {
    U user = null;
    String accessTokenString = HttpUtils.readHeader(request, accessTokenHeaderName);
    if (accessTokenString == null && accessTokenCookieName != null) {
      accessTokenString = HttpUtils.readCookie(request, accessTokenCookieName);
    }
    if (accessTokenString != null) {
      AccessToken accessTokenObject = tokenMapper.deserialize(accessTokenString, AccessToken.class, request);
      if (accessTokenObject != null) {
        if (accessTokenObject.isSyntacticallyValid()) {
          if (accessTokenObject.isNotExpired(new Date())) {
            LOGGER.debug("Valid AccessToken found in request");
            user = userService.findOne(userIdConverter.convert(accessTokenObject.getUserId()));
          } else {
            LOGGER.debug("Expired AccessToken found in request");
            if (refreshTokenHeaderName != null) {
              user = readRefreshToken(accessTokenObject, request, response);
            }
          }
        } else {
          LOGGER.warn("AccessToken is syntactically invalid: " + accessTokenString + " | " + HttpUtils.serialize(request));
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
      RefreshToken refreshTokenObject = tokenMapper.deserialize(refreshTokenString, RefreshToken.class, request);
      if (refreshTokenObject != null) {
        if (refreshTokenObject.isSyntacticallyValid()) {
          if (refreshTokenObject.isAvailable(new Date())) {
            if (refreshTokenObject.isNotExpired(new Date())) {
              user = userService.findOneByRefreshToken(refreshTokenString);
              if (user != null) {
                if (user.getId().equals(accessTokenObject.getUserId())) {
                  LOGGER.debug("Valid refresh token found in request");
                  write(user, response);
                } else {
                  LOGGER.warn("RefreshToken's linked user does not match with AccessToken embedded user: " + user.getId() + " (" + user.getEmail()
                      + ") vs " + accessTokenObject.getUserId() + " | " + HttpUtils.serialize(request));
                  user = null;
                }
              } else {
                LOGGER.debug("Transient RefreshToken found in request");
              }
            } else {
              LOGGER.debug("Expired RefreshToken found in request");
            }
          } else {
            LOGGER.warn("RefreshToken is not available yet: " + refreshTokenString + " | " + HttpUtils.serialize(request));
          }
        } else {
          LOGGER.warn("RefreshToken is syntactically invalid: " + refreshTokenString + " | " + HttpUtils.serialize(request));
        }
      }
    }
    return user;
  }

}
