package org.jrestful.data.documents;

import org.jrestful.data.documents.support.GenericDocument;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "userTokens")
public class UserToken extends GenericDocument {

  private static final long serialVersionUID = 1L;

  public enum Type {

    EMAIL_CONFIRMATION;

  }

  private String userId;

  private Type type;

  @Indexed(unique = true)
  private String token;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

}
