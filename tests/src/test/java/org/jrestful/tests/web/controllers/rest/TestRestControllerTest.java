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

import org.jrestful.tests.business.TestService;
import org.jrestful.tests.data.documents.TestDocument;
import org.jrestful.util.JsonUtils;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:jrestful/tests/applicationContextTest.xml" })
@WebAppConfiguration
public class TestRestControllerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestRestControllerTest.class);

  private MockMvc mockMvc;

  @Autowired
  private TestService testService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  public void testRest() throws Exception {

    // create test1 document
    TestDocument test1 = new TestDocument("test1");
    mockMvc.perform( //
        post("/test").contentType(MediaType.APPLICATION_JSON_VALUE).content(JsonUtils.toJson(test1).asString()));
    test1 = testService.findByLabel("test1");
    Assert.assertNotNull(test1);
    Assert.assertEquals("test1", test1.getLabel());

    // create test2 document and check
    TestDocument test2 = new TestDocument("test2");
    ResultActions resultActions2 = mockMvc.perform( //
        post("/test").contentType(MediaType.APPLICATION_JSON_VALUE).content(JsonUtils.toJson(test2).asString()));
    test2 = testService.findByLabel("test2");
    Assert.assertNotNull(test2);
    Assert.assertEquals("test2", test2.getLabel());
    MvcResult result2 = resultActions2 //
        .andExpect(status().is(HttpStatus.CREATED.value())) //
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)) //
        .andExpect(jsonPath("$.id", is(test2.getId()))) //
        .andExpect(jsonPath("$.sequence", is(2))) //
        .andExpect(jsonPath("$.label", is("test2"))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/test/" + test2.getId()))) //
        .andReturn();
    LOGGER.debug("Create test2 document: " + result2.getResponse().getContentAsString());

    // get test2 document and check
    MvcResult result3 = mockMvc.perform(get("/test/{id}", new Object[] { test2.getId() })) //
        .andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)) //
        .andExpect(jsonPath("$.id", is(test2.getId()))) //
        .andExpect(jsonPath("$.sequence", is(2))) //
        .andExpect(jsonPath("$.label", is("test2"))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/test/" + test2.getId()))) //
        .andReturn();
    LOGGER.debug("Get test2 document: " + result3.getResponse().getContentAsString());

    // update test2 document and check
    test2.setLabel("test2updated");
    ResultActions resultActions4 = mockMvc.perform( //
        put("/test/{id}", new Object[] { test2.getId() }).contentType(MediaType.APPLICATION_JSON_VALUE).content(JsonUtils.toJson(test2).asString()));
    test2 = testService.findByLabel("test2");
    Assert.assertNull(test2);
    test2 = testService.findByLabel("test2updated");
    Assert.assertNotNull(test2);
    Assert.assertEquals("test2updated", test2.getLabel());
    MvcResult result4 = resultActions4 //
        .andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)) //
        .andExpect(jsonPath("$.id", is(test2.getId()))) //
        .andExpect(jsonPath("$.sequence", is(2))) //
        .andExpect(jsonPath("$.label", is("test2updated"))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/test/" + test2.getId()))) //
        .andReturn();
    LOGGER.debug("Update test2 document: " + result4.getResponse().getContentAsString());

    // list documents and check
    MvcResult result5 = mockMvc.perform(get("/test")) //
        .andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)) //
        .andExpect(jsonPath("$._embedded", hasSize(2))) //
        .andExpect(jsonPath("$._embedded[0].id", is(test1.getId()))) //
        .andExpect(jsonPath("$._embedded[0].sequence", is(1))) //
        .andExpect(jsonPath("$._embedded[0].label", is("test1"))) //
        .andExpect(jsonPath("$._embedded[0]._links.self.href", is("http://localhost/test/" + test1.getId()))) //
        .andExpect(jsonPath("$._embedded[1].id", is(test2.getId()))) //
        .andExpect(jsonPath("$._embedded[1].sequence", is(2))) //
        .andExpect(jsonPath("$._embedded[1].label", is("test2updated"))) //
        .andExpect(jsonPath("$._embedded[1]._links.self.href", is("http://localhost/test/" + test2.getId()))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/test"))) //
        .andExpect(jsonPath("$._links.item", hasSize(2))) //
        .andExpect(jsonPath("$._links.item[0].href", is("http://localhost/test/" + test1.getId()))) //
        .andExpect(jsonPath("$._links.item[1].href", is("http://localhost/test/" + test2.getId()))) //
        .andReturn();
    LOGGER.debug("List documents: " + result5.getResponse().getContentAsString());

    // delete test1 document and check
    mockMvc.perform(delete("/test/{id}", new Object[] { test1.getId() })) //
        .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
    test1 = testService.findByLabel("test1");
    Assert.assertNull(test1);

    // list documents and check
    MvcResult result7 = mockMvc.perform(get("/test")) //
        .andExpect(status().is(HttpStatus.OK.value())) //
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)) //
        .andExpect(jsonPath("$._embedded", hasSize(1))) //
        .andExpect(jsonPath("$._embedded[0].id", is(test2.getId()))) //
        .andExpect(jsonPath("$._embedded[0].sequence", is(2))) //
        .andExpect(jsonPath("$._embedded[0].label", is("test2updated"))) //
        .andExpect(jsonPath("$._embedded[0]._links.self.href", is("http://localhost/test/" + test2.getId()))) //
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/test"))) //
        .andExpect(jsonPath("$._links.item", hasSize(1))) //
        .andExpect(jsonPath("$._links.item[0].href", is("http://localhost/test/" + test2.getId()))) //
        .andReturn();
    LOGGER.debug("List documents: " + result7.getResponse().getContentAsString());

  }

}
