package org.jrestful.business.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HttpStatusException extends Exception {

  private static final long serialVersionUID = 1L;

  private final HttpStatus status;

  public HttpStatusException(HttpStatus status) {
    super(status.toString());
    this.status = status;
  }

  public <T> ResponseEntity<T> build() {
    return new ResponseEntity<>(status);
  }

}
