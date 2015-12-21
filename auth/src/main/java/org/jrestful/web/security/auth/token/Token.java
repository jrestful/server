package org.jrestful.web.security.auth.token;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Token {

  protected final Date expirationDate;
  
  public Token(Date expirationDate) {
    this.expirationDate = expirationDate;
  }

  public Date getExpirationDate() {
    return expirationDate;
  }

  @JsonIgnore
  public boolean isValid() {
    return isValid(new Date());
  }
  
  protected abstract boolean isValid(Date now);

}
