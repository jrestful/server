package org.jrestful.business.support;

import java.io.Serializable;

import org.jrestful.business.exceptions.HttpStatusException;
import org.jrestful.data.documents.support.GenericAuthUser;

public interface GenericAuthUserService<U extends GenericAuthUser<K>, K extends Serializable> {

  U findOne(K id);

  U findOneByEmail(String email);

  U signUp(U user) throws HttpStatusException;

}