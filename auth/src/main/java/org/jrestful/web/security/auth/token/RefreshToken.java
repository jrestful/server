package org.jrestful.web.security.auth.token;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RefreshToken extends Token {
  
  private final Date availabilityDate;

  @JsonCreator
  public RefreshToken(@JsonProperty("availabilityDate") Date availabilityDate, @JsonProperty("expirationDate") Date expirationDate) {
    super(expirationDate);
    this.availabilityDate = availabilityDate;
  }
  
  public Date getAvailabilityDate() {
    return availabilityDate;
  }

  @Override
  @JsonIgnore
  public boolean isSyntacticallyValid() {
    return availabilityDate != null;
  }

  @JsonIgnore
  public boolean isAvailable(Date now) {
    return !availabilityDate.after(now);
  }

  @Override
  @JsonIgnore
  public boolean isNotExpired(Date now) {
    return expirationDate == null || !expirationDate.before(now);
  }

}
