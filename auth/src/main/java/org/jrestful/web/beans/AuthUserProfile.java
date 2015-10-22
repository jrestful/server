package org.jrestful.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jrestful.data.documents.support.AuthUser;

public class AuthUserProfile<U extends AuthUser<K>, K extends Serializable> {

  private K id;

  private String name;

  private String email;

  private List<String> roles = new ArrayList<>();

  private boolean anonymous = true;

  public AuthUserProfile(U user) {
    if (user != null) {
      id = user.getId();
      name = user.getName();
      email = user.getEmail();
      roles = user.getRoles();
      anonymous = false;
    }
  }

  public K getId() {
    return id;
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
