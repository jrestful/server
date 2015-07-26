package org.jrestful.web.security.cors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Handles CORS requests.
 */
public class CorsInterceptor extends HandlerInterceptorAdapter {

  private final String allowOrigin;

  @Autowired
  public CorsInterceptor(@Value("${cors.allowOrigin:#{null}}") String allowOrigin) {
    this.allowOrigin = allowOrigin;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    if (allowOrigin != null) {
      response.setHeader("Access-Control-Allow-Origin", allowOrigin);
      response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
      response.setHeader("Access-Control-Max-Age", "3600");
      response.setHeader("Access-Control-Allow-Headers", "X-Requested-With");
    }
    return super.preHandle(request, response, handler);
  }

}
