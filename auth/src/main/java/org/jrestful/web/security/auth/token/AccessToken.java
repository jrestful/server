package org.jrestful.web.security.auth.token;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessToken extends Token {

  @JsonCreator
  public AccessToken(@JsonProperty("userId") String userId, @JsonProperty("expirationDate") Date expirationDate) {
    super(userId, expirationDate);
  }

  @Override
  public boolean isValid(Date now) {
    return StringUtils.isNotBlank(userId) //
        && expirationDate != null && !expirationDate.before(now);
  }

}
