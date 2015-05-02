package org.jrestful.web.security.auth.user;

import org.springframework.stereotype.Component;

@Component
public class StringUserIdConverter implements UserIdConverter<String> {

  @Override
  public String convert(String id) {
    return id;
  }

}
