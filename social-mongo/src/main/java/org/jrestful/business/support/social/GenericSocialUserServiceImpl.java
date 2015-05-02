package org.jrestful.business.support.social;

import org.jrestful.business.support.user.GenericUserServiceImpl;
import org.jrestful.data.documents.support.social.GenericSocialUser;
import org.jrestful.data.repositories.support.social.GenericSocialUserRepository;

public abstract class GenericSocialUserServiceImpl<R extends GenericSocialUserRepository<U>, U extends GenericSocialUser> extends
    GenericUserServiceImpl<R, U> implements GenericSocialUserService<U> {

  public GenericSocialUserServiceImpl(R repository) {
    super(repository);
  }

}
