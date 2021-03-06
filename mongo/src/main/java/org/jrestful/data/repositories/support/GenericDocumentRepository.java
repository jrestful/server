package org.jrestful.data.repositories.support;

import org.jrestful.data.documents.support.GenericDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Generic interface for a document repository.
 * 
 * @param <D>
 *          The document type to manage.
 */
@NoRepositoryBean
public interface GenericDocumentRepository<D extends GenericDocument> extends MongoRepository<D, String> {

}
