package org.jrestful.web.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Operations on HTTP requests and responses.
 */
public final class HttpUtils {

  private static final String UTF_8 = "UTF-8";

  public static String readCookie(HttpServletRequest request, String cookieName) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      try {
        for (Cookie cookie : cookies) {
          if (cookieName.equals(URLDecoder.decode(cookie.getName(), UTF_8))) {
            return URLDecoder.decode(cookie.getValue(), UTF_8);
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
      name = URLEncoder.encode(name, UTF_8);
      value = URLEncoder.encode(value, UTF_8);
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

  private HttpUtils() {
  }

}
