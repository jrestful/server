package org.jrestful.business;

import org.jrestful.business.support.GenericDocumentService;
import org.jrestful.data.documents.UserToken;
import org.jrestful.data.documents.UserToken.Type;
import org.jrestful.data.documents.support.GenericUser;

public interface UserTokenService extends GenericDocumentService<UserToken> {

  UserToken findOneByToken(String token);

  UserToken findOneByTypeAndToken(Type type, String token);

  <U extends GenericUser> UserToken createAndSave(U user, UserToken.Type type, String alphabet, int length);

  int deleteByUserId(String userId);

  int deleteByUserIdAndType(String userId, Type type);

}
