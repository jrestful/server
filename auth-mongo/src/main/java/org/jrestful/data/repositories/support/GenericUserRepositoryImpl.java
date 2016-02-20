package org.jrestful.data.repositories.support;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.jrestful.data.documents.support.GenericUser;
import org.jrestful.data.repositories.support.GenericSequencedDocumentRepositoryImpl;
import org.springframework.data.mongodb.core.MongoOperations;

public abstract class GenericUserRepositoryImpl<U extends GenericUser> extends GenericSequencedDocumentRepositoryImpl<U> implements
    GenericUserRepository<U> {

  public GenericUserRepositoryImpl(Class<U> documentClass, MongoOperations mongoOperations) {
    super(documentClass, mongoOperations);
  }

  @Override
  public U findOneByEmail(String email) {
    return mongoOperations.findOne(query(where("email").is(email)), documentClass);
  }

}
