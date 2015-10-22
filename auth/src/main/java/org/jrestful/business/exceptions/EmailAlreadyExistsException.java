package org.jrestful.business.exceptions;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends HttpStatusException {

  private static final long serialVersionUID = 1L;

  public EmailAlreadyExistsException() {
    super(HttpStatus.CONFLICT);
  }

}
