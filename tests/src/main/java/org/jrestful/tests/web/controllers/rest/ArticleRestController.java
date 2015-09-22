package org.jrestful.tests.web.controllers.rest;

import org.jrestful.tests.business.ArticleService;
import org.jrestful.tests.data.documents.Article;
import org.jrestful.web.controllers.rest.support.GenericSequencedDocumentRestController;
import org.jrestful.web.hateoas.RestResource;
import org.jrestful.web.hateoas.RestResources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${url.articles}", produces = RestResource.HAL_MEDIA_TYPE)
public class ArticleRestController extends GenericSequencedDocumentRestController<ArticleService, Article> {

  @Autowired
  public ArticleRestController(ArticleService service) {
    super(service);
  }

  @Override
  @PreAuthorize(value = "hasRole('ADMIN')")
  public ResponseEntity<RestResource<Article>> create(@RequestBody Article document) {
    return super.create(document);
  }

  @Override
  protected void addAdditionalLinks(RestResource<Article> resource) {
    resource.addLink("resource", "http://resource.com");
  }

  @Override
  protected void addAdditionalLinks(RestResources<Article> resources) {
    resources.addLink("resources", "http://resources.com");
  }

}
