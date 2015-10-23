package org.jrestful.tests.components.article;

import org.jrestful.web.beans.RestResource;
import org.jrestful.web.beans.RestResources;
import org.jrestful.web.controllers.rest.support.GenericSequencedDocumentRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api-${app.apiVersion}/rest/articles", produces = RestResource.HAL_MEDIA_TYPE)
public class ArticleRestController extends GenericSequencedDocumentRestController<ArticleService, Article> {

  @Autowired
  public ArticleRestController(ArticleService service) {
    super(service);
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
