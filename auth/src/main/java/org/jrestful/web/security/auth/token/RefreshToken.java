package org.jrestful.web.security.auth.token;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RefreshToken extends Token {
  
  private final Date availabilityDate;

  @JsonCreator
  public RefreshToken(@JsonProperty("userId") String userId, @JsonProperty("availabilityDate") Date availabilityDate, @JsonProperty("expirationDate") Date expirationDate) {
    super(userId, expirationDate);
    this.availabilityDate = availabilityDate;
  }
  
  public Date getAvailabilityDate() {
    return availabilityDate;
  }

  @Override
  public boolean isValid(Date now) {
    return StringUtils.isNotBlank(userId) //
        && availabilityDate != null && !availabilityDate.after(now) //
        && (expirationDate == null || !expirationDate.before(now));
  }

}
