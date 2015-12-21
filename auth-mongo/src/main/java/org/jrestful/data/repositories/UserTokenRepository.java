package org.jrestful.data.repositories;

import org.jrestful.data.documents.UserToken;
import org.jrestful.data.documents.UserToken.Type;
import org.jrestful.data.repositories.support.GenericDocumentRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UserTokenRepository extends GenericDocumentRepository<UserToken> {

  // TODO use findOneByTypeAndToken instead
  UserToken findOneByToken(String token);

  UserToken findOneByTypeAndToken(Type type, String token);

  int deleteByUserId(String userId);

  int deleteByUserIdAndType(String userId, Type type);

}
