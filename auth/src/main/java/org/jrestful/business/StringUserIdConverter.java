package org.jrestful.business;

import org.jrestful.business.support.UserIdConverter;
import org.springframework.stereotype.Component;

@Component
public class StringUserIdConverter implements UserIdConverter<String> {

  @Override
  public String convert(String id) {
    return id;
  }

}
