package org.jrestful.web.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jrestful.util.Base64Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.io.ByteStreams;

/**
 * Operations on HTTP requests and responses.
 */
public final class HttpUtils {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

  private static final String ENCODING = "UTF-8";
  
  private static final String FORWARDED_FOR_HEADER_NAME = "X-Forwarded-For";
  
  private static final String REAL_IP_HEADER_NAME = "X-Real-IP";
  
  private static final String USER_AGENT_HEADER_NAME = "User-Agent";

  public static String readCookie(HttpServletRequest request, String cookieName) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      try {
        for (Cookie cookie : cookies) {
          if (cookieName.equals(URLDecoder.decode(cookie.getName(), ENCODING))) {
            return URLDecoder.decode(cookie.getValue(), ENCODING);
          }
        }
      } catch (UnsupportedEncodingException ignore) {
        // cannot happen
      }
    }
    return null;
  }

  public static Cookie writeCookie(HttpServletResponse response, String name, String value) {
    try {
      name = URLEncoder.encode(name, ENCODING);
      value = URLEncoder.encode(value, ENCODING);
    } catch (UnsupportedEncodingException ignore) {
      // cannot happen
    }
    Cookie cookie = new Cookie(name, value);
    response.addCookie(cookie);
    return cookie;
  }

  public static String readHeader(HttpServletRequest request, String name) {
    return request.getHeader(name);
  }

  public static void writeHeader(HttpServletResponse response, String name, String value) {
    response.setHeader(name, value);
  }
  
  public static String serialize(HttpServletRequest request) {
    
    StringBuilder metadata = new StringBuilder();
    
    metadata.append(request.getMethod());
    metadata.append(" ").append(request.getRequestURI().substring(request.getContextPath().length()));
    
    try {
      byte[] payload = ByteStreams.toByteArray(request.getInputStream());
      if (payload.length > 0) {
        metadata.append(" [body=").append(Base64Utils.encode(payload).asString()).append("]");
      }
    } catch (IOException e) {
      LOGGER.warn("Could not read request payload", e);
    }
    
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated()) {
      metadata.append(" [auth=").append(authentication.getPrincipal()).append("]");
    }
    
    metadata.append(" [ip=").append(request.getRemoteAddr());
    String forwardedFor = request.getHeader(FORWARDED_FOR_HEADER_NAME);
    if (forwardedFor != null) {
      metadata.append("; ").append(FORWARDED_FOR_HEADER_NAME).append(": ").append(forwardedFor);
    }
    String realIp = request.getHeader(REAL_IP_HEADER_NAME);
    if (realIp != null) {
      metadata.append("; ").append(REAL_IP_HEADER_NAME).append(": ").append(realIp);
    }
    metadata.append("]");
    
    String userAgent = request.getHeader(USER_AGENT_HEADER_NAME);
    if (userAgent != null) {
      metadata.append(" [ua=").append(userAgent).append("]");
    }
    
    return metadata.toString();
  }

  private HttpUtils() {
  }

}
