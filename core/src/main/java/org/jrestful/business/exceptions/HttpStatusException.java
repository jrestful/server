package org.jrestful.business.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HttpStatusException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final HttpStatus status;

  public HttpStatusException(HttpStatus status) {
    this(status, status.toString());
  }

  public HttpStatusException(HttpStatus status, String message) {
    super(message);
    this.status = status;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public ResponseEntity<?> toResponseEntity() {
    return new ResponseEntity<>(getMessage(), status);
  }

}
