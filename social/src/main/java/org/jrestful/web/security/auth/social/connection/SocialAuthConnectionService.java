package org.jrestful.web.security.auth.social.connection;

import java.util.List;

import org.jrestful.web.security.auth.social.user.SocialAuthUser;

public interface SocialAuthConnectionService<C extends SocialAuthConnection<U>, U extends SocialAuthUser<?>> {

  List<C> findAllByUser(U user);

  List<C> findAllByUserAndProviderIdOrderByCreationDateAsc(U user, String providerId);

  C findOneByUserAndProviderIdAndProviderUserId(U user, String providerId, String providerUserId);

  C insert(C connection);

  C save(C userConnection);

  void delete(Iterable<C> userConnections);

  void delete(C userConnection);

  C newInstance();

  List<C> findAllByProviderIdAndProviderUserId(String providerId, String providerUserId);

}
