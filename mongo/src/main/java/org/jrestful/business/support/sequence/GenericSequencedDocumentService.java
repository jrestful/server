package org.jrestful.business.support.sequence;

import org.jrestful.business.support.GenericDocumentService;
import org.jrestful.data.documents.support.sequence.GenericSequencedDocument;

/**
 * Generic interface for a sequenced document service.
 * 
 * @param <D>
 *            The sequenced document type to manage.
 */
public interface GenericSequencedDocumentService<D extends GenericSequencedDocument> extends GenericDocumentService<D> {

  D findBySequence(long sequence);

}
