package org.jrestful.data.repositories.support.social;

import java.util.List;

import org.jrestful.data.documents.support.social.GenericSocialConnection;
import org.jrestful.data.documents.support.social.GenericSocialUser;
import org.jrestful.data.repositories.support.GenericDocumentRepository;

public interface GenericSocialConnectionRepository<C extends GenericSocialConnection<U>, U extends GenericSocialUser> extends
    GenericDocumentRepository<C> {

  List<C> findAllByUser(U user);

  List<C> findAllByUserAndProviderIdOrderByCreationDateAsc(U user, String providerId);

  C findOneByUserAndProviderIdAndProviderUserId(U user, String providerId, String providerUserId);

  List<C> findAllByProviderIdAndProviderUserId(String providerId, String providerUserId);

}
