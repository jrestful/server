package org.jrestful.data.repositories.support;

import org.jrestful.data.documents.support.GenericSequencedDocument;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Generic interface for a sequenced document repository.
 * 
 * @param <D>
 *          The sequenced document type to manage.
 */
@NoRepositoryBean
public interface GenericSequencedDocumentRepository<D extends GenericSequencedDocument> extends GenericDocumentRepository<D> {

  D findOneBySequence(long sequence);

  int deleteBySequence(long sequence);

}
