package org.jrestful.tests.data.repositories;

import org.jrestful.data.repositories.support.GenericSequencedDocumentRepository;
import org.jrestful.tests.data.documents.Article;

public interface ArticleRepository extends GenericSequencedDocumentRepository<Article> {

  Article findByTitle(String title);
  
}
