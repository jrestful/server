package org.jrestful.web.security.auth.social.user;

import java.io.Serializable;

import org.jrestful.web.security.auth.user.AuthUser;

public interface SocialAuthUser<K extends Serializable> extends AuthUser<K> {

}
