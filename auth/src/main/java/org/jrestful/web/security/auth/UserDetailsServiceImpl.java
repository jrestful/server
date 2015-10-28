package org.jrestful.web.security.auth;

import java.io.Serializable;

import org.jrestful.business.converters.UserIdConverter;
import org.jrestful.business.support.GenericAuthUser;
import org.jrestful.business.support.GenericAuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class UserDetailsServiceImpl<U extends GenericAuthUser<K>, K extends Serializable> implements UserDetailsService {

  private final GenericAuthUserService<U, K> userService;

  private final UserIdConverter<K> userIdConverter;

  private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

  @Autowired
  public UserDetailsServiceImpl(GenericAuthUserService<U, K> userService, UserIdConverter<K> userIdConverter) {
    this.userService = userService;
    this.userIdConverter = userIdConverter;
  }

  @Override
  public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
    if (id == null) {
      throw new UsernameNotFoundException("Trying to load a user from a null id");
    }
    U user = userService.findOne(userIdConverter.convert(id));
    if (user == null) {
      throw new UsernameNotFoundException("No user found with id " + id);
    }
    detailsChecker.check(user);
    return user;
  }

}