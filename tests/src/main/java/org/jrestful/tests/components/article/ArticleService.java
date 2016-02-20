package org.jrestful.tests.components.article;

import org.jrestful.business.support.GenericSequencedDocumentService;

public interface ArticleService extends GenericSequencedDocumentService<Article> {

  Article findByTitle(String title);
  
}
