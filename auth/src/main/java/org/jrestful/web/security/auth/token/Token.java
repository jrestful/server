package org.jrestful.web.security.auth.token;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Token {

  protected final String userId;

  protected final Date expirationDate;
  
  public Token(String userId, Date expirationDate) {
    this.userId = userId;
    this.expirationDate = expirationDate;
  }

  public String getUserId() {
    return userId;
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
