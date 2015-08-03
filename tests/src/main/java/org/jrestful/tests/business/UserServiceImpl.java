package org.jrestful.tests.business;

import org.jrestful.business.support.user.GenericUserServiceImpl;
import org.jrestful.tests.data.documents.User;
import org.jrestful.tests.data.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends GenericUserServiceImpl<UserRepository, User> implements UserService {

  @Autowired
  public UserServiceImpl(UserRepository repository) {
    super(repository);
  }

}
