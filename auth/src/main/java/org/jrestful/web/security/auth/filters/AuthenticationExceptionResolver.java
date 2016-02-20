package org.jrestful.web.security.auth.filters;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jrestful.web.security.auth.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationExceptionResolver implements AuthenticationEntryPoint {

  private final static Logger LOGGER = LoggerFactory.getLogger(AuthenticationExceptionResolver.class);

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException,
      ServletException {
    HttpStatus status = CurrentUser.isAnonymous() ? HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN;
    LOGGER.error("Access denied, returning " + status, authException);
    response.setStatus(status.value());
    response.setContentLength(0);
    response.flushBuffer();
  }

}
