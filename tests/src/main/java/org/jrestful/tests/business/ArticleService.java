package org.jrestful.tests.business;

import org.jrestful.business.support.sequence.GenericSequencedDocumentService;
import org.jrestful.tests.data.documents.Article;

public interface ArticleService extends GenericSequencedDocumentService<Article> {

  Article findByTitle(String title);
  
}
