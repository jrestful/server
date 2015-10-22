package org.jrestful.web.security.auth.user;

import java.io.Serializable;

import org.jrestful.web.security.auth.exceptions.SignUpException;

public interface AuthUserService<U extends AuthUser<K>, K extends Serializable> {

  U findOne(K id);

  U findOneByEmail(String email);
  
  U signUp(U user) throws SignUpException;

}
