package org.jrestful.data.repositories.support.sequence;

import org.jrestful.data.documents.support.sequence.GenericSequencedDocument;
import org.jrestful.data.repositories.support.GenericDocumentRepositoryImpl;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * Generic abstract class for a sequenced document repository.
 * 
 * @param <D>
 *            The sequenced document type to manage.
 */
public abstract class GenericSequencedDocumentRepositoryImpl<D extends GenericSequencedDocument> extends GenericDocumentRepositoryImpl<D> implements
    GenericSequencedDocumentRepository<D> {

  public GenericSequencedDocumentRepositoryImpl(Class<D> documentClass, MongoOperations mongoOperations) {
    super(documentClass, mongoOperations);
  }

}
