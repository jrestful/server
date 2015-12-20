package org.jrestful.data.documents;

import org.jrestful.data.documents.support.GenericDocument;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usersTokens")
@CompoundIndex(name = "unique_userId_type", def = "{'userId': 1, 'type': 1}", unique = true)
public class UserToken extends GenericDocument {

  private static final long serialVersionUID = 1L;

  public enum Type {

    SIGN_UP_EMAIL_CONFIRMATION;

  }

  private String userId;

  private Type type;

  @Indexed(name = "unique_token", unique = true)
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
