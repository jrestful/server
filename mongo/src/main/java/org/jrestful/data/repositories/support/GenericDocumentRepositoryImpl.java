package org.jrestful.data.repositories.support;

import org.jrestful.data.documents.support.GenericDocument;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

/**
 * Generic abstract class for a document repository.
 * 
 * @param <D>
 *          The document type to manage.
 */
public abstract class GenericDocumentRepositoryImpl<D extends GenericDocument> extends SimpleMongoRepository<D, String> implements
    GenericDocumentRepository<D> {

  protected final Class<D> documentClass;

  protected final MongoOperations mongoOperations;

  public GenericDocumentRepositoryImpl(Class<D> documentClass, MongoOperations mongoOperations) {
    super(getMetadata(documentClass, mongoOperations), mongoOperations);
    this.documentClass = documentClass;
    this.mongoOperations = mongoOperations;
  }

  @SuppressWarnings("unchecked")
  private static <D extends GenericDocument> MongoEntityInformation<D, String> getMetadata(Class<D> documentClass, MongoOperations mongoOperations) {
    MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoOperations.getConverter().getMappingContext();
    MongoPersistentEntity<?> entity = mappingContext.getPersistentEntity(documentClass);
    return new MappingMongoEntityInformation<>((MongoPersistentEntity<D>) entity);
  }

}
