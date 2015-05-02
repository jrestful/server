package org.jrestful.data.repositories.support.sequence;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.jrestful.data.documents.support.sequence.GenericSequencedDocument;
import org.jrestful.data.documents.support.sequence.Sequence;
import org.jrestful.data.repositories.support.GenericDocumentRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public final class SequenceRepositoryImpl extends GenericDocumentRepositoryImpl<Sequence> implements SequenceRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(SequenceRepositoryImpl.class);

  private final ConcurrentHashMap<Class<? extends GenericSequencedDocument>, String> collectionsNames = new ConcurrentHashMap<>();

  private final ConcurrentHashMap<String, Long> initialValues = new ConcurrentHashMap<>();

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
    Query query = new Query(Criteria.where("_id").is(collectionName));
    Update update = new Update().inc("value", 1);
    FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
    return mongoOperations.findAndModify(query, update, options, Sequence.class);
  }

  private void initialize(String collectionName) {
    Long initialValue = initialValues.get(collectionName);
    if (initialValue == null) {
      initialValue = 0l;
      initialValues.putIfAbsent(collectionName, initialValue);
    }
    Sequence sequence = new Sequence();
    sequence.setId(collectionName);
    sequence.setValue(initialValue);
    try {
      insert(sequence);
      LOGGER.debug("Sequence initialized at " + initialValue + " for collection " + collectionName);
    } catch (DuplicateKeyException ignore) {
      // another thread initialized this collection sequence
    }
  }

  @PostConstruct
  protected void init() {
    Query query = new Query();
    List<Sequence> sequences = mongoOperations.findAllAndRemove(query, Sequence.class);
    for (Sequence sequence : sequences) {
      initialValues.put(sequence.getId(), sequence.getValue());
    }
  }

}
