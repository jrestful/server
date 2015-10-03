package org.jrestful.tests.web.controllers.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.jrestful.tests.components.article.ArticleService;
import org.jrestful.tests.components.user.User;
import org.jrestful.tests.components.user.UserService;
import org.jrestful.util.JsonUtils;
import org.jrestful.web.hateoas.RestResource;
import org.jrestful.web.security.auth.user.EmailPassword;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:jrestful/tests/applicationContext.xml" })
@WebAppConfiguration
public class UserRestControllerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserRestControllerTest.class);

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private FilterChainProxy springSecurityFilterChain;

  @Autowired
  private ArticleService articleService;

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Value("#{appProps['app.apiVersion']}")
  private String apiVersion;

  @Value("#{secProps['auth.headerName']}")
  private String authHeader;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();
  }

  @Test
  public void testRest() throws Exception {

    ResultActions resultActions;

    // try to login but 401 (malformed request)
    resultActions = mockMvc.perform( //
        post("/api-" + apiVersion + "/signin"));
    Assert.assertEquals(HttpStatus.UNAUTHORIZED.value(), resultActions.andReturn().getResponse().getStatus());

    // try to login but 401 (user does not exist)
    EmailPassword emailPassword = new EmailPassword("john.doe@jrestful.org", "jrestful");
    resultActions = mockMvc.perform( //
        post("/api-" + apiVersion + "/signin") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(emailPassword).asString()));
    Assert.assertEquals(HttpStatus.UNAUTHORIZED.value(), resultActions.andReturn().getResponse().getStatus());

    // create user
    User user = new User();
    user.setEmail("john.doe@jrestful.org");
    user.setPassword(passwordEncoder.encode("jrestful"));
    user.setRoles(Lists.newArrayList("ROLE_ADMIN"));
    user.setCity("Springfield");
    user = userService.insert(user);

    // try to login but 401 (user is not enabled)
    resultActions = mockMvc.perform( //
        post("/api-" + apiVersion + "/signin") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(emailPassword).asString()));
    Assert.assertEquals(HttpStatus.UNAUTHORIZED.value(), resultActions.andReturn().getResponse().getStatus());

    // enable user
    user.setEnabled(true);
    user = userService.save(user);

    // login
    resultActions = mockMvc.perform( //
        post("/api-" + apiVersion + "/signin") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(emailPassword).asString()));
    String authToken = resultActions.andReturn().getResponse().getHeader(authHeader);
    Assert.assertNotNull(authToken);

    // check profile (without token)
    resultActions = mockMvc.perform( //
        get("/api-" + apiVersion + "/rest/users/profile"));
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
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/api-" + apiVersion + "/rest/users/profile")));

    // check profile (with token)
    resultActions = mockMvc.perform( //
        get("/api-" + apiVersion + "/rest/users/profile") //
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
        .andExpect(jsonPath("$.roles[0]", is("ROLE_ADMIN"))) //
        .andExpect(jsonPath("$.anonymous", is(false))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/api-" + apiVersion + "/rest/users/profile")));

  }

}
