package org.jrestful.web.security.auth.social;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.jrestful.web.security.auth.social.connection.SocialAuthConnection;
import org.jrestful.web.security.auth.social.connection.SocialAuthConnectionService;
import org.jrestful.web.security.auth.social.user.SocialAuthUser;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class UserConnectionsRepository<C extends SocialAuthConnection<U>, U extends SocialAuthUser<?>> implements ConnectionRepository {

  private final U user;

  private final SocialAuthConnectionService<C, U> connectionService;

  private final ConnectionFactoryLocator connectionFactoryLocator;

  public UserConnectionsRepository(U user, SocialAuthConnectionService<C, U> connectionService, ConnectionFactoryLocator connectionFactoryLocator) {
    this.user = user;
    this.connectionService = connectionService;
    this.connectionFactoryLocator = connectionFactoryLocator;
  }

  @Override
  public MultiValueMap<String, Connection<?>> findAllConnections() {
    MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<>();
    for (C socialConnection : connectionService.findAllByUser(user)) {
      ConnectionData connectionData = toConnectionData(socialConnection);
      Connection<?> connection = toConnection(connectionData);
      connections.add(socialConnection.getProviderId(), connection);
    }
    return connections;
  }

  @Override
  public List<Connection<?>> findConnections(String providerId) {
    List<Connection<?>> connections = new ArrayList<>();
    for (C socialConnection : connectionService.findAllByUserAndProviderIdOrderByCreationDateAsc(user, providerId)) {
      ConnectionData connectionData = toConnectionData(socialConnection);
      Connection<?> connection = toConnection(connectionData);
      connections.add(connection);
    }
    return connections;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <A> List<Connection<A>> findConnections(Class<A> apiType) {
    List<?> connections = findConnections(toProviderId(apiType));
    return (List<Connection<A>>) connections;
  }

  @Override
  public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUserIds) {
    MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<>();
    for (Entry<String, List<String>> entry : providerUserIds.entrySet()) {
      String providerId = entry.getKey();
      for (String providerUserId : entry.getValue()) {
        C socialConnection = connectionService.findOneByUserAndProviderIdAndProviderUserId(user, providerId, providerUserId);
        if (socialConnection != null) {
          ConnectionData connectionData = toConnectionData(socialConnection);
          Connection<?> connection = toConnection(connectionData);
          connections.add(providerId, connection);
        }
      }
    }
    return connections;
  }

  @Override
  public Connection<?> getConnection(ConnectionKey connectionKey) {
    String providerId = connectionKey.getProviderId();
    String providerUserId = connectionKey.getProviderUserId();
    C socialConnection = connectionService.findOneByUserAndProviderIdAndProviderUserId(user, providerId, providerUserId);
    if (socialConnection == null) {
      throw new NoSuchConnectionException(connectionKey);
    } else {
      ConnectionData connectionData = toConnectionData(socialConnection);
      return toConnection(connectionData);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
    return (Connection<A>) getConnection(new ConnectionKey(toProviderId(apiType), providerUserId));
  }

  @Override
  public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
    List<Connection<A>> connections = findConnections(apiType);
    return connections.isEmpty() ? null : connections.get(0);
  }

  @Override
  public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
    return getPrimaryConnection(apiType);
  }

  @Override
  public void addConnection(Connection<?> connection) {
    C socialConnection = connectionService.newInstance();
    fromConnectionData(socialConnection, connection.createData());
    socialConnection.setUser(user);
    socialConnection.setCreationDate(new Date());
    connectionService.insert(socialConnection);
  }

  @Override
  public void updateConnection(Connection<?> connection) {
    ConnectionData connectionData = connection.createData();
    String providerId = connectionData.getProviderId();
    String providerUserId = connectionData.getProviderUserId();
    C socialConnection = connectionService.findOneByUserAndProviderIdAndProviderUserId(user, providerId, providerUserId);
    if (socialConnection != null) {
      fromConnectionData(socialConnection, connectionData);
      connectionService.save(socialConnection);
    }
  }

  @Override
  public void removeConnections(String providerId) {
    List<C> socialConnections = connectionService.findAllByUserAndProviderIdOrderByCreationDateAsc(user, providerId);
    connectionService.delete(socialConnections);
  }

  @Override
  public void removeConnection(ConnectionKey connectionKey) {
    String providerId = connectionKey.getProviderId();
    String providerUserId = connectionKey.getProviderUserId();
    C socialConnection = connectionService.findOneByUserAndProviderIdAndProviderUserId(user, providerId, providerUserId);
    if (socialConnection != null) {
      connectionService.delete(socialConnection);
    }
  }

  private String toProviderId(Class<?> apiType) {
    return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
  }

  private Connection<?> toConnection(ConnectionData connectionData) {
    return connectionFactoryLocator.getConnectionFactory(connectionData.getProviderId()).createConnection(connectionData);
  }

  public static ConnectionData toConnectionData(SocialAuthConnection<?> connection) {
    return new ConnectionData(connection.getProviderId(), connection.getProviderUserId(), connection.getDisplayName(), connection.getProfileUrl(),
        connection.getImageUrl(), connection.getAccessToken(), connection.getSecret(), connection.getRefreshToken(), connection.getExpireTime());
  }

  public static void fromConnectionData(SocialAuthConnection<?> connection, ConnectionData connectionData) {
    connection.setProviderId(connectionData.getProviderId());
    connection.setProviderUserId(connectionData.getProviderUserId());
    connection.setDisplayName(connectionData.getDisplayName());
    connection.setProfileUrl(connectionData.getProfileUrl());
    connection.setImageUrl(connectionData.getImageUrl());
    connection.setAccessToken(connectionData.getAccessToken());
    connection.setSecret(connectionData.getSecret());
    connection.setRefreshToken(connectionData.getRefreshToken());
    connection.setExpireTime(connectionData.getExpireTime());
  }

}
