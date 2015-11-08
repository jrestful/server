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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.velocity.VelocityEngineUtils;

public abstract class GenericUserServiceImpl<R extends GenericUserRepository<U>, U extends GenericUser> extends
    GenericSequencedDocumentServiceImpl<R, U> implements GenericUserService<U> {

  private static final String ENCODING = "UTF-8";

  private static final int EMAIL_CONFIRMATION_TOKEN_LENGTH = 6;

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
  public U signUp(U payload) throws HttpStatusException {
    validateSignUp(payload);
    prepareSignUp(payload);
    final U user = insert(payload);
    if (user != null) {
      final UserToken token = userTokenService.createAndSave(user, UserToken.Type.EMAIL_CONFIRMATION, RandomUtils.NUMBERS,
          EMAIL_CONFIRMATION_TOKEN_LENGTH);
      MimeMessagePreparator preparator = new MimeMessagePreparator() {

        @Override
        public void prepare(MimeMessage mimeMessage) throws Exception {
          MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
          if (emailFrom != null) {
            message.setFrom(emailFrom);
          }
          message.setTo(user.getEmail());
          Map<String, Object> model = new HashMap<>();
          model.put("user", user);
          model.put("token", token.getToken());
          String templateLocation = prepareEmailConfirmationEmail(message, model);
          String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateLocation, ENCODING, model);
          message.setText(text, true);
        }

      };
      mailSender.send(preparator);
    }
    return user;
  }

  protected void validateSignUp(U payload) throws HttpStatusException {
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

  protected abstract String prepareEmailConfirmationEmail(MimeMessageHelper message, Map<String, Object> model) throws MessagingException;

}
