package org.jrestful.data.documents.support.user;

import java.util.ArrayList;
import java.util.List;

import org.jrestful.data.documents.support.sequence.GenericSequencedDocument;
import org.jrestful.web.security.auth.user.AuthUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public abstract class GenericUser extends GenericSequencedDocument implements AuthUser<String> {

  private static final long serialVersionUID = 1L;

  private String name;

  private String email;

  private String password;

  private List<String> roles = new ArrayList<>();

  private boolean enabled;

  private boolean accountLocked;

  private boolean accountExpired;

  private boolean passwordExpired;

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonIgnore
  @Override
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @JsonIgnore
  @Override
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  @JsonIgnore
  @Override
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @JsonIgnore
  @Override
  public boolean isAccountLocked() {
    return accountLocked;
  }

  public void setAccountLocked(boolean accountLocked) {
    this.accountLocked = accountLocked;
  }

  @JsonIgnore
  @Override
  public boolean isAccountExpired() {
    return accountExpired;
  }

  public void setAccountExpired(boolean accountExpired) {
    this.accountExpired = accountExpired;
  }

  @JsonIgnore
  @Override
  public boolean isPasswordExpired() {
    return passwordExpired;
  }

  public void setPasswordExpired(boolean passwordExpired) {
    this.passwordExpired = passwordExpired;
  }

  @JsonIgnore
  @Override
  public final String getUsername() {
    return getId();
  }

  @JsonIgnore
  @Override
  public final List<GrantedAuthority> getAuthorities() {
    return Lists.transform(getRoles(), new Function<String, GrantedAuthority>() {

      @Override
      public SimpleGrantedAuthority apply(String input) {
        return new SimpleGrantedAuthority(input);
      }

    });
  }

  @JsonIgnore
  @Override
  public final boolean isAccountNonExpired() {
    return !isAccountExpired();
  }

  @JsonIgnore
  @Override
  public final boolean isAccountNonLocked() {
    return !isAccountLocked();
  }

  @JsonIgnore
  @Override
  public final boolean isCredentialsNonExpired() {
    return !isPasswordExpired();
  }

}
