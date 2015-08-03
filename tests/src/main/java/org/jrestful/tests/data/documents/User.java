package org.jrestful.tests.data.documents;

import java.util.ArrayList;
import java.util.List;

import org.jrestful.data.documents.support.user.GenericUser;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User extends GenericUser {

  private static final long serialVersionUID = 1L;

  private String name;
  private String email;
  private String password;
  private List<String> roles = new ArrayList<>();
  private boolean accountExpired;
  private boolean accountLocked;
  private boolean passwordExpired;
  private boolean enabled;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public boolean isAccountExpired() {
    return accountExpired;
  }

  public void setAccountExpired(boolean accountExpired) {
    this.accountExpired = accountExpired;
  }

  public boolean isAccountLocked() {
    return accountLocked;
  }

  public void setAccountLocked(boolean accountLocked) {
    this.accountLocked = accountLocked;
  }

  public boolean isPasswordExpired() {
    return passwordExpired;
  }

  public void setPasswordExpired(boolean passwordExpired) {
    this.passwordExpired = passwordExpired;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

}
