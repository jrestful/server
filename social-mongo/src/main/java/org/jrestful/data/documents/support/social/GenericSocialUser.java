package org.jrestful.data.documents.support.social;

import org.jrestful.data.documents.support.user.GenericUser;
import org.jrestful.web.security.auth.social.user.SocialAuthUser;

public abstract class GenericSocialUser extends GenericUser implements SocialAuthUser<String> {

  private static final long serialVersionUID = 1L;

}
