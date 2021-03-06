package org.jrestful.tests.components.user;

import java.util.Map;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.jrestful.business.exceptions.HttpStatusException;
import org.jrestful.business.support.GenericUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends GenericUserServiceImpl<UserRepository, User> implements UserService {

  @Autowired
  public UserServiceImpl(UserRepository repository) {
    super(repository);
  }

  @Override
  public void validateSignUp(User payload) {
    super.validateSignUp(payload);
    payload.setCity(StringUtils.trim(payload.getCity()));
    if (StringUtils.isEmpty(payload.getCity())) {
      throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "EMPTY_CITY");
    }
  }

  @Override
  protected String prepareSignUpEmailConfirmationEmail(MimeMessageHelper message, Map<String, Object> model) throws MessagingException {
    message.setSubject("[jrestful] Testing confirmation email");
    return "jrestful/tests/emails/signUpEmailConfirmation.vm";
  }

  @Override
  protected String prepareTempPasswordEmail(MimeMessageHelper message, Map<String, Object> model) throws MessagingException {
    message.setSubject("[jrestful] Testing temp password email");
    return "jrestful/tests/emails/tempPassword.vm";
  }

}
