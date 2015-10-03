package org.jrestful.tests.web.controllers.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.jrestful.tests.components.article.Article;
import org.jrestful.tests.components.user.User;
import org.jrestful.util.JsonUtils;
import org.jrestful.web.hateoas.RestResource;
import org.jrestful.web.security.auth.user.EmailPassword;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import com.google.common.collect.Lists;

public class ArticleRestControllerTest extends TestHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArticleRestControllerTest.class);

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Value("#{appProps['app.apiVersion']}")
  private String apiVersion;

  @Value("#{secProps['auth.headerName']}")
  private String authHeader;

  @Test
  public void testRest() throws Exception {

    ResultActions resultActions;

    // create article1 but 403 (role ROLE_ADMIN needed)
    Article article1 = new Article("article1");
    resultActions = mockMvc.perform( //
        post("/api-" + apiVersion + "/rest/articles") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(article1).asString()));
    Assert.assertEquals(HttpStatus.FORBIDDEN.value(), resultActions.andReturn().getResponse().getStatus());

    // create user
    User user = new User();
    user.setEmail("john.doe@jrestful.org");
    user.setPassword(passwordEncoder.encode("jrestful"));
    user.setRoles(Lists.newArrayList("ROLE_ADMIN"));
    user.setCity("Springfield");
    user.setEnabled(true);
    user = userService.insert(user);

    // login
    EmailPassword emailPassword = new EmailPassword("john.doe@jrestful.org", "jrestful");
    resultActions = mockMvc.perform( //
        post("/api-" + apiVersion + "/signin") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(emailPassword).asString()));
    String authToken = resultActions.andReturn().getResponse().getHeader(authHeader);
    Assert.assertNotNull(authToken);

    // create article1
    article1 = new Article("article1");
    resultActions = mockMvc.perform( //
        post("/api-" + apiVersion + "/rest/articles") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .header(authHeader, authToken) //
            .content(JsonUtils.toJson(article1).asString()));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    article1 = articleService.findByTitle("article1");
    Assert.assertNotNull(article1);
    Assert.assertEquals("article1", article1.getTitle());

    // get article1 by sequence
    resultActions = mockMvc.perform( //
        get("/api-" + apiVersion + "/rest/articles/{sequence}", new Object[] { article1.getSequence() }) //
            .param("by", "sequence"));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    resultActions //
        .andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(RestResource.HAL_MEDIA_TYPE)) //
        .andExpect(jsonPath("$.id", is(article1.getId()))) //
        .andExpect(jsonPath("$.sequence", is(article1.getSequence().intValue()))) //
        .andExpect(jsonPath("$.title", is(article1.getTitle()))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/api-" + apiVersion + "/rest/articles/" + article1.getId()))) //
        .andExpect(jsonPath("$._links.resource.href", is("http://resource.com")));

    // create article2
    Article article2 = new Article("article2");
    resultActions = mockMvc.perform( //
        post("/api-" + apiVersion + "/rest/articles") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .header(authHeader, authToken) //
            .content(JsonUtils.toJson(article2).asString()));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    article2 = articleService.findByTitle("article2");
    Assert.assertNotNull(article2);
    Assert.assertEquals("article2", article2.getTitle());
    resultActions //
        .andExpect(status().is(HttpStatus.CREATED.value())) //
        .andExpect(content().contentType(RestResource.HAL_MEDIA_TYPE)) //
        .andExpect(jsonPath("$.id", is(article2.getId()))) //
        .andExpect(jsonPath("$.sequence", is(article2.getSequence().intValue()))) //
        .andExpect(jsonPath("$.title", is(article2.getTitle()))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/api-" + apiVersion + "/rest/articles/" + article2.getId()))) //
        .andExpect(jsonPath("$._links.resource.href", is("http://resource.com")));

    // get article2
    resultActions = mockMvc.perform( //
        get("/api-" + apiVersion + "/rest/articles/{id}", new Object[] { article2.getId() }));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    resultActions //
        .andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(RestResource.HAL_MEDIA_TYPE)) //
        .andExpect(jsonPath("$.id", is(article2.getId()))) //
        .andExpect(jsonPath("$.sequence", is(article2.getSequence().intValue()))) //
        .andExpect(jsonPath("$.title", is(article2.getTitle()))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/api-" + apiVersion + "/rest/articles/" + article2.getId()))) //
        .andExpect(jsonPath("$._links.resource.href", is("http://resource.com")));

    // update article1 by sequence
    article1.setTitle("article1updated");
    resultActions = mockMvc.perform( //
        put("/api-" + apiVersion + "/rest/articles/{sequence}", new Object[] { article1.getSequence() }) //
            .param("by", "sequence") //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(article1).asString()));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    article1 = articleService.findByTitle("article1");
    Assert.assertNull(article1);
    article1 = articleService.findByTitle("article1updated");
    Assert.assertNotNull(article1);
    Assert.assertEquals("article1updated", article1.getTitle());
    resultActions //
        .andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(RestResource.HAL_MEDIA_TYPE)) //
        .andExpect(jsonPath("$.id", is(article1.getId()))) //
        .andExpect(jsonPath("$.sequence", is(article1.getSequence().intValue()))) //
        .andExpect(jsonPath("$.title", is(article1.getTitle()))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/api-" + apiVersion + "/rest/articles/" + article1.getId()))) //
        .andExpect(jsonPath("$._links.resource.href", is("http://resource.com")));

    // update article2
    article2.setTitle("article2updated");
    resultActions = mockMvc.perform( //
        put("/api-" + apiVersion + "/rest/articles/{id}", new Object[] { article2.getId() }) //
            .contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(article2).asString()));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    article2 = articleService.findByTitle("article2");
    Assert.assertNull(article2);
    article2 = articleService.findByTitle("article2updated");
    Assert.assertNotNull(article2);
    Assert.assertEquals("article2updated", article2.getTitle());
    resultActions //
        .andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(RestResource.HAL_MEDIA_TYPE)) //
        .andExpect(jsonPath("$.id", is(article2.getId()))) //
        .andExpect(jsonPath("$.sequence", is(article2.getSequence().intValue()))) //
        .andExpect(jsonPath("$.title", is(article2.getTitle()))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/api-" + apiVersion + "/rest/articles/" + article2.getId()))) //
        .andExpect(jsonPath("$._links.resource.href", is("http://resource.com")));

    // list articles
    resultActions = mockMvc.perform( //
        get("/api-" + apiVersion + "/rest/articles"));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    resultActions //
        .andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(RestResource.HAL_MEDIA_TYPE)) //
        .andExpect(jsonPath("$.pageIndex", is(0))) //
        .andExpect(jsonPath("$.pageSize", is(25))) //
        .andExpect(jsonPath("$.totalPages", is(1))) //
        .andExpect(jsonPath("$.totalItems", is(2))) //
        .andExpect(jsonPath("$._embedded", hasSize(2))) //
        .andExpect(jsonPath("$._embedded[0].id", is(article1.getId()))) //
        .andExpect(jsonPath("$._embedded[0].sequence", is(article1.getSequence().intValue()))) //
        .andExpect(jsonPath("$._embedded[0].title", is(article1.getTitle()))) //
        .andExpect(jsonPath("$._embedded[0]._links.self.href", is("http://localhost/api-" + apiVersion + "/rest/articles/" + article1.getId()))) //
        .andExpect(jsonPath("$._embedded[0]._links.resource.href", is("http://resource.com"))) //
        .andExpect(jsonPath("$._embedded[1].id", is(article2.getId()))) //
        .andExpect(jsonPath("$._embedded[1].sequence", is(article2.getSequence().intValue()))) //
        .andExpect(jsonPath("$._embedded[1].title", is(article2.getTitle()))) //
        .andExpect(jsonPath("$._embedded[1]._links.self.href", is("http://localhost/api-" + apiVersion + "/rest/articles/" + article2.getId()))) //
        .andExpect(jsonPath("$._embedded[1]._links.resource.href", is("http://resource.com"))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/api-" + apiVersion + "/rest/articles?pageIndex=0&pageSize=25"))) //
        .andExpect(jsonPath("$._links.items", hasSize(2))) //
        .andExpect(jsonPath("$._links.items[0].href", is("http://localhost/api-" + apiVersion + "/rest/articles/" + article1.getId()))) //
        .andExpect(jsonPath("$._links.items[1].href", is("http://localhost/api-" + apiVersion + "/rest/articles/" + article2.getId()))) //
        .andExpect(jsonPath("$._links.resources.href", is("http://resources.com")));

    // delete article1
    resultActions = mockMvc.perform( //
        delete("/api-" + apiVersion + "/rest/articles/{id}", new Object[] { article1.getId() }));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    resultActions //
        .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
    article1 = articleService.findByTitle("article1updated");
    Assert.assertNull(article1);

    // list articles
    resultActions = mockMvc.perform( //
        get("/api-" + apiVersion + "/rest/articles"));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    resultActions //
        .andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(RestResource.HAL_MEDIA_TYPE)) //
        .andExpect(jsonPath("$._embedded", hasSize(1))) //
        .andExpect(jsonPath("$._embedded[0].id", is(article2.getId()))) //
        .andExpect(jsonPath("$._embedded[0].sequence", is(article2.getSequence().intValue()))) //
        .andExpect(jsonPath("$._embedded[0].title", is(article2.getTitle()))) //
        .andExpect(jsonPath("$._embedded[0]._links.self.href", is("http://localhost/api-" + apiVersion + "/rest/articles/" + article2.getId()))) //
        .andExpect(jsonPath("$._embedded[0]._links.resource.href", is("http://resource.com"))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/api-" + apiVersion + "/rest/articles?pageIndex=0&pageSize=25"))) //
        .andExpect(jsonPath("$._links.items", hasSize(1))) //
        .andExpect(jsonPath("$._links.items[0].href", is("http://localhost/api-" + apiVersion + "/rest/articles/" + article2.getId()))) //
        .andExpect(jsonPath("$._links.resources.href", is("http://resources.com")));

    // delete article2 by sequence
    resultActions = mockMvc.perform( //
        delete("/api-" + apiVersion + "/rest/articles/{sequence}", new Object[] { article2.getSequence() }) //
            .param("by", "sequence"));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    resultActions //
        .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
    article2 = articleService.findByTitle("article2updated");
    Assert.assertNull(article2);

    // generate articles
    for (int i = 1; i <= 10; i++) {
      articleService.insert(new Article("generated" + i));
    }
    Assert.assertEquals(10, articleService.count());

    // list articles
    resultActions = mockMvc.perform( //
        get("/api-" + apiVersion + "/rest/articles?pageIndex=2&pageSize=2"));
    LOGGER.debug(resultActions.andReturn().getResponse().getContentAsString());
    resultActions //
        .andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(RestResource.HAL_MEDIA_TYPE)) //
        .andExpect(jsonPath("$.pageIndex", is(2))) //
        .andExpect(jsonPath("$.pageSize", is(2))) //
        .andExpect(jsonPath("$.totalPages", is(5))) //
        .andExpect(jsonPath("$.totalItems", is(10))) //
        .andExpect(jsonPath("$._embedded", hasSize(2))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/api-" + apiVersion + "/rest/articles?pageIndex=2&pageSize=2"))) //
        .andExpect(jsonPath("$._links.first.href", is("http://localhost/api-" + apiVersion + "/rest/articles?pageIndex=0&pageSize=2"))) //
        .andExpect(jsonPath("$._links.previous.href", is("http://localhost/api-" + apiVersion + "/rest/articles?pageIndex=1&pageSize=2"))) //
        .andExpect(jsonPath("$._links.next.href", is("http://localhost/api-" + apiVersion + "/rest/articles?pageIndex=3&pageSize=2"))) //
        .andExpect(jsonPath("$._links.last.href", is("http://localhost/api-" + apiVersion + "/rest/articles?pageIndex=4&pageSize=2"))) //
        .andExpect(jsonPath("$._links.resources.href", is("http://resources.com")));

  }

}
