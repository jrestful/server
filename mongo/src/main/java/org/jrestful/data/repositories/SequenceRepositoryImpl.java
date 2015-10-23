package org.jrestful.data.repositories;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.concurrent.ConcurrentHashMap;

import org.jrestful.data.documents.Sequence;
import org.jrestful.data.documents.support.GenericSequencedDocument;
import org.jrestful.data.repositories.support.GenericDocumentRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * Manages auto-incremented sequenced.
 */
@Repository
public final class SequenceRepositoryImpl extends GenericDocumentRepositoryImpl<Sequence> implements SequenceRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(SequenceRepositoryImpl.class);

  private final ConcurrentHashMap<Class<? extends GenericSequencedDocument>, String> collectionsNames = new ConcurrentHashMap<>();

  @Autowired
  public SequenceRepositoryImpl(MongoOperations mongoOperations) {
    super(Sequence.class, mongoOperations);
  }

  @Override
  public Sequence findNext(GenericSequencedDocument document) {
    String collectionName = getCollectionName(document);
    Sequence sequence = findNext(collectionName);
    if (sequence == null) {
      initialize(collectionName);
      sequence = findNext(collectionName);
    }
    return sequence;
  }

  private String getCollectionName(GenericSequencedDocument document) {
    Class<? extends GenericSequencedDocument> documentClass = document.getClass();
    String collectionName = collectionsNames.get(documentClass);
    if (collectionName == null) {
      collectionName = documentClass.getAnnotation(Document.class).collection();
      collectionsNames.putIfAbsent(documentClass, collectionName);
    }
    return collectionName;
  }

  private Sequence findNext(String collectionName) {
    return mongoOperations.findAndModify( //
        query(where("_id").is(collectionName)), //
        new Update().inc("value", 1), //
        options().returnNew(true), //
        Sequence.class);
  }

  private void initialize(String collectionName) {
    Sequence sequence = new Sequence();
    sequence.setId(collectionName);
    sequence.setValue(0l);
    try {
      insert(sequence);
      LOGGER.debug("Sequence initialized for collection " + collectionName);
    } catch (DuplicateKeyException ignore) {
      // another thread initialized this collection sequence
    }
  }

}
