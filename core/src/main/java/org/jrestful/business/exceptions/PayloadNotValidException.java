package org.jrestful.business.exceptions;

import org.springframework.http.HttpStatus;

public class PayloadNotValidException extends HttpStatusException {

  private static final long serialVersionUID = 1L;

  public PayloadNotValidException() {
    super(HttpStatus.UNPROCESSABLE_ENTITY);
  }

}
