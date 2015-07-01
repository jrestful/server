package org.jrestful.test.impl.web.controllers.rest;

import org.jrestful.test.impl.business.TestService;
import org.jrestful.test.impl.data.documents.TestDocument;
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
