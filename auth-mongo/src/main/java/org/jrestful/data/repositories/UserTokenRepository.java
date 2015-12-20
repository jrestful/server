package org.jrestful.data.repositories;

import org.jrestful.data.documents.UserToken;
import org.jrestful.data.repositories.support.GenericDocumentRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UserTokenRepository extends GenericDocumentRepository<UserToken> {

  UserToken findOneByToken(String token);

  int deleteByUserId(String userId);

}
