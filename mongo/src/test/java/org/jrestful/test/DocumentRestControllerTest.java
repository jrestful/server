package org.jrestful.test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.jrestful.test.impl.business.TestService;
import org.jrestful.test.impl.data.documents.TestDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:jrestful/test/applicationContextTest.xml" })
@WebAppConfiguration
public class DocumentRestControllerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentRestControllerTest.class);

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
  public void testGet() throws Exception {
    testService.insert(new TestDocument("test1"));
    TestDocument document = testService.insert(new TestDocument("test2"));
    MvcResult result = mockMvc.perform(get("/test/{id}", new Object[] { document.getId() })) //
        .andExpect(jsonPath("$.content.id", is(document.getId()))) //
        .andExpect(jsonPath("$.content.sequence", is(2))) //
        .andExpect(jsonPath("$.content.label", is("test2"))) //
        .andExpect(jsonPath("$.links[0].rel", is("self"))) //
        .andExpect(jsonPath("$.links[0].href", is("http://localhost/test/" + document.getId()))) //
        .andReturn();
    LOGGER.debug(result.getResponse().getContentAsString());
  }

  @Test
  public void testList() throws Exception {
    MvcResult result = mockMvc.perform(get("/test")) //
        .andExpect(jsonPath("$.content", hasSize(2))) //
        .andExpect(jsonPath("$.links[0].rel", is("self"))) //
        .andExpect(jsonPath("$.links[0].href", is("http://localhost/test"))) //
        .andReturn();
    LOGGER.debug(result.getResponse().getContentAsString());
  }

}
