package org.jrestful.web.beans;

import java.util.ArrayList;
import java.util.List;

import org.jrestful.web.security.auth.user.AuthUser;

public class UserProfile {

  private final String name;

  private final String email;

  private final List<String> roles;

  private final boolean anonymous;

  public UserProfile() {
    name = "";
    email = "";
    roles = new ArrayList<>();
    anonymous = true;
  }

  public UserProfile(AuthUser<?> user) {
    name = user.getName();
    email = user.getEmail();
    roles = user.getRoles();
    anonymous = false;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public List<String> getRoles() {
    return roles;
  }

  public boolean isAnonymous() {
    return anonymous;
  }

}
