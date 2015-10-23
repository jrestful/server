package org.jrestful.data.documents.support;

import java.io.Serializable;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

public interface GenericAuthUser<K extends Serializable> extends UserDetails {

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
