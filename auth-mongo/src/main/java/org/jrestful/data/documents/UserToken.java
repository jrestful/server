package org.jrestful.data.documents;

import org.jrestful.data.documents.support.GenericDocument;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usersTokens")
// @formatter:off
@CompoundIndexes({ //
  @CompoundIndex(name = "unique_userId_type", def = "{'userId': 1, 'type': 1}", unique = true), //
  @CompoundIndex(name = "unique_type_token", def = "{'type': 1, 'token': 1}", unique = true) //
})
//@formatter:on
public class UserToken extends GenericDocument {

  private static final long serialVersionUID = 1L;

  public enum Type {

    SIGN_UP_EMAIL_CONFIRMATION,
    REFRESH_TOKEN;

  }

  private String userId;

  private Type type;

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
