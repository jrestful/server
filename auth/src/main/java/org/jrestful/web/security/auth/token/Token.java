package org.jrestful.web.security.auth.token;

import java.util.Date;

public abstract class Token {

  protected final Date expirationDate;
  
  public Token(Date expirationDate) {
    this.expirationDate = expirationDate;
  }

  public Date getExpirationDate() {
    return expirationDate;
  }

  public abstract boolean isSyntacticallyValid();

  public abstract boolean isNotExpired(Date now);

}
