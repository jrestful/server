package org.jrestful.data.repositories.support;

import org.jrestful.data.documents.support.GenericUser;
import org.jrestful.data.repositories.support.GenericSequencedDocumentRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GenericUserRepository<U extends GenericUser> extends GenericSequencedDocumentRepository<U> {

  U findOneByEmail(String email);

}
