package org.jrestful.business.support.user;

import org.jrestful.business.support.AuthUserService;
import org.jrestful.business.support.GenericSequencedDocumentService;
import org.jrestful.data.documents.support.user.GenericUser;

public interface GenericUserService<U extends GenericUser> extends GenericSequencedDocumentService<U>, AuthUserService<U, String> {

}
