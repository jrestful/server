package org.jrestful.data.repositories.support.user;

import org.jrestful.data.documents.support.user.GenericUser;
import org.jrestful.data.repositories.support.sequence.GenericSequencedDocumentRepository;

public interface GenericUserRepository<U extends GenericUser> extends GenericSequencedDocumentRepository<U> {

  U findByEmail(String email);

}
