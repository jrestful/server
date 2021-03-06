package org.jrestful.web.security.auth.token;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessToken extends Token {

  private final String userId;

  @JsonCreator
  public AccessToken(@JsonProperty("userId") String userId, @JsonProperty("expirationDate") Date expirationDate) {
    super(expirationDate);
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }

  @Override
  @JsonIgnore
  public boolean isSyntacticallyValid() {
    return StringUtils.isNotBlank(userId) && expirationDate != null;
  }

  @Override
  @JsonIgnore
  public boolean isNotExpired(Date now) {
    return !expirationDate.before(now);
  }

}
