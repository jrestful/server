package org.jrestful.data.documents.support.social;

import org.jrestful.data.documents.support.GenericDocument;
import org.jrestful.web.security.auth.social.connection.SocialAuthConnection;

public abstract class GenericSocialConnection<U extends GenericSocialUser> extends GenericDocument implements SocialAuthConnection<U> {

  private static final long serialVersionUID = 1L;

}
