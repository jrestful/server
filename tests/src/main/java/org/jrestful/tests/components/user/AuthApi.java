package org.jrestful.tests.components.user;

import org.jrestful.web.beans.AuthUserProfile;
import org.jrestful.web.beans.RestResource;
import org.jrestful.web.controllers.rest.support.GenericAuthApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v${app.apiVersion}/auth", produces = RestResource.HAL_MEDIA_TYPE)
public class AuthApi extends GenericAuthApi<UserService, User, String> {

  @Autowired
  public AuthApi(UserService service) {
    super(service);
  }

  @Override
  protected AuthUserProfile<User, String> createUserProfile(User user) {
    return new UserProfile(user);
  }

}
