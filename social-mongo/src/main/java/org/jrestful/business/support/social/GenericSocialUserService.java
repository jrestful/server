package org.jrestful.business.support.social;

import org.jrestful.business.support.user.GenericUserService;
import org.jrestful.data.documents.support.social.GenericSocialUser;
import org.jrestful.web.security.auth.social.user.SocialAuthUserService;

public interface GenericSocialUserService<U extends GenericSocialUser> extends GenericUserService<U>, SocialAuthUserService<U, String> {

}
