package org.jrestful.tests.components.article;

import org.apache.commons.lang3.StringUtils;
import org.jrestful.business.exceptions.HttpStatusException;
import org.jrestful.business.exceptions.PayloadNotValidException;
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
  
  @Override
  public void validatePayload(Article payload) throws HttpStatusException {
    if (StringUtils.isBlank(payload.getTitle())) {
      throw new PayloadNotValidException();
    }
  }

  @Override
  public void copyPayload(Article fromPayload, Article toDocument) {
    toDocument.setTitle(fromPayload.getTitle());
  }

}
