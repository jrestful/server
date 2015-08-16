package org.jrestful.web.security.auth.user;

import java.io.Serializable;

public interface AuthUserService<U extends AuthUser<K>, K extends Serializable> {

  U findOne(K id);

  U findOneByEmail(String email);

}
