package org.jrestful.web.security.auth.social;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jrestful.web.security.auth.social.connection.SocialAuthConnection;
import org.jrestful.web.security.auth.social.connection.SocialAuthConnectionService;
import org.jrestful.web.security.auth.social.user.SocialAuthUser;
import org.jrestful.web.security.auth.social.user.SocialAuthUserService;
import org.jrestful.web.security.auth.user.UserIdConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.stereotype.Component;

@Component
public class UsersConnectionsRepository<C extends SocialAuthConnection<U>, U extends SocialAuthUser<K>, K extends Serializable> implements
    UsersConnectionRepository {

  private final SocialAuthUserService<U, K> userService;

  private final UserIdConverter<K> userIdConverter;

  private final SocialAuthConnectionService<C, U> connectionService;

  private final ConnectionSignUp connectionSignUp;

  private final ConnectionFactoryLocator connectionFactoryLocator;

  @Autowired
  public UsersConnectionsRepository(SocialAuthUserService<U, K> userService, UserIdConverter<K> userIdConverter,
      SocialAuthConnectionService<C, U> connectionService, ConnectionSignUp connectionSignUp, ConnectionFactoryLocator connectionFactoryLocator) {
    this.userService = userService;
    this.userIdConverter = userIdConverter;
    this.connectionService = connectionService;
    this.connectionSignUp = connectionSignUp;
    this.connectionFactoryLocator = connectionFactoryLocator;
  }

  @Override
  public List<String> findUserIdsWithConnection(Connection<?> connection) {
    ConnectionData connectionData = connection.createData();
    String providerId = connectionData.getProviderId();
    String providerUserId = connectionData.getProviderUserId();
    List<String> userIds = new ArrayList<>();
    List<C> socialConnections = connectionService.findAllByProviderIdAndProviderUserId(providerId, providerUserId);
    if (socialConnections.isEmpty()) {
      String userId = connectionSignUp.execute(connection);
      createConnectionRepository(userId).addConnection(connection);
      userIds.add(userId);
    } else {
      for (C socialConnection : socialConnections) {
        userIds.add(socialConnection.getUser().getId().toString());
      }
    }
    return userIds;
  }

  @Override
  public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
    Set<String> userIds = new HashSet<>();
    for (String providerUserId : providerUserIds) {
      for (C socialConnection : connectionService.findAllByProviderIdAndProviderUserId(providerId, providerUserId)) {
        userIds.add(socialConnection.getUser().getId().toString());
      }
    }
    return userIds;
  }

  @Override
  public ConnectionRepository createConnectionRepository(String userId) {
    return new UserConnectionsRepository<>(userService.findOne(userIdConverter.convert(userId)), connectionService, connectionFactoryLocator);
  }

}
