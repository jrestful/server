package org.jrestful.tests.components.article;

import org.jrestful.data.repositories.support.GenericSequencedDocumentRepository;

public interface ArticleRepository extends GenericSequencedDocumentRepository<Article> {

  Article findByTitle(String title);
  
}
