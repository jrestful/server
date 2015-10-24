package org.jrestful.tests.components.user;

import org.apache.commons.lang3.StringUtils;
import org.jrestful.business.exceptions.HttpStatusException;
import org.jrestful.business.support.GenericUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends GenericUserServiceImpl<UserRepository, User> implements UserService {

  @Autowired
  public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
    super(repository, passwordEncoder);
  }

  @Override
  public void validatePayload(User payload) throws HttpStatusException {
    super.validatePayload(payload);
    payload.setCity(StringUtils.trim(payload.getCity()));
    if (StringUtils.isEmpty(payload.getCity())) {
      throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User city cannot be empty");
    }
  }

}
