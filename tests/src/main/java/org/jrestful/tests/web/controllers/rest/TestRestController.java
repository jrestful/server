package org.jrestful.tests.web.controllers.rest;

import org.jrestful.tests.business.TestService;
import org.jrestful.tests.data.documents.TestDocument;
import org.jrestful.web.controllers.rest.support.GenericDocumentRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestRestController extends GenericDocumentRestController<TestDocument> {

  @Autowired
  public TestRestController(TestService testService) {
    super(testService);
  }

}
