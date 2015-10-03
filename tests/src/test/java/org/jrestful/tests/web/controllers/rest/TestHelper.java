package org.jrestful.tests.web.controllers.rest;

import org.jrestful.tests.components.article.ArticleService;
import org.jrestful.tests.components.user.UserService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:jrestful/tests/applicationContext.xml" })
@WebAppConfiguration
public abstract class TestHelper {

  protected MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private FilterChainProxy springSecurityFilterChain;

  @Autowired
  protected ArticleService articleService;

  @Autowired
  protected UserService userService;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();
    articleService.deleteAll();
    userService.deleteAll();
  }

}
