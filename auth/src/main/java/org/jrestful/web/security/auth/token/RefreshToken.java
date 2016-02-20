package org.jrestful.web.security.auth.token;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
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
  public boolean isValid(Date now) {
    return availabilityDate != null && !availabilityDate.after(now) //
        && (expirationDate == null || !expirationDate.before(now));
  }

}
