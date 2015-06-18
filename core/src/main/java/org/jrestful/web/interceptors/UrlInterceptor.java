package org.jrestful.web.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Sets URL related attributes to requests.
 */
public class UrlInterceptor extends HandlerInterceptorAdapter {

  public static final String DOMAIN_URL = "domainUrl";

  public static final String BASE_URL = "baseUrl";

  public static final String REQUEST_URL = "requestUrl";

  public static final String SHORT_URL = "shortUrl";

  public static final String FULL_URL = "fullUrl";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String requestUrl = request.getRequestURL().toString();
    String requestUri = request.getRequestURI();
    String contextPath = request.getContextPath();
    String queryString = request.getQueryString();
    String domainUrl = requestUrl.substring(0, requestUrl.length() - requestUri.length());
    String baseUrl = domainUrl + contextPath;
    String shortUrl = requestUri.substring(contextPath.length());
    request.setAttribute(DOMAIN_URL, domainUrl);
    request.setAttribute(BASE_URL, baseUrl);
    request.setAttribute(REQUEST_URL, requestUrl);
    request.setAttribute(SHORT_URL, shortUrl);
    if (queryString != null) {
      requestUrl += "?" + queryString;
    }
    request.setAttribute(FULL_URL, requestUrl);
    return super.preHandle(request, response, handler);
  }

}