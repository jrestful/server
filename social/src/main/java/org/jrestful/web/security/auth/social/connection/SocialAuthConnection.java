package org.jrestful.web.security.auth.social.connection;

import java.util.Date;

import org.jrestful.web.security.auth.social.user.SocialAuthUser;

public interface SocialAuthConnection<U extends SocialAuthUser<?>> {

  U getUser();

  void setUser(U user);

  String getProviderId();

  void setProviderId(String providerId);

  String getProviderUserId();

  void setProviderUserId(String providerUserId);

  String getDisplayName();

  void setDisplayName(String displayName);

  String getProfileUrl();

  void setProfileUrl(String profileUrl);

  String getImageUrl();

  void setImageUrl(String imageUrl);

  String getAccessToken();

  void setAccessToken(String accessToken);

  String getSecret();

  void setSecret(String secret);

  String getRefreshToken();

  void setRefreshToken(String refreshToken);

  Long getExpireTime();

  void setExpireTime(Long expireTime);

  Date getCreationDate();

  void setCreationDate(Date creationDate);

}
