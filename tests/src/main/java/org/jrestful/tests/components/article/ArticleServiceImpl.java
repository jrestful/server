package org.jrestful.tests.components.article;

import org.jrestful.business.support.GenericSequencedDocumentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl extends GenericSequencedDocumentServiceImpl<ArticleRepository, Article> implements ArticleService {

  @Autowired
  public ArticleServiceImpl(ArticleRepository repository) {
    super(repository);
  }

  @Override
  public Article findByTitle(String title) {
    return repository.findByTitle(title);
  }

}
