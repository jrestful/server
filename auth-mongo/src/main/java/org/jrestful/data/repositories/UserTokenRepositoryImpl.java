package org.jrestful.data.repositories;

import org.jrestful.data.documents.UserToken;
import org.jrestful.data.repositories.support.GenericDocumentRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

@Repository
public class UserTokenRepositoryImpl extends GenericDocumentRepositoryImpl<UserToken> implements UserTokenRepository {

  @Autowired
  public UserTokenRepositoryImpl(MongoOperations mongoOperations) {
    super(UserToken.class, mongoOperations);
  }

}
