package org.jrestful.tests.web.controllers.rest;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.jrestful.tests.components.user.User;
import org.jrestful.util.JsonUtils;
import org.jrestful.util.JsonUtils.ObjectMapperDecorator;
import org.jrestful.web.beans.EmailPassword;
import org.jrestful.web.beans.RestResource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;
import org.subethamail.wiser.Wiser;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

public class AuthRestControllerTest extends TestHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthRestControllerTest.class);

  private static final ObjectMapperDecorator DISABLE_ANNOTATIONS = new ObjectMapperDecorator() {

    @Override
    public void decorate(ObjectMapper objectMapper) {
      objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
    }

  };

  private final Wiser smtpServer = new Wiser();

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Value("#{appProps['app.apiVersion']}")
  private String apiVersion;

  @Value("#{emailProps['email.host']}")
  private String emailHost;

  @Value("#{emailProps['email.port']}")
  private int emailPort;

  @Value("#{secProps['auth.accessTokenHeaderName']}")
  private String accessTokenHeaderName;

  @Value("#{secProps['auth.refreshTokenHeaderName']}")
  private String refreshTokenHeaderName;

  @Override
  public void before() {
    super.before();
    smtpServer.setHostname(emailHost);
    smtpServer.setPort(emailPort);
    smtpServer.start();
  }

  @Test
  public void testSignUp() throws Exception {

    Map<String, String> payload = new HashMap<>();
    MimeMessage message;
    Document messageContent;
    ResultActions resultActions;

    // try to sign in but 422 (unprocessable entity)
    resultActions = mockMvc.perform( //
        put("/api/v" + apiVersion + "/auth"));
    resultActions //
        .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));

    // try to sign in but 401 (user does not exist)
    EmailPassword emailPassword = new EmailPassword("john.doe@jrestful.org", "  jrestful  ");
    resultActions = mockMvc.perform( //
        put("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(emailPassword).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));

    // sign up but 422 (name is missing)
    User user = new User();
    user.setEmail("john.doe@jrestful.org");
    user.setPassword("jrestful");
    user.setCity("Springfield");
    resultActions = mockMvc.perform( //
        post("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(user, DISABLE_ANNOTATIONS).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value())) //
        .andExpect(content().string("EMPTY_USERNAME"));

    // sign up but 422 (email is missing)
    user = new User();
    user.setName("John Doe");
    user.setPassword("jrestful");
    user.setCity("Springfield");
    resultActions = mockMvc.perform( //
        post("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(user, DISABLE_ANNOTATIONS).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value())) //
        .andExpect(content().string("EMPTY_EMAIL"));

    // sign up but 422 (password is missing)
    user = new User();
    user.setName("John Doe");
    user.setEmail("john.doe@jrestful.org");
    user.setCity("Springfield");
    resultActions = mockMvc.perform( //
        post("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(user, DISABLE_ANNOTATIONS).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value())) //
        .andExpect(content().string("EMPTY_PASSWORD"));

    // sign up but 422 (city is missing)
    user = new User();
    user.setName("John Doe");
    user.setEmail("john.doe@jrestful.org");
    user.setPassword("jrestful");
    resultActions = mockMvc.perform( //
        post("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(user, DISABLE_ANNOTATIONS).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value())) //
        .andExpect(content().string("EMPTY_CITY"));

    // sign up but 422 (email is invalid)
    user = new User();
    user.setName("John Doe");
    user.setEmail("#");
    user.setPassword("jrestful");
    user.setCity("Springfield");
    resultActions = mockMvc.perform( //
        post("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(user, DISABLE_ANNOTATIONS).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value())) //
        .andExpect(content().string("INVALID_EMAIL"));

    // sign up but 422 (email is DEA)
    user = new User();
    user.setName("John Doe");
    user.setEmail("john.doe@yopmail.com");
    user.setPassword("jrestful");
    user.setCity("Springfield");
    resultActions = mockMvc.perform( //
        post("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(user, DISABLE_ANNOTATIONS).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value())) //
        .andExpect(content().string("INVALID_EMAIL"));

    // sign up
    user = new User();
    user.setId("#");
    user.setSequence(-1l);
    user.setName("  John Doe  ");
    user.setEmail("  john.doe@jrestful.org  ");
    user.setPassword("  jrestful  ");
    user.setCity("  Springfield  ");
    user.setRoles(Arrays.asList("#"));
    user.setAccountExpired(true);
    user.setAccountLocked(true);
    user.setEnabled(true);
    user.setPasswordExpired(true);
    resultActions = mockMvc.perform( //
        post("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(user, DISABLE_ANNOTATIONS).asString()));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());

    // check the database values
    user = userService.findOneByEmail("john.doe@jrestful.org");
    assertNotNull(user);
    assertNotEquals("#", user.getId());
    assertNotEquals(new Long(-1), user.getSequence());
    assertEquals("John Doe", user.getName());
    assertEquals("john.doe@jrestful.org", user.getEmail());
    assertEquals("Springfield", user.getCity());
    assertEquals(Arrays.asList("ROLE_USER"), user.getRoles());
    assertFalse(user.isAccountExpired());
    assertFalse(user.isAccountLocked());
    assertFalse(user.isEnabled());
    assertFalse(user.isPasswordExpired());

    // check sign up HTTP response
    resultActions //
        .andExpect(status().is(HttpStatus.CREATED.value())) //
        .andExpect(content().contentType(RestResource.HAL_MEDIA_TYPE)) //
        .andExpect(jsonPath("$.id", is(user.getId()))) //
        .andExpect(jsonPath("$.sequence", is(user.getSequence().intValue()))) //
        .andExpect(jsonPath("$.name", is(user.getName()))) //
        .andExpect(jsonPath("$.city", is(user.getCity()))) //
        .andExpect(jsonPath("$.roles", is(Arrays.asList("ROLE_USER")))) //
        .andExpect(jsonPath("$", not(hasProperty("authorities")))) //
        .andExpect(jsonPath("$", not(hasProperty("email")))) //
        .andExpect(jsonPath("$", not(hasProperty("username")))) //
        .andExpect(jsonPath("$", not(hasProperty("accountExpired")))) //
        .andExpect(jsonPath("$", not(hasProperty("accountLocked")))) //
        .andExpect(jsonPath("$", not(hasProperty("accountNonExpired")))) //
        .andExpect(jsonPath("$", not(hasProperty("accountNonLocked")))) //
        .andExpect(jsonPath("$", not(hasProperty("credentialsNonExpired")))) //
        .andExpect(jsonPath("$", not(hasProperty("enabled")))) //
        .andExpect(jsonPath("$", not(hasProperty("passwordExpired")))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v" + apiVersion + "/auth")));

    // check the email
    assertEquals(1, smtpServer.getMessages().size());
    message = smtpServer.getMessages().get(0).getMimeMessage();
    assertEquals("jrestful <tests@jrestful.org>", message.getFrom()[0].toString());
    assertEquals("john.doe@jrestful.org", message.getAllRecipients()[0].toString());
    assertEquals("[jrestful] Testing confirmation email", message.getSubject());
    messageContent = Jsoup.parse(message.getContent().toString());
    assertEquals(user.getName(), messageContent.getElementById("username").text());
    String signUpEmailConfirmationToken = messageContent.getElementById("token").text();
    assertTrue(signUpEmailConfirmationToken.matches("^\\d{6}$"));

    // sign up but 409 (email already exists)
    User dupe = new User();
    dupe.setName("John Doe");
    dupe.setEmail("john.doe@jrestful.org");
    dupe.setPassword("jrestful");
    dupe.setCity("Springfield");
    resultActions = mockMvc.perform( //
        post("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(dupe, DISABLE_ANNOTATIONS).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.CONFLICT.value())) //
        .andExpect(content().string("EMAIL_ALREADY_EXISTS"));

    // try to sign in but 401 (user is not enabled)
    resultActions = mockMvc.perform( //
        put("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(emailPassword).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.UNAUTHORIZED.value())) //
        .andExpect(content().string("DISABLED"));

    // enable user
    payload.clear();
    payload.put("token", signUpEmailConfirmationToken);
    resultActions = mockMvc.perform( //
        patch("/api/v" + apiVersion + "/auth") //
            .param("type", "signUpEmailConfirmation") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(payload).asString())); //
    resultActions.andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(RestResource.HAL_MEDIA_TYPE)) //
        .andExpect(jsonPath("$.id", is(user.getId()))) //
        .andExpect(jsonPath("$.sequence", is(user.getSequence().intValue()))) //
        .andExpect(jsonPath("$.name", is(user.getName()))) //
        .andExpect(jsonPath("$.city", is(user.getCity()))) //
        .andExpect(jsonPath("$.roles", is(Arrays.asList("ROLE_USER")))) //
        .andExpect(jsonPath("$", not(hasProperty("authorities")))) //
        .andExpect(jsonPath("$", not(hasProperty("email")))) //
        .andExpect(jsonPath("$", not(hasProperty("username")))) //
        .andExpect(jsonPath("$", not(hasProperty("accountExpired")))) //
        .andExpect(jsonPath("$", not(hasProperty("accountLocked")))) //
        .andExpect(jsonPath("$", not(hasProperty("accountNonExpired")))) //
        .andExpect(jsonPath("$", not(hasProperty("accountNonLocked")))) //
        .andExpect(jsonPath("$", not(hasProperty("credentialsNonExpired")))) //
        .andExpect(jsonPath("$", not(hasProperty("enabled")))) //
        .andExpect(jsonPath("$", not(hasProperty("passwordExpired")))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v" + apiVersion + "/auth")));

    // check the database values
    user = userService.findOneByEmail("john.doe@jrestful.org");
    assertTrue(user.isEnabled());

    // sign in
    resultActions = mockMvc.perform( //
        put("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(emailPassword).asString()));
    String accessToken = resultActions.andReturn().getResponse().getHeader(accessTokenHeaderName);
    String refreshToken = resultActions.andReturn().getResponse().getHeader(refreshTokenHeaderName);
    assertNotNull(accessToken);
    assertNotNull(refreshToken);

    // check profile without access token
    resultActions = mockMvc.perform( //
        get("/api/v" + apiVersion + "/auth"));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    resultActions //
        .andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(RestResource.HAL_MEDIA_TYPE)) //
        .andExpect(jsonPath("$.id", nullValue())) //
        .andExpect(jsonPath("$.name", nullValue())) //
        .andExpect(jsonPath("$.email", nullValue())) //
        .andExpect(jsonPath("$.city", nullValue())) //
        .andExpect(jsonPath("$.roles", hasSize(0))) //
        .andExpect(jsonPath("$.anonymous", is(true))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v" + apiVersion + "/auth")));

    // check profile with access token only
    resultActions = mockMvc.perform( //
        get("/api/v" + apiVersion + "/auth") //
            .header(accessTokenHeaderName, accessToken));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    resultActions //
        .andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(RestResource.HAL_MEDIA_TYPE)) //
        .andExpect(jsonPath("$.id", is(user.getId()))) //
        .andExpect(jsonPath("$.name", is(user.getName()))) //
        .andExpect(jsonPath("$.email", is(user.getEmail()))) //
        .andExpect(jsonPath("$.city", is(user.getCity()))) //
        .andExpect(jsonPath("$.roles", hasSize(1))) //
        .andExpect(jsonPath("$.roles[0]", is("ROLE_USER"))) //
        .andExpect(jsonPath("$.anonymous", is(false))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v" + apiVersion + "/auth")));

    // create another user
    User anotherUser = new User();
    anotherUser.setEmail("another.user@jrestful.org");
    anotherUser.setPassword(passwordEncoder.encode("jrestful"));
    anotherUser.setRoles(Lists.newArrayList("ROLE_ADMIN"));
    anotherUser.setCity("Springfield");
    anotherUser.setEnabled(true);
    anotherUser = userService.insert(anotherUser);

    // login with another user
    resultActions = mockMvc.perform( //
        put("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(new EmailPassword("another.user@jrestful.org", "jrestful")).asString()));
    String anotherAccessToken = resultActions.andReturn().getResponse().getHeader(accessTokenHeaderName);
    String anotherRefreshToken = resultActions.andReturn().getResponse().getHeader(refreshTokenHeaderName);
    assertNotNull(anotherAccessToken);
    assertNotNull(anotherRefreshToken);

    // making access tokens invalid
    Thread.sleep(2100);

    // check profile with invalid access token
    resultActions = mockMvc.perform( //
        get("/api/v" + apiVersion + "/auth") //
            .header(accessTokenHeaderName, accessToken));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    resultActions //
        .andExpect(jsonPath("$.anonymous", is(true)));

    // check profile with invalid access token but valid refresh token that belongs to another user
    resultActions = mockMvc.perform( //
        get("/api/v" + apiVersion + "/auth") //
            .header(accessTokenHeaderName, accessToken) //
            .header(refreshTokenHeaderName, anotherRefreshToken));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    resultActions //
        .andExpect(jsonPath("$.anonymous", is(true)));

    // check profile with invalid access token but valid refresh token
    resultActions = mockMvc.perform( //
        get("/api/v" + apiVersion + "/auth") //
            .header(accessTokenHeaderName, accessToken) //
            .header(refreshTokenHeaderName, refreshToken));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    resultActions //
        .andExpect(jsonPath("$.anonymous", is(false)));
    String newAccessToken = resultActions.andReturn().getResponse().getHeader(accessTokenHeaderName);
    String newRefreshToken = resultActions.andReturn().getResponse().getHeader(refreshTokenHeaderName);
    assertNotNull(newAccessToken);
    assertNotNull(newRefreshToken);

    // check profile with invalid access token and already used refresh token
    resultActions = mockMvc.perform( //
        get("/api/v" + apiVersion + "/auth") //
            .header(accessTokenHeaderName, accessToken) //
            .header(refreshTokenHeaderName, refreshToken));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    resultActions //
        .andExpect(jsonPath("$.anonymous", is(true)));

    // check profile with invalid access token and not yet valid new refresh token
    resultActions = mockMvc.perform( //
        get("/api/v" + apiVersion + "/auth") //
            .header(accessTokenHeaderName, accessToken) //
            .header(refreshTokenHeaderName, newRefreshToken));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    resultActions //
        .andExpect(jsonPath("$.anonymous", is(true)));

    // making refresh tokens valid
    Thread.sleep(2100);

    // check profile with invalid access token and valid new refresh token
    resultActions = mockMvc.perform( //
        get("/api/v" + apiVersion + "/auth") //
            .header(accessTokenHeaderName, accessToken) //
            .header(refreshTokenHeaderName, newRefreshToken));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    resultActions //
        .andExpect(jsonPath("$.anonymous", is(false)));
    newAccessToken = resultActions.andReturn().getResponse().getHeader(accessTokenHeaderName);
    newRefreshToken = resultActions.andReturn().getResponse().getHeader(refreshTokenHeaderName);
    assertNotNull(newAccessToken);
    assertNotNull(newRefreshToken);

    // check profile with new access token only
    resultActions = mockMvc.perform( //
        get("/api/v" + apiVersion + "/auth") //
            .header(accessTokenHeaderName, newAccessToken));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    resultActions //
        .andExpect(jsonPath("$.anonymous", is(false)));

  }

  @Test
  public void testForgottenPassword() throws Exception {

    Map<String, String> payload = new HashMap<>();
    MimeMessage message;
    Document messageContent;
    ResultActions resultActions;

    // create user
    User user = new User();
    user.setName("John Doe");
    user.setEmail("john.doe@jrestful.org");
    user.setPassword(passwordEncoder.encode("jrestful"));
    user.setCity("Springfield");
    user.setRoles(Arrays.asList("ROLE_USER"));
    user.setAccountExpired(false);
    user.setAccountLocked(false);
    user.setEnabled(true);
    user.setPasswordExpired(false);
    userService.insert(user);

    // ask for new password but 404 (email is invalid)
    payload.clear();
    payload.put("email", "fakeemail");
    resultActions = mockMvc.perform( //
        patch("/api/v" + apiVersion + "/auth") //
            .param("type", "tempPasswordGeneration") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(payload).asString())); //
    resultActions.andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    // ask for new password but 404 (email is valid but does not exist)
    payload.clear();
    payload.put("email", "fake@email");
    resultActions = mockMvc.perform( //
        patch("/api/v" + apiVersion + "/auth") //
            .param("type", "tempPasswordGeneration") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(payload).asString())); //
    resultActions.andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    // ask for new password
    payload.clear();
    payload.put("email", user.getEmail());
    resultActions = mockMvc.perform( //
        patch("/api/v" + apiVersion + "/auth") //
            .param("type", "tempPasswordGeneration") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(payload).asString())); //
    resultActions.andExpect(status().is(HttpStatus.NO_CONTENT.value()));

    // ask for new password again but 409 (new password already asked)
    payload.clear();
    payload.put("email", user.getEmail());
    resultActions = mockMvc.perform( //
        patch("/api/v" + apiVersion + "/auth") //
            .param("type", "tempPasswordGeneration") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(payload).asString())); //
    resultActions.andExpect(status().is(HttpStatus.CONFLICT.value()));

    // check the email
    assertEquals(1, smtpServer.getMessages().size());
    message = smtpServer.getMessages().get(0).getMimeMessage();
    assertEquals("jrestful <tests@jrestful.org>", message.getFrom()[0].toString());
    assertEquals("john.doe@jrestful.org", message.getAllRecipients()[0].toString());
    assertEquals("[jrestful] Testing temp password email", message.getSubject());
    messageContent = Jsoup.parse(message.getContent().toString());
    assertEquals(user.getName(), messageContent.getElementById("username").text());
    String tempPassword = messageContent.getElementById("tempPassword").text();
    assertTrue(tempPassword.matches("^[a-zA-Z0-9]{12}$"));

    // check database values
    user = userService.findOneByEmail(user.getEmail());
    assertTrue(passwordEncoder.matches("jrestful", user.getPassword()));
    assertTrue(passwordEncoder.matches(tempPassword, user.getTempPassword()));

    // sign in with new password
    resultActions = mockMvc.perform( //
        put("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(new EmailPassword("john.doe@jrestful.org", tempPassword)).asString()));
    String accessToken = resultActions.andReturn().getResponse().getHeader(accessTokenHeaderName);
    String refreshToken = resultActions.andReturn().getResponse().getHeader(refreshTokenHeaderName);
    assertNotNull(accessToken);
    assertNotNull(refreshToken);

    // check database values
    user = userService.findOneByEmail(user.getEmail());
    assertTrue(passwordEncoder.matches(tempPassword, user.getPassword()));
    assertNull(user.getTempPassword());

    // sign in with new password
    resultActions = mockMvc.perform( //
        put("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(new EmailPassword("john.doe@jrestful.org", tempPassword)).asString()));
    accessToken = resultActions.andReturn().getResponse().getHeader(accessTokenHeaderName);
    refreshToken = resultActions.andReturn().getResponse().getHeader(refreshTokenHeaderName);
    assertNotNull(accessToken);
    assertNotNull(refreshToken);

    // ask for new password
    payload.clear();
    payload.put("email", user.getEmail());
    resultActions = mockMvc.perform( //
        patch("/api/v" + apiVersion + "/auth") //
            .param("type", "tempPasswordGeneration") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(payload).asString())); //
    resultActions.andExpect(status().is(HttpStatus.NO_CONTENT.value()));

    // check the email
    message = smtpServer.getMessages().get(1).getMimeMessage();
    messageContent = Jsoup.parse(message.getContent().toString());
    String newTempPassword = messageContent.getElementById("tempPassword").text();
    assertTrue(tempPassword.matches("^[a-zA-Z0-9]{12}$"));

    // check database values
    user = userService.findOneByEmail(user.getEmail());
    assertTrue(passwordEncoder.matches(tempPassword, user.getPassword()));
    assertTrue(passwordEncoder.matches(newTempPassword, user.getTempPassword()));

    // sign in with previous password (new password should be forgotten)
    resultActions = mockMvc.perform( //
        put("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(new EmailPassword("john.doe@jrestful.org", tempPassword)).asString()));
    accessToken = resultActions.andReturn().getResponse().getHeader(accessTokenHeaderName);
    refreshToken = resultActions.andReturn().getResponse().getHeader(refreshTokenHeaderName);
    assertNotNull(accessToken);
    assertNotNull(refreshToken);

    // check database values
    user = userService.findOneByEmail(user.getEmail());
    assertTrue(passwordEncoder.matches(tempPassword, user.getPassword()));
    assertNull(user.getTempPassword());

    // sign in with new password (should fail as forgotten)
    resultActions = mockMvc.perform( //
        put("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(new EmailPassword("john.doe@jrestful.org", newTempPassword)).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));

  }

  @Test
  public void testChangePassword() throws Exception {

    Map<String, String> payload = new HashMap<>();
    ResultActions resultActions;

    // create user
    User user = new User();
    user.setName("John Doe");
    user.setEmail("john.doe@jrestful.org");
    user.setPassword(passwordEncoder.encode("jrestful"));
    user.setCity("Springfield");
    user.setRoles(Arrays.asList("ROLE_USER"));
    user.setAccountExpired(false);
    user.setAccountLocked(false);
    user.setEnabled(true);
    user.setPasswordExpired(false);
    userService.insert(user);

    // sign in
    resultActions = mockMvc.perform( //
        put("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(new EmailPassword("john.doe@jrestful.org", "jrestful")).asString()));
    String accessToken = resultActions.andReturn().getResponse().getHeader(accessTokenHeaderName);
    String refreshToken = resultActions.andReturn().getResponse().getHeader(refreshTokenHeaderName);
    assertNotNull(accessToken);
    assertNotNull(refreshToken);

    // change password but 401 (no token)
    payload.clear();
    payload.put("currentPassword", "fake");
    payload.put("newPassword", "newpassword");
    resultActions = mockMvc.perform( //
        patch("/api/v" + apiVersion + "/auth") //
            .param("type", "passwordChange") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(payload).asString())); //
    resultActions.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));

    // change password but 404 (current password does not match)
    payload.clear();
    payload.put("currentPassword", "fake");
    payload.put("newPassword", "newpassword");
    resultActions = mockMvc.perform( //
        patch("/api/v" + apiVersion + "/auth") //
            .param("type", "passwordChange") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(payload).asString()) //
            .header(accessTokenHeaderName, accessToken) //
            .header(refreshTokenHeaderName, refreshToken));
    resultActions.andExpect(status().is(HttpStatus.NOT_FOUND.value()));

    // change password but 422 (no new password)
    payload.clear();
    payload.put("currentPassword", "jrestful");
    resultActions = mockMvc.perform( //
        patch("/api/v" + apiVersion + "/auth") //
            .param("type", "passwordChange") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(payload).asString()) //
            .header(accessTokenHeaderName, accessToken) //
            .header(refreshTokenHeaderName, refreshToken));
    resultActions.andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value())) //
        .andExpect(content().string("EMPTY_NEW_PASSWORD"));

    // change password but 422 (empty new password)
    payload.clear();
    payload.put("currentPassword", "jrestful");
    payload.put("newPassword", "");
    resultActions = mockMvc.perform( //
        patch("/api/v" + apiVersion + "/auth") //
            .param("type", "passwordChange") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(payload).asString()) //
            .header(accessTokenHeaderName, accessToken) //
            .header(refreshTokenHeaderName, refreshToken));
    resultActions.andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value())) //
        .andExpect(content().string("EMPTY_NEW_PASSWORD"));

    // change password
    payload.clear();
    payload.put("currentPassword", "jrestful");
    payload.put("newPassword", "newpassword");
    resultActions = mockMvc.perform( //
        patch("/api/v" + apiVersion + "/auth") //
            .param("type", "passwordChange") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(payload).asString()) //
            .header(accessTokenHeaderName, accessToken) //
            .header(refreshTokenHeaderName, refreshToken));
    resultActions.andExpect(status().is(HttpStatus.NO_CONTENT.value()));

    // sign in with old password: 401
    resultActions = mockMvc.perform( //
        put("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(new EmailPassword("john.doe@jrestful.org", "jrestful")).asString()));
    resultActions.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));

    // sign in with new password
    resultActions = mockMvc.perform( //
        put("/api/v" + apiVersion + "/auth") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(new EmailPassword("john.doe@jrestful.org", "newpassword")).asString()));
    accessToken = resultActions.andReturn().getResponse().getHeader(accessTokenHeaderName);
    refreshToken = resultActions.andReturn().getResponse().getHeader(refreshTokenHeaderName);
    assertNotNull(accessToken);
    assertNotNull(refreshToken);

  }

  @Override
  public void after() {
    super.after();
    smtpServer.stop();
  }

}
