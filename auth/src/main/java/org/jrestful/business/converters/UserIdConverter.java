package org.jrestful.business.converters;

import java.io.Serializable;

public interface UserIdConverter<K extends Serializable> {

  K convert(String id);

}
