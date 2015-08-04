package org.jrestful.business.support.user;

import org.jrestful.business.support.GenericSequencedDocumentService;
import org.jrestful.data.documents.support.user.GenericUser;
import org.jrestful.web.security.auth.user.AuthUserService;

public interface GenericUserService<U extends GenericUser> extends GenericSequencedDocumentService<U>, AuthUserService<U, String> {

  @Override
  U findOne(String id);

}
