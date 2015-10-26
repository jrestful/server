package org.jrestful.business.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jrestful.business.exceptions.HttpStatusException;
import org.jrestful.data.documents.support.GenericUser;
import org.jrestful.data.repositories.support.GenericUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class GenericUserServiceImpl<R extends GenericUserRepository<U>, U extends GenericUser> extends
    GenericSequencedDocumentServiceImpl<R, U> implements GenericUserService<U> {

  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+$");

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
    payload.setName(StringUtils.trim(payload.getName()));
    payload.setEmail(StringUtils.trim(payload.getEmail()));
    if (StringUtils.isEmpty(payload.getName())) {
      throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User name cannot be empty");
    } else if (StringUtils.isEmpty(payload.getEmail())) {
      throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User email cannot be empty");
    } else if (!EMAIL_PATTERN.matcher(payload.getEmail()).matches()) {
      // TODO [pixwin] handle yopmail, etc.
      throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User email is invalid");
    } else if (StringUtils.isEmpty(payload.getPassword())) {
      throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User password cannot be empty");
    } else if (findOneByEmail(payload.getEmail()) != null) {
      throw new HttpStatusException(HttpStatus.CONFLICT, "User email already exists");
    }
  }

  protected void prepareForSignUp(U user) {
    user.setRoles(new ArrayList<>(Arrays.asList("ROLE_USER")));
    user.setPassword(passwordEncoder.encode(user.getPassword()));
  }

}
