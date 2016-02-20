package org.jrestful.web.security.auth.social.user;

import java.io.Serializable;

import org.jrestful.web.security.auth.user.AuthUserService;
import org.springframework.social.connect.UserProfile;

public interface SocialAuthUserService<U extends SocialAuthUser<K>, K extends Serializable> extends AuthUserService<U, K> {

  U create(UserProfile userProfile);

}
