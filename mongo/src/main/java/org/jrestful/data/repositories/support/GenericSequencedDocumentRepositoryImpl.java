package org.jrestful.data.repositories.support;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.jrestful.data.documents.support.sequence.GenericSequencedDocument;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * Generic abstract class for a sequenced document repository.
 * 
 * @param <D>
 *          The sequenced document type to manage.
 */
public abstract class GenericSequencedDocumentRepositoryImpl<D extends GenericSequencedDocument> extends GenericDocumentRepositoryImpl<D> implements
    GenericSequencedDocumentRepository<D> {

  public GenericSequencedDocumentRepositoryImpl(Class<D> documentClass, MongoOperations mongoOperations) {
    super(documentClass, mongoOperations);
  }

  @Override
  public final D findBySequence(long sequence) {
    return mongoOperations.findOne(query(where("sequence").is(sequence)), documentClass);
  }

  @Override
  public final int deleteBySequence(long sequence) {
    return mongoOperations.remove(query(where("sequence").is(sequence)), documentClass).getN();
  }

}
