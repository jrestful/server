package org.jrestful.web.security.csrf;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jrestful.web.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.collect.Sets;

@Component
public class StatelessCsrfFilter extends OncePerRequestFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(StatelessCsrfFilter.class);

  public static final class CsrfProtectionMethodMatcher implements RequestMatcher {

    private static final Set<RequestMethod> NO_PROTECTION_METHODS;
    static {
      Set<RequestMethod> noProtectionMethods = new HashSet<RequestMethod>();
      noProtectionMethods.add(RequestMethod.GET);
      noProtectionMethods.add(RequestMethod.HEAD);
      noProtectionMethods.add(RequestMethod.TRACE);
      noProtectionMethods.add(RequestMethod.OPTIONS);
      NO_PROTECTION_METHODS = Sets.immutableEnumSet(noProtectionMethods);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
      return !NO_PROTECTION_METHODS.contains(RequestMethod.valueOf(request.getMethod()));
    }

  }

  private final RequestMatcher requireCsrfProtectionMatcher = new CsrfProtectionMethodMatcher();

  private final AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandlerImpl();

  private final String headerName;

  private final String cookieName;

  @Autowired
  public StatelessCsrfFilter(@Value("${csrf.headerName}") String headerName, @Value("${csrf.cookieName}") String cookieName) {
    this.headerName = headerName;
    this.cookieName = cookieName;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException,
      IOException {
    boolean accessDenied = false;
    if (requireCsrfProtectionMatcher.matches(request)) {
      String header = request.getHeader(headerName);
      String cookie = RequestUtils.readCookie(request, cookieName);
      accessDenied = header == null || !header.equals(cookie);
    }
    if (accessDenied) {
      LOGGER.error("Missing or non-matching CSRF token for URL " + request.getRequestURL());
      accessDeniedHandler.handle(request, response, new AccessDeniedException("Missing or non-matching CSRF token"));
    } else {
      filterChain.doFilter(request, response);
    }
  }

}
