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
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.subethamail.wiser.Wiser;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthRestControllerTest extends TestHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthRestControllerTest.class);

  private final Wiser smtpServer = new Wiser();

  @Value("#{appProps['app.apiVersion']}")
  private String apiVersion;

  @Value("#{secProps['auth.headerName']}")
  private String authHeader;

  @Override
  public void before() {
    super.before();
    smtpServer.start();
  }

  @Test
  public void testSignUp() throws Exception {

    ObjectMapperDecorator disableAnnotations = new ObjectMapperDecorator() {

      @Override
      public void decorate(ObjectMapper objectMapper) {
        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
      }

    };

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
            .content(JsonUtils.toJson(user, disableAnnotations).asString()));
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
            .content(JsonUtils.toJson(user, disableAnnotations).asString()));
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
            .content(JsonUtils.toJson(user, disableAnnotations).asString()));
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
            .content(JsonUtils.toJson(user, disableAnnotations).asString()));
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
            .content(JsonUtils.toJson(user, disableAnnotations).asString()));
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
            .content(JsonUtils.toJson(user, disableAnnotations).asString()));
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
            .content(JsonUtils.toJson(user, disableAnnotations).asString()));
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
            .content(JsonUtils.toJson(dupe, disableAnnotations).asString()));
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
    resultActions = mockMvc.perform( //
        patch("/api/v" + apiVersion + "/auth") //
            .param("token", signUpEmailConfirmationToken));
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
    String authToken = resultActions.andReturn().getResponse().getHeader(authHeader);
    assertNotNull(authToken);

    // check profile (without token)
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

    // check profile (with token)
    resultActions = mockMvc.perform( //
        get("/api/v" + apiVersion + "/auth") //
            .header(authHeader, authToken));
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

  }

  @Override
  public void after() {
    super.after();
    smtpServer.stop();
  }

}
