package org.jrestful.tests.web.controllers.rest;

import org.jrestful.tests.business.ArticleService;
import org.jrestful.tests.data.documents.Article;
import org.jrestful.web.controllers.rest.support.GenericDocumentRestController;
import org.jrestful.web.hateoas.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/articles", produces = Resource.HAL_MEDIA_TYPE)
public class ArticleRestController extends GenericDocumentRestController<Article> {

  @Autowired
  public ArticleRestController(ArticleService service) {
    super(service);
  }

}
