package org.jrestful.business.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HttpStatusException extends Exception {

  private static final long serialVersionUID = 1L;

  private final HttpStatus status;

  public HttpStatusException(HttpStatus status, String message) {
    super(message);
    this.status = status;
  }

  public HttpStatusException(HttpStatus status, String message, Throwable cause) {
    super(message, cause);
    this.status = status;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public <T> ResponseEntity<T> toResponseEntity() {
    return new ResponseEntity<>(status);
  }

}
