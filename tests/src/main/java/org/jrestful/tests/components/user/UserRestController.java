package org.jrestful.tests.components.user;

import org.jrestful.web.controllers.rest.support.GenericUserRestController;
import org.jrestful.web.hateoas.RestResource;
import org.jrestful.web.security.auth.user.AuthUserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api-${app.apiVersion}/rest/users", produces = RestResource.HAL_MEDIA_TYPE)
public class UserRestController extends GenericUserRestController<UserService, User> {

  @Autowired
  public UserRestController(UserService service) {
    super(service);
  }

  @Override
  protected AuthUserProfile<User, String> createUserProfile(User user) {
    return new UserProfile(user);
  }

}
