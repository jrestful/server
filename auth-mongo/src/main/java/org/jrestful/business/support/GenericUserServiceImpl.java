package org.jrestful.business.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.jrestful.business.UserTokenService;
import org.jrestful.business.exceptions.HttpStatusException;
import org.jrestful.data.documents.UserToken;
import org.jrestful.data.documents.support.GenericUser;
import org.jrestful.data.repositories.support.GenericUserRepository;
import org.jrestful.util.EmailUtils;
import org.jrestful.util.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.velocity.VelocityEngineUtils;

public abstract class GenericUserServiceImpl<R extends GenericUserRepository<U>, U extends GenericUser> extends
    GenericSequencedDocumentServiceImpl<R, U> implements GenericUserService<U> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GenericUserServiceImpl.class);

  private static class NoEmailException extends Exception {

    private static final long serialVersionUID = 1L;

  }

  private static final String ENCODING = "UTF-8";

  private static final int EMAIL_CONFIRMATION_TOKEN_LENGTH = 6;

  private static final int NEW_PASSWORD_LENGTH = 12;

  @Autowired
  private UserTokenService userTokenService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private VelocityEngine velocityEngine;

  @Value("#{emailProps['email.from']}")
  private String emailFrom;

  public GenericUserServiceImpl(R repository) {
    super(repository);
  }

  @Override
  public U findOneByEmail(String email) {
    return repository.findOneByEmail(email);
  }

  @Override
  public U findOneByRefreshToken(String refreshToken) {
    UserToken userToken = userTokenService.findOneByTypeAndTokenThenRemove(UserToken.Type.REFRESH_TOKEN, refreshToken);
    return userToken != null ? findOne(userToken.getUserId()) : null;
  }

  @Override
  public U signUp(U payload) {
    LOGGER.debug("New user tries to sign up");
    validateSignUp(payload);
    prepareSignUp(payload);
    U user = insert(payload);
    if (user != null && !user.isEnabled()) {
      LOGGER.info("User " + user.getEmail() + " successfuly created, sending email to confirm the account");
      UserToken userToken = userTokenService.createAndSave(user, UserToken.Type.SIGN_UP_EMAIL_CONFIRMATION, RandomUtils.NUMBERS,
          EMAIL_CONFIRMATION_TOKEN_LENGTH);
      sendSignUpEmailConfirmationEmail(user, userToken);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Confirmation email sent to user " + user.getEmail());
      }
    }
    return user;
  }

  protected void validateSignUp(U payload) {
    payload.setName(StringUtils.trim(payload.getName()));
    payload.setEmail(StringUtils.trim(payload.getEmail()));
    if (StringUtils.isEmpty(payload.getName())) {
      throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "EMPTY_USERNAME");
    } else if (StringUtils.isEmpty(payload.getEmail())) {
      throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "EMPTY_EMAIL");
    } else if (!EmailUtils.seemsValid(payload.getEmail())) {
      throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_EMAIL");
    } else if (StringUtils.isEmpty(payload.getPassword())) {
      throw new HttpStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "EMPTY_PASSWORD");
    } else if (findOneByEmail(payload.getEmail()) != null) {
      throw new HttpStatusException(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS");
    }
  }

  protected void prepareSignUp(U user) {
    user.setRoles(new ArrayList<>(Arrays.asList("ROLE_USER")));
    user.setPassword(passwordEncoder.encode(user.getPassword()));
  }

  private void sendSignUpEmailConfirmationEmail(final U user, final UserToken userToken) {
    MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {

      @Override
      public void prepare(MimeMessage mimeMessage) throws Exception {
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, ENCODING);
        if (emailFrom != null) {
          message.setFrom(emailFrom);
        }
        message.setTo(user.getEmail());
        Map<String, Object> model = new HashMap<>();
        model.put("user", user);
        model.put("token", userToken.getToken());
        String templateLocation = prepareSignUpEmailConfirmationEmail(message, model);
        if (templateLocation == null) {
          throw new NoEmailException();
        } else {
          String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateLocation, ENCODING, model);
          message.setText(text, true);
        }
      }

    };
    try {
      mailSender.send(mimeMessagePreparator);
    } catch (MailPreparationException e) {
      if (!(e.getCause() instanceof NoEmailException)) {
        throw e;
      }
    }
  }

  protected abstract String prepareSignUpEmailConfirmationEmail(MimeMessageHelper message, Map<String, Object> model) throws MessagingException;

  @Override
  public U confirmSignUpEmail(String token) {
    UserToken userToken = userTokenService.findOneByTypeAndTokenThenRemove(UserToken.Type.SIGN_UP_EMAIL_CONFIRMATION, token);
    if (userToken == null) {
      throw new HttpStatusException(HttpStatus.NOT_FOUND);
    } else {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Confirming user " + userToken.getUserId() + " account");
      }
      U user = findOne(userToken.getUserId());
      if (user == null) {
        throw new HttpStatusException(HttpStatus.GONE);
      } else if (user.isEnabled()) {
        throw new HttpStatusException(HttpStatus.CONFLICT);
      } else {
        user.setEnabled(true);
        user = save(user);
        LOGGER.info("User " + userToken.getUserId() + " account confirmed and enabled");
        return user;
      }
    }
  }

  @Override
  public void persistRefreshToken(String userId, String refreshToken) {
    userTokenService.deleteByUserIdAndType(userId, UserToken.Type.REFRESH_TOKEN);
    UserToken userToken = new UserToken();
    userToken.setUserId(userId);
    userToken.setType(UserToken.Type.REFRESH_TOKEN);
    userToken.setToken(refreshToken);
    userTokenService.save(userToken);
  }

  @Override
  public U generateTempPassword(String email) {
    if (!EmailUtils.seemsValid(email)) {
      throw new HttpStatusException(HttpStatus.NOT_FOUND);
    } else {
      U user = findOneByEmail(email);
      if (user == null) {
        throw new HttpStatusException(HttpStatus.NOT_FOUND);
      } else if (user.getTempPassword() != null) {
        throw new HttpStatusException(HttpStatus.CONFLICT);
      } else {
        String tempPassword = RandomUtils.generateNumbersAndLetters(NEW_PASSWORD_LENGTH);
        user.setTempPassword(passwordEncoder.encode(tempPassword));
        save(user);
        sendTempPasswordEmail(user, tempPassword);
        return user;
      }
    }
  }

  private void sendTempPasswordEmail(final U user, final String tempPassword) {
    MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {

      @Override
      public void prepare(MimeMessage mimeMessage) throws Exception {
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, ENCODING);
        if (emailFrom != null) {
          message.setFrom(emailFrom);
        }
        message.setTo(user.getEmail());
        Map<String, Object> model = new HashMap<>();
        model.put("user", user);
        model.put("tempPassword", tempPassword);
        String templateLocation = prepareTempPasswordEmail(message, model);
        if (templateLocation == null) {
          throw new NoEmailException();
        } else {
          String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateLocation, ENCODING, model);
          message.setText(text, true);
        }
      }

    };
    try {
      mailSender.send(mimeMessagePreparator);
    } catch (MailPreparationException e) {
      if (!(e.getCause() instanceof NoEmailException)) {
        throw e;
      }
    }
  }

  protected abstract String prepareTempPasswordEmail(MimeMessageHelper message, Map<String, Object> model) throws MessagingException;

  @Override
  public void clearTempPassword(String userId) {
    U user = findOne(userId);
    user.setTempPassword(null);
    save(user);
  }

  @Override
  public void replacePasswordByTempPassword(String userId) {
    U user = findOne(userId);
    user.setPassword(user.getTempPassword());
    user.setTempPassword(null);
    save(user);
  }

  @Override
  public void delete(Iterable<U> users) {
    for (U user : users) {
      userTokenService.deleteByUserId(user.getId());
    }
    super.delete(users);
  }

  @Override
  public void delete(String id) {
    userTokenService.deleteByUserId(id);
    super.delete(id);
  }

  @Override
  public void delete(U user) {
    userTokenService.deleteByUserId(user.getId());
    super.delete(user);
  }

  @Override
  public void deleteAll() {
    userTokenService.deleteAll();
    super.deleteAll();
  }

  @Override
  public int deleteBySequence(long sequence) {
    U user = findOneBySequence(sequence);
    if (user != null) {
      userTokenService.deleteByUserId(user.getId());
    }
    return super.deleteBySequence(sequence);
  }

}
