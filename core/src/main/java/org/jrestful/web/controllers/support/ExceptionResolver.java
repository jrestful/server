package org.jrestful.web.controllers.support;

import static org.jrestful.web.beans.RestResponse.badRequest;

import org.jrestful.business.exceptions.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionResolver.class);

  @ExceptionHandler(HttpStatusException.class)
  public ResponseEntity<?> resolveHttpStatusException(HttpStatusException e) {
    LOGGER.error("HttpStatusException catched, returning " + e.getStatus(), e);
    return e.toResponseEntity();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> resolveException(Exception e) throws Exception {
    if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
      LOGGER.warn("@ResponseStatus annotated Exception catched, rethrowing", e);
      throw e;
    } else if (e instanceof AccessDeniedException) {
      LOGGER.warn("AccessDeniedException catched, rethrowing", e);
      throw e;
    } else {
      LOGGER.error("Exception catched, returning " + HttpStatus.BAD_REQUEST, e);
      return badRequest();
    }
  }

}
