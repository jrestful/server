package org.jrestful.web.security.auth.user;

import java.io.Serializable;

public interface UserIdConverter<K extends Serializable> {

  K convert(String id);

}
