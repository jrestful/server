package org.jrestful.data.documents.support.user;

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
