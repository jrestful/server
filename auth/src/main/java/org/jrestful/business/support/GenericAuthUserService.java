package org.jrestful.business.support;

import java.io.Serializable;

import org.jrestful.business.exceptions.HttpStatusException;

public interface GenericAuthUserService<U extends GenericAuthUser<K>, K extends Serializable> {

  U findOne(K id);

  U findOneByEmail(String email);

  U findOneByRefreshToken(String refreshToken);

  U signUp(U user) throws HttpStatusException;

  U confirm(String token) throws HttpStatusException;

  void persistRefreshToken(K userId, String refreshToken);

}
