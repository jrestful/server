package org.jrestful.tests.components.user;

import org.apache.commons.lang3.StringUtils;
import org.jrestful.business.support.user.GenericUserServiceImpl;
import org.jrestful.web.security.auth.exceptions.SignUpException;
import org.jrestful.web.security.auth.exceptions.UserDataNotValidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends GenericUserServiceImpl<UserRepository, User> implements UserService {

  @Autowired
  public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
    super(repository, passwordEncoder);
  }
  
  @Override
  protected void validate(User user) throws SignUpException {
    super.validate(user);
    if (StringUtils.isBlank(user.getCity())) {
      throw new UserDataNotValidException();
    }
  }

}
