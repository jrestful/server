package org.jrestful.business.support.social;

import org.jrestful.business.support.GenericDocumentService;
import org.jrestful.data.documents.support.social.GenericSocialConnection;
import org.jrestful.data.documents.support.social.GenericSocialUser;
import org.jrestful.web.security.auth.social.connection.SocialAuthConnectionService;

public interface GenericSocialConnectionService<C extends GenericSocialConnection<U>, U extends GenericSocialUser> extends GenericDocumentService<C>,
    SocialAuthConnectionService<C, U> {

}
