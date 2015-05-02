package org.jrestful.business.support.user;

import org.jrestful.business.support.sequence.GenericSequencedDocumentServiceImpl;
import org.jrestful.data.documents.support.user.GenericUser;
import org.jrestful.data.repositories.support.user.GenericUserRepository;

public abstract class GenericUserServiceImpl<R extends GenericUserRepository<U>, U extends GenericUser> extends
    GenericSequencedDocumentServiceImpl<R, U> implements GenericUserService<U> {

  public GenericUserServiceImpl(R repository) {
    super(repository);
  }

  @Override
  public final U findByEmail(String email) {
    return repository.findByEmail(email);
  }

}
