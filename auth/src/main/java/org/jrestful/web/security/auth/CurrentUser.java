package org.jrestful.web.security.auth;

import org.jrestful.business.support.GenericAuthUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUser {

  public static boolean isAnonymous() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication == null || authentication instanceof AnonymousAuthenticationToken || !authentication.isAuthenticated();
  }

  public static boolean isAuthenticated() {
    return !isAnonymous();
  }

  @SuppressWarnings("unchecked")
  public static <U extends GenericAuthUser<?>> U get(Class<U> type) {
    if (isAnonymous()) {
      return null;
    } else {
      return (U) SecurityContextHolder.getContext().getAuthentication().getDetails();
    }
  }

  public static <U extends GenericAuthUser<?>> U get() {
    return get(null);
  }

  private CurrentUser() {
    // utility class
  }

}
