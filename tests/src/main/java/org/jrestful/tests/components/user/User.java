package org.jrestful.tests.components.user;

import org.jrestful.data.documents.support.GenericUser;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User extends GenericUser {

  private static final long serialVersionUID = 1L;

  private String city;

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

}
