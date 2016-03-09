package org.jrestful.web.controllers.support;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import javax.servlet.http.HttpServletResponse;

import org.jrestful.business.exceptions.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class ExceptionResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionResolver.class);

  private final String apiVersion;

  @Autowired
  public ExceptionResolver(@Value("#{appProps['app.apiVersion']}") String apiVersion) {
    this.apiVersion = apiVersion;
  }

  @ExceptionHandler(HttpStatusException.class)
  public ResponseEntity<?> resolveHttpStatusException(HttpStatusException e) {
    LOGGER.error("HttpStatusException catched, returning " + e.getStatus(), e);
    return e.toResponseEntity();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> resolveException(Exception e, HttpServletResponse response) throws Exception {
    if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
      LOGGER.warn("@ResponseStatus annotated Exception catched, rethrowing", e);
      throw e;
    } else if (e instanceof AccessDeniedException) {
      LOGGER.warn("AccessDeniedException catched, rethrowing", e);
      throw e;
    } else if (e instanceof NoHandlerFoundException) {
      LOGGER.warn("NoHandlerFoundException catched, returning " + HttpStatus.NOT_FOUND, e);
      response.setHeader("X-API-Version", apiVersion);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else if (e instanceof MissingServletRequestParameterException) {
      LOGGER.warn("MissingServletRequestParameterException catched, returning " + HttpStatus.UNPROCESSABLE_ENTITY, e);
      String paramName = LOWER_CAMEL.to(UPPER_UNDERSCORE, ((MissingServletRequestParameterException) e).getParameterName());
      return new ResponseEntity<>("EMPTY_" + paramName, HttpStatus.UNPROCESSABLE_ENTITY);
    } else {
      LOGGER.error("Exception catched, returning " + HttpStatus.INTERNAL_SERVER_ERROR, e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
