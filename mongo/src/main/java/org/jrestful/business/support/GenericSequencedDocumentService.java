package org.jrestful.business.support;

import org.jrestful.data.documents.support.GenericSequencedDocument;

/**
 * Generic interface for a sequenced document service.
 * 
 * @param <D>
 *          The sequenced document type to manage.
 */
public interface GenericSequencedDocumentService<D extends GenericSequencedDocument> extends GenericDocumentService<D> {

  D findOneBySequence(long sequence);

  int deleteBySequence(long sequence);

}
