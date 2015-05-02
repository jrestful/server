package org.jrestful.data.repositories.support;

import org.jrestful.data.documents.support.GenericDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GenericDocumentRepository<D extends GenericDocument> extends MongoRepository<D, String> {

}
