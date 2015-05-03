package org.jrestful.web.security.auth.token;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Token {

  private final String id;

  private final Date expirationDate;

  @JsonCreator
  public Token(@JsonProperty("id") String id, @JsonProperty("expirationDate") Date expirationDate) {
    this.id = id;
    this.expirationDate = expirationDate;
  }

  public String getId() {
    return id;
  }

  public Date getExpirationDate() {
    return expirationDate;
  }

  @JsonIgnore
  public boolean isValid() {
    return id != null && expirationDate != null && new Date().before(expirationDate);
  }

  @Override
  public String toString() {
    return id.toString();
  }

}
