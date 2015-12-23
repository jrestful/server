package org.jrestful.web.util;

import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GzipFilter extends net.sf.ehcache.constructs.web.filter.GzipFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(GzipFilter.class);

  private static final String EXCLUDES_PARAM = "excludes";

  private Pattern excludes;

  @Override
  protected void doInit(FilterConfig filterConfig) throws Exception {
    super.doInit(filterConfig);
    String excludesParam = filterConfig.getInitParameter(EXCLUDES_PARAM);
    if (excludesParam != null) {
      excludes = Pattern.compile(excludesParam);
    }
  }

  @Override
  protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {
    if (isExcluded(request)) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("URL " + request.getRequestURL() + " is excluded, writing without gzip compression");
      }
      chain.doFilter(request, response);
    } else {
      super.doFilter(request, response, chain);
    }
  }

  private boolean isExcluded(HttpServletRequest request) {
    return excludes != null && excludes.matcher(request.getRequestURI().substring(request.getContextPath().length())).matches();
  }

}
