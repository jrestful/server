package org.jrestful.web.security.auth.social;

import java.io.Serializable;

import org.jrestful.web.security.auth.social.user.SocialAuthUser;
import org.jrestful.web.security.auth.social.user.SocialAuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class AutoConnectionSignUp<U extends SocialAuthUser<K>, K extends Serializable> implements ConnectionSignUp {

  private final SocialAuthUserService<U, K> userService;

  @Autowired
  public AutoConnectionSignUp(SocialAuthUserService<U, K> userService) {
    this.userService = userService;
  }

  @Override
  public String execute(Connection<?> connection) {
    UserProfile userProfile = connection.fetchUserProfile();
    return userService.create(userProfile).getId().toString();
  }

}
