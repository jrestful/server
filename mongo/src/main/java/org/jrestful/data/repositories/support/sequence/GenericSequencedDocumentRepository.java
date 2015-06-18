package org.jrestful.data.repositories.support.sequence;

import org.jrestful.data.documents.support.sequence.GenericSequencedDocument;
import org.jrestful.data.repositories.support.GenericDocumentRepository;

/**
 * Generic interface for a sequenced document repository.
 * 
 * @param <D>
 *            The sequenced document type to manage.
 */
public interface GenericSequencedDocumentRepository<D extends GenericSequencedDocument> extends GenericDocumentRepository<D> {

  D findBySequence(long sequence);

}
