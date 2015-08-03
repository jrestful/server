package org.jrestful.tests.data.documents;

import org.jrestful.data.documents.support.user.GenericUser;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User extends GenericUser {

  private static final long serialVersionUID = 1L;

}
