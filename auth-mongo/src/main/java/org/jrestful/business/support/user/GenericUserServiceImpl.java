package org.jrestful.business.support.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jrestful.business.support.GenericSequencedDocumentServiceImpl;
import org.jrestful.data.documents.support.user.GenericUser;
import org.jrestful.data.repositories.support.user.GenericUserRepository;
import org.jrestful.web.security.auth.exceptions.EmailAlreadyExistsException;
import org.jrestful.web.security.auth.exceptions.SignUpException;
import org.jrestful.web.security.auth.exceptions.UserDataNotValidException;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class GenericUserServiceImpl<R extends GenericUserRepository<U>, U extends GenericUser> extends
    GenericSequencedDocumentServiceImpl<R, U> implements GenericUserService<U> {

  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@]+@[^@]+$");

  private final PasswordEncoder passwordEncoder;
  
  public GenericUserServiceImpl(R repository, PasswordEncoder passwordEncoder) {
    super(repository);
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public U findOneByEmail(String email) {
    return repository.findOneByEmail(email);
  }
  
  @Override
  public U signUp(U user) throws SignUpException {
    validate(user);
    return insert(user);
  }
  
  protected void validate(U user) throws SignUpException {
    if (StringUtils.isBlank(user.getName())) {
      throw new UserDataNotValidException();
    } else if (StringUtils.isBlank(user.getEmail())) {
      throw new UserDataNotValidException();
    } else if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
      throw new UserDataNotValidException();
    } else if (StringUtils.isBlank(user.getPassword())) {
      throw new UserDataNotValidException();
    } else if (findOneByEmail(user.getEmail()) != null) {
      throw new EmailAlreadyExistsException();
    }
    user.setRoles(new ArrayList<>(Arrays.asList("ROLE_USER")));
    user.setPassword(passwordEncoder.encode(user.getPassword()));
  }

}
