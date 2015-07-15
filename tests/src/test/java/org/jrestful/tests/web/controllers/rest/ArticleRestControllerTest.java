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

import org.jrestful.tests.business.ArticleService;
import org.jrestful.tests.data.documents.Article;
import org.jrestful.util.JsonUtils;
import org.jrestful.web.hateoas.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:jrestful/tests/applicationContext.xml" })
@WebAppConfiguration
public class ArticleRestControllerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArticleRestControllerTest.class);

  private MockMvc mockMvc;

  @Autowired
  private ArticleService articleService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  public void testRest() throws Exception {

    // create article1
    Article article1 = new Article("article1");
    ResultActions resultActions1 = mockMvc.perform( //
        post("/articles").contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(article1).asString()));
    LOGGER.debug(resultActions1.andReturn().getResponse().getContentAsString());
    article1 = articleService.findByTitle("article1");
    Assert.assertNotNull(article1);
    Assert.assertEquals("article1", article1.getTitle());

    // create article2
    Article article2 = new Article("article2");
    ResultActions resultActions2 = mockMvc.perform( //
        post("/articles").contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(article2).asString()));
    LOGGER.debug(resultActions2.andReturn().getResponse().getContentAsString());
    article2 = articleService.findByTitle("article2");
    Assert.assertNotNull(article2);
    Assert.assertEquals("article2", article2.getTitle());
    resultActions2 //
        .andExpect(status().is(HttpStatus.CREATED.value())) //
        .andExpect(content().contentType(Resource.HAL_MEDIA_TYPE)) //
        .andExpect(jsonPath("$.id", is(article2.getId()))) //
        .andExpect(jsonPath("$.sequence", is(2))) //
        .andExpect(jsonPath("$.title", is("article2"))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/articles/" + article2.getId())));

    // get article2
    ResultActions resultActions3 = mockMvc.perform( //
        get("/articles/{id}", new Object[] { article2.getId() }));
    LOGGER.debug(resultActions3.andReturn().getResponse().getContentAsString());
    resultActions3 //
        .andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(Resource.HAL_MEDIA_TYPE)) //
        .andExpect(jsonPath("$.id", is(article2.getId()))) //
        .andExpect(jsonPath("$.sequence", is(2))) //
        .andExpect(jsonPath("$.title", is("article2"))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/articles/" + article2.getId())));

    // update article2
    article2.setTitle("article2updated");
    ResultActions resultActions4 = mockMvc.perform( //
        put("/articles/{id}", new Object[] { article2.getId() }).contentType(MediaType.APPLICATION_JSON_VALUE) //
            .content(JsonUtils.toJson(article2).asString()));
    LOGGER.debug(resultActions4.andReturn().getResponse().getContentAsString());
    article2 = articleService.findByTitle("article2");
    Assert.assertNull(article2);
    article2 = articleService.findByTitle("article2updated");
    Assert.assertNotNull(article2);
    Assert.assertEquals("article2updated", article2.getTitle());
    resultActions4 //
        .andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(Resource.HAL_MEDIA_TYPE)) //
        .andExpect(jsonPath("$.id", is(article2.getId()))) //
        .andExpect(jsonPath("$.sequence", is(2))) //
        .andExpect(jsonPath("$.title", is("article2updated"))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/articles/" + article2.getId())));

    // list articles
    ResultActions resultActions5 = mockMvc.perform( //
        get("/articles"));
    LOGGER.debug(resultActions5.andReturn().getResponse().getContentAsString());
    resultActions5 //
        .andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(Resource.HAL_MEDIA_TYPE)) //
        .andExpect(jsonPath("$.pageIndex", is(0))) //
        .andExpect(jsonPath("$.pageSize", is(25))) //
        .andExpect(jsonPath("$.totalPages", is(1))) //
        .andExpect(jsonPath("$.totalItems", is(2))) //
        .andExpect(jsonPath("$._embedded", hasSize(2))) //
        .andExpect(jsonPath("$._embedded[0].id", is(article1.getId()))) //
        .andExpect(jsonPath("$._embedded[0].sequence", is(1))) //
        .andExpect(jsonPath("$._embedded[0].title", is("article1"))) //
        .andExpect(jsonPath("$._embedded[0]._links.self.href", is("http://localhost/articles/" + article1.getId()))) //
        .andExpect(jsonPath("$._embedded[1].id", is(article2.getId()))) //
        .andExpect(jsonPath("$._embedded[1].sequence", is(2))) //
        .andExpect(jsonPath("$._embedded[1].title", is("article2updated"))) //
        .andExpect(jsonPath("$._embedded[1]._links.self.href", is("http://localhost/articles/" + article2.getId()))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/articles?pageIndex=0&pageSize=25"))) //
        .andExpect(jsonPath("$._links.items", hasSize(2))) //
        .andExpect(jsonPath("$._links.items[0].href", is("http://localhost/articles/" + article1.getId()))) //
        .andExpect(jsonPath("$._links.items[1].href", is("http://localhost/articles/" + article2.getId())));

    // delete article1
    ResultActions resultActions6 = mockMvc.perform( //
        delete("/articles/{id}", new Object[] { article1.getId() }));
    LOGGER.debug(resultActions6.andReturn().getResponse().getContentAsString());
    resultActions6 //
        .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
    article1 = articleService.findByTitle("article1");
    Assert.assertNull(article1);

    // list articles
    ResultActions resultActions7 = mockMvc.perform( //
        get("/articles"));
    LOGGER.debug(resultActions7.andReturn().getResponse().getContentAsString());
    resultActions7 //
        .andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(Resource.HAL_MEDIA_TYPE)) //
        .andExpect(jsonPath("$._embedded", hasSize(1))) //
        .andExpect(jsonPath("$._embedded[0].id", is(article2.getId()))) //
        .andExpect(jsonPath("$._embedded[0].sequence", is(2))) //
        .andExpect(jsonPath("$._embedded[0].title", is("article2updated"))) //
        .andExpect(jsonPath("$._embedded[0]._links.self.href", is("http://localhost/articles/" + article2.getId()))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/articles?pageIndex=0&pageSize=25"))) //
        .andExpect(jsonPath("$._links.items", hasSize(1))) //
        .andExpect(jsonPath("$._links.items[0].href", is("http://localhost/articles/" + article2.getId())));

    // generate articles
    for (int i = 1; i <= 9; i++) {
      articleService.insert(new Article("generated" + i));
    }
    Assert.assertEquals(10, articleService.count());

    // list articles
    ResultActions resultActions8 = mockMvc.perform( //
        get("/articles?pageIndex=2&pageSize=2"));
    LOGGER.debug(resultActions8.andReturn().getResponse().getContentAsString());
    resultActions8 //
        .andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(Resource.HAL_MEDIA_TYPE)) //
        .andExpect(jsonPath("$.pageIndex", is(2))) //
        .andExpect(jsonPath("$.pageSize", is(2))) //
        .andExpect(jsonPath("$.totalPages", is(5))) //
        .andExpect(jsonPath("$.totalItems", is(10))) //
        .andExpect(jsonPath("$._embedded", hasSize(2))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/articles?pageIndex=2&pageSize=2"))) //
        .andExpect(jsonPath("$._links.first.href", is("http://localhost/articles?pageIndex=0&pageSize=2"))) //
        .andExpect(jsonPath("$._links.previous.href", is("http://localhost/articles?pageIndex=1&pageSize=2"))) //
        .andExpect(jsonPath("$._links.next.href", is("http://localhost/articles?pageIndex=3&pageSize=2"))) //
        .andExpect(jsonPath("$._links.last.href", is("http://localhost/articles?pageIndex=4&pageSize=2")));

  }

}
