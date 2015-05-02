package org.jrestful.web.security.auth.user;

import java.io.Serializable;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthUser<K extends Serializable> extends UserDetails {

  K getId();

  String getName();

  String getEmail();

  @Override
  String getPassword();

  List<String> getRoles();

  boolean isAccountExpired();

  boolean isAccountLocked();

  boolean isPasswordExpired();

  @Override
  boolean isEnabled();

}
