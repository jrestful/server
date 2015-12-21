package org.jrestful.data.repositories;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.jrestful.data.documents.UserToken;
import org.jrestful.data.documents.UserToken.Type;
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

  @Override
  public UserToken findOneByTypeAndToken(Type type, String token) {
    return mongoOperations.findOne(query(where("type").is(type).and("token").is(token)), documentClass);
  }

  @Override
  public int deleteByUserId(String userId) {
    return mongoOperations.remove(query(where("userId").is(userId)), documentClass).getN();
  }

  @Override
  public int deleteByUserIdAndType(String userId, Type type) {
    return mongoOperations.remove(query(where("userId").is(userId).and("type").is(type)), documentClass).getN();
  }

}
