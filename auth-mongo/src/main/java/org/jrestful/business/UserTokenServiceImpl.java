package org.jrestful.business;

import org.jrestful.business.support.GenericDocumentServiceImpl;
import org.jrestful.data.documents.UserToken;
import org.jrestful.data.documents.UserToken.Type;
import org.jrestful.data.documents.support.GenericUser;
import org.jrestful.data.repositories.UserTokenRepository;
import org.jrestful.util.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class UserTokenServiceImpl extends GenericDocumentServiceImpl<UserTokenRepository, UserToken> implements UserTokenService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserTokenServiceImpl.class);

  @Autowired
  public UserTokenServiceImpl(UserTokenRepository repository) {
    super(repository);
  }

  @Override
  public UserToken findOneByToken(String token) {
    return repository.findOneByToken(token);
  }

  @Override
  public <U extends GenericUser> UserToken createAndSave(U user, Type type, String alphabet, int length) {
    LOGGER.debug("Creating token " + type + " for user " + user.getId());
    UserToken token = new UserToken();
    token.setUserId(user.getId());
    token.setType(type);
    while (true) {
      token.setToken(RandomUtils.generate(alphabet, length));
      try {
        return save(token);
      } catch (DuplicateKeyException retry) {
      }
    }
  }

}
