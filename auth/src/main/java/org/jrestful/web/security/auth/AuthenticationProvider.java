package org.jrestful.web.security.auth;

import java.io.Serializable;

import org.jrestful.business.support.GenericAuthUser;
import org.jrestful.business.support.GenericAuthUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationProvider<U extends GenericAuthUser<K>, K extends Serializable> extends DaoAuthenticationProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationProvider.class);

  private final GenericAuthUserService<U, K> userService;

  @Autowired
  public AuthenticationProvider(GenericAuthUserService<U, K> userService, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    this.userService = userService;
    setUserDetailsService(userDetailsService);
    setPasswordEncoder(passwordEncoder);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
      throws AuthenticationException {

    try {

      super.additionalAuthenticationChecks(userDetails, authentication);
      if (userDetails instanceof GenericAuthUser) {
        userService.clearTempPassword(((GenericAuthUser<K>) userDetails).getId());
      }

    } catch (AuthenticationException e) {

      if (userDetails instanceof GenericAuthUser) {
        GenericAuthUser<K> authUser = (GenericAuthUser<K>) userDetails;
        authUser.setPassword(authUser.getTempPassword());
        if (authUser.getPassword() != null) {

          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Trying to authenticate user " + authUser.getId() + " with temporary password");
          }
          super.additionalAuthenticationChecks(userDetails, authentication);
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("User " + authUser.getId() + " successfully authenticated with temporary password");
          }
          userService.replacePasswordByTempPassword(authUser.getId());

        } else {
          throw e;
        }
      } else {
        throw e;
      }

    }

  }

}
