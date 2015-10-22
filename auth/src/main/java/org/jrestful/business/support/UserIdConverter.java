package org.jrestful.business.support;

import java.io.Serializable;

public interface UserIdConverter<K extends Serializable> {

  K convert(String id);

}
