package org.jrestful.data.repositories.support.user;

import org.jrestful.data.documents.support.user.GenericUser;
import org.jrestful.data.repositories.support.GenericSequencedDocumentRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GenericUserRepository<U extends GenericUser> extends GenericSequencedDocumentRepository<U> {

  U findOneByEmail(String email);

}
