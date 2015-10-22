package org.jrestful.tests.components.user;

import org.jrestful.web.controllers.rest.GenericAuthRestController;
import org.jrestful.web.hateoas.RestResource;
import org.jrestful.web.security.auth.user.AuthUserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api-${app.apiVersion}", produces = RestResource.HAL_MEDIA_TYPE)
public class AuthRestController extends GenericAuthRestController<UserService, User, String> {

  @Autowired
  public AuthRestController(UserService service) {
    super(service);
  }

  @Override
  protected AuthUserProfile<User, String> createUserProfile(User user) {
    return new UserProfile(user);
  }

}
