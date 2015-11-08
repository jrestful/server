package org.jrestful.business;

import org.jrestful.business.support.GenericDocumentService;
import org.jrestful.data.documents.UserToken;
import org.jrestful.data.documents.support.GenericUser;

public interface UserTokenService extends GenericDocumentService<UserToken> {

  <U extends GenericUser> UserToken createAndSave(U user, UserToken.Type type, String alphabet, int length);

}
