package org.jrestful.tests.web.controllers.rest;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.jrestful.tests.components.user.User;
import org.jrestful.util.JsonUtils;
import org.jrestful.util.JsonUtils.ObjectMapperDecorator;
import org.jrestful.web.beans.EmailPassword;
import org.jrestful.web.beans.RestResource;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserRestControllerTest extends TestHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserRestControllerTest.class);

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Value("#{appProps['app.apiVersion']}")
  private String apiVersion;

  @Value("#{secProps['auth.headerName']}")
  private String authHeader;

  @Test
  public void testRest() throws Exception {

    ObjectMapperDecorator disableAnnotations = new ObjectMapperDecorator() {

      @Override
      public void decorate(ObjectMapper objectMapper) {
        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
      }

    };

    ResultActions resultActions;

    // try to login but 401 (malformed request)
    resultActions = mockMvc.perform( //
        put("/api-" + apiVersion + "/signIn"));
    resultActions //
        .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));

    // try to login but 401 (user does not exist)
    EmailPassword emailPassword = new EmailPassword("john.doe@jrestful.org", "jrestful");
    resultActions = mockMvc.perform( //
        put("/api-" + apiVersion + "/signIn") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(emailPassword).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));

    // create user but 422 (name is missing)
    User user = new User();
    user.setEmail("john.doe@jrestful.org");
    user.setPassword("jrestful");
    user.setCity("Springfield");
    resultActions = mockMvc.perform( //
        post("/api-" + apiVersion + "/signUp") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(user, disableAnnotations).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));

    // create user but 422 (email is missing)
    user = new User();
    user.setName("John Doe");
    user.setPassword("jrestful");
    user.setCity("Springfield");
    resultActions = mockMvc.perform( //
        post("/api-" + apiVersion + "/signUp") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(user, disableAnnotations).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));

    // create user but 422 (password is missing)
    user = new User();
    user.setName("John Doe");
    user.setEmail("john.doe@jrestful.org");
    user.setCity("Springfield");
    resultActions = mockMvc.perform( //
        post("/api-" + apiVersion + "/signUp") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(user, disableAnnotations).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));

    // create user but 422 (city is missing)
    user = new User();
    user.setName("John Doe");
    user.setEmail("john.doe@jrestful.org");
    user.setPassword("jrestful");
    resultActions = mockMvc.perform( //
        post("/api-" + apiVersion + "/signUp") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(user, disableAnnotations).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));

    // create user but 422 (email is invalid)
    user = new User();
    user.setName("John Doe");
    user.setEmail("#");
    user.setPassword("jrestful");
    user.setCity("Springfield");
    resultActions = mockMvc.perform( //
        post("/api-" + apiVersion + "/signUp") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(user, disableAnnotations).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()));

    // create user
    user = new User();
    user.setId("#");
    user.setSequence(-1l);
    user.setName("John Doe");
    user.setEmail("john.doe@jrestful.org");
    user.setPassword("jrestful");
    user.setCity("Springfield");
    user.setRoles(Arrays.asList("#"));
    user.setAccountExpired(true);
    user.setAccountLocked(true);
    user.setEnabled(true);
    user.setPasswordExpired(true);
    resultActions = mockMvc.perform( //
        post("/api-" + apiVersion + "/signUp") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(user, disableAnnotations).asString()));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());

    // check that read-only values have not been written
    user = userService.findOneByEmail("john.doe@jrestful.org");
    Assert.assertNotNull(user);
    Assert.assertNotEquals("#", user.getId());
    Assert.assertNotEquals(new Long(-1), user.getSequence());
    Assert.assertNotEquals(true, user.isAccountExpired());
    Assert.assertNotEquals(true, user.isAccountLocked());
    Assert.assertNotEquals(true, user.isEnabled());
    Assert.assertNotEquals(true, user.isPasswordExpired());

    // check user creation response
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
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/api-" + apiVersion + "/profile")));

    // create user but 409 (email already exists)
    User dupe = new User();
    dupe.setName("John Doe");
    dupe.setEmail("john.doe@jrestful.org");
    dupe.setPassword("jrestful");
    dupe.setCity("Springfield");
    resultActions = mockMvc.perform( //
        post("/api-" + apiVersion + "/signUp") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(dupe, disableAnnotations).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.CONFLICT.value()));

    // try to login but 401 (user is not enabled)
    resultActions = mockMvc.perform( //
        put("/api-" + apiVersion + "/signIn") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(emailPassword).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));

    // enable user
    user.setEnabled(true);
    user = userService.save(user);

    // try to login but 405 (PUT expected)
    resultActions = mockMvc.perform( //
        post("/api-" + apiVersion + "/signIn") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(emailPassword).asString()));
    resultActions //
        .andExpect(status().is(HttpStatus.METHOD_NOT_ALLOWED.value()))
        .andExpect(header().string(HttpHeaders.ALLOW, RequestMethod.PUT.toString()));

    // login
    resultActions = mockMvc.perform( //
        put("/api-" + apiVersion + "/signIn") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(emailPassword).asString()));
    String authToken = resultActions.andReturn().getResponse().getHeader(authHeader);
    Assert.assertNotNull(authToken);

    // check profile (without token)
    resultActions = mockMvc.perform( //
        get("/api-" + apiVersion + "/profile"));
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
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/api-" + apiVersion + "/profile")));

    // check profile (with token)
    resultActions = mockMvc.perform( //
        get("/api-" + apiVersion + "/profile") //
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
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/api-" + apiVersion + "/profile")));

  }

}
