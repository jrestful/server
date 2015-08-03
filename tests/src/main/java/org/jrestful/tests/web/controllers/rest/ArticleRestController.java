package org.jrestful.tests.web.controllers.rest;

import org.jrestful.tests.business.ArticleService;
import org.jrestful.tests.data.documents.Article;
import org.jrestful.web.controllers.rest.support.GenericDocumentRestController;
import org.jrestful.web.hateoas.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/articles", produces = Resource.HAL_MEDIA_TYPE)
public class ArticleRestController extends GenericDocumentRestController<Article> {

  @Autowired
  public ArticleRestController(ArticleService service) {
    super(service);
  }
  
  @Override
  @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
  public ResponseEntity<Resource<Article>> create(@RequestBody Article document) {
    return super.create(document);
  }

}
