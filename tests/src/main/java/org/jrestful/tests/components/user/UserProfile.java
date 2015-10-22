package org.jrestful.tests.components.user;

import org.jrestful.web.beans.AuthUserProfile;

public class UserProfile extends AuthUserProfile<User, String> {

  private String city;

  public UserProfile(User user) {
    super(user);
    if (user != null) {
      city = user.getCity();
    }
  }

  public String getCity() {
    return city;
  }

}
