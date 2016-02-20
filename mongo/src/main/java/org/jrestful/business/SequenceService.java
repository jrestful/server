package org.jrestful.business;

import org.jrestful.business.support.GenericDocumentService;
import org.jrestful.data.documents.Sequence;
import org.jrestful.data.documents.support.GenericSequencedDocument;

/**
 * Manages auto-incremented sequences.
 */
public interface SequenceService extends GenericDocumentService<Sequence> {

  Sequence findNext(GenericSequencedDocument document);

}
