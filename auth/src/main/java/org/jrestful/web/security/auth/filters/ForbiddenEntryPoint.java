package org.jrestful.web.security.auth.filters;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class ForbiddenEntryPoint implements AuthenticationEntryPoint {

  private final static Logger LOGGER = LoggerFactory.getLogger(ForbiddenEntryPoint.class);

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException,
      ServletException {
    LOGGER.error("Access denied", authException);
    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentLength(0);
    response.flushBuffer();
  }

}
