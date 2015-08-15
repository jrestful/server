package org.jrestful.web.security.cors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Handles CORS requests.
 */
@Component
public class CorsInterceptor extends HandlerInterceptorAdapter {

  private final String allowOrigin;

  private final String allowMethods;

  private final String allowHeaders;

  private final String maxAge;

  @Autowired
  public CorsInterceptor(@Value("#{secProps['cors.allowOrigin']}") String allowOrigin,
      @Value("#{secProps['cors.allowMethods'] ?: 'GET, POST, PUT, DELTE'}") String allowMethods,
      @Value("#{secProps['cors.allowHeaders']}") String allowHeaders, @Value("#{secProps['cors.maxAge']}") String maxAge) {
    this.allowOrigin = allowOrigin;
    this.allowMethods = allowMethods;
    this.allowHeaders = allowHeaders;
    this.maxAge = maxAge;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    if (allowOrigin != null) {
      response.setHeader("Access-Control-Allow-Origin", allowOrigin);
      response.setHeader("Access-Control-Allow-Methods", allowMethods);
      if (allowHeaders != null) {
        response.setHeader("Access-Control-Allow-Headers", allowHeaders);
      }
      if (maxAge != null) {
        response.setHeader("Access-Control-Max-Age", maxAge);
      }
    }
    return super.preHandle(request, response, handler);
  }

}
