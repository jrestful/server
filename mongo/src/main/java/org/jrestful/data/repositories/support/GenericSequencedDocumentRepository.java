package org.jrestful.data.repositories.support;

import org.jrestful.data.documents.support.sequence.GenericSequencedDocument;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Generic interface for a sequenced document repository.
 * 
 * @param <D>
 *          The sequenced document type to manage.
 */
@NoRepositoryBean
public interface GenericSequencedDocumentRepository<D extends GenericSequencedDocument> extends GenericDocumentRepository<D> {

  D findBySequence(long sequence);

  // TODO wait for spring-data-mongodb 1.7.3.RELEASE so that void can be used
  int deleteBySequence(long sequence);

}
