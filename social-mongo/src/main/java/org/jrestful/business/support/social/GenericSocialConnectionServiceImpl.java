package org.jrestful.business.support.social;

import java.util.List;

import org.jrestful.business.support.GenericDocumentServiceImpl;
import org.jrestful.data.documents.support.social.GenericSocialConnection;
import org.jrestful.data.documents.support.social.GenericSocialUser;
import org.jrestful.data.repositories.support.social.GenericSocialConnectionRepository;

public abstract class GenericSocialConnectionServiceImpl<R extends GenericSocialConnectionRepository<C, U>, C extends GenericSocialConnection<U>, U extends GenericSocialUser>
    extends GenericDocumentServiceImpl<R, C> implements GenericSocialConnectionService<C, U> {

  public GenericSocialConnectionServiceImpl(R repository) {
    super(repository);
  }

  @Override
  public final List<C> findAllByUser(U user) {
    return repository.findAllByUser(user);
  }

  @Override
  public final List<C> findAllByUserAndProviderIdOrderByCreationDateAsc(U user, String providerId) {
    return repository.findAllByUserAndProviderIdOrderByCreationDateAsc(user, providerId);
  }

  @Override
  public final C findOneByUserAndProviderIdAndProviderUserId(U user, String providerId, String providerUserId) {
    return repository.findOneByUserAndProviderIdAndProviderUserId(user, providerId, providerUserId);
  }

  @Override
  public final List<C> findAllByProviderIdAndProviderUserId(String providerId, String providerUserId) {
    return repository.findAllByProviderIdAndProviderUserId(providerId, providerUserId);
  }

}
