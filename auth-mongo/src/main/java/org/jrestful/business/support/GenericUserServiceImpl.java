package org.jrestful.business.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jrestful.business.exceptions.EmailAlreadyExistsException;
import org.jrestful.business.exceptions.HttpStatusException;
import org.jrestful.business.exceptions.PayloadNotValidException;
import org.jrestful.data.documents.support.GenericUser;
import org.jrestful.data.repositories.support.GenericUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class GenericUserServiceImpl<R extends GenericUserRepository<U>, U extends GenericUser> extends
    GenericSequencedDocumentServiceImpl<R, U> implements GenericUserService<U> {

  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@]+@[^@]+$");

  protected final PasswordEncoder passwordEncoder;

  public GenericUserServiceImpl(R repository, PasswordEncoder passwordEncoder) {
    super(repository);
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public U findOneByEmail(String email) {
    return repository.findOneByEmail(email);
  }

  @Override
  public U signUp(U payload) throws HttpStatusException {
    validatePayload(payload);
    prepareForSignUp(payload);
    return insert(payload);
  }

  @Override
  public void validatePayload(U payload) throws HttpStatusException {
    if (StringUtils.isBlank(payload.getName())) {
      throw new PayloadNotValidException();
    } else if (StringUtils.isBlank(payload.getEmail())) {
      throw new PayloadNotValidException();
    } else if (!EMAIL_PATTERN.matcher(payload.getEmail()).matches()) {
      throw new PayloadNotValidException();
    } else if (StringUtils.isBlank(payload.getPassword())) {
      throw new PayloadNotValidException();
    } else if (findOneByEmail(payload.getEmail()) != null) {
      throw new EmailAlreadyExistsException();
    }
  }

  protected void prepareForSignUp(U user) {
    user.setRoles(new ArrayList<>(Arrays.asList("ROLE_USER")));
    user.setPassword(passwordEncoder.encode(user.getPassword()));
  }

}
