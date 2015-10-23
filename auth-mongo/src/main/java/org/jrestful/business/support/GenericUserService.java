package org.jrestful.business.support;

import org.jrestful.business.support.GenericAuthUserService;
import org.jrestful.business.support.GenericSequencedDocumentService;
import org.jrestful.data.documents.support.GenericUser;

public interface GenericUserService<U extends GenericUser> extends GenericSequencedDocumentService<U>, GenericAuthUserService<U, String> {

}
