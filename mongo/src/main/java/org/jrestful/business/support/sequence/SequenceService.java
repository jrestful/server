package org.jrestful.business.support.sequence;

import org.jrestful.business.support.GenericDocumentService;
import org.jrestful.data.documents.support.GenericSequencedDocument;
import org.jrestful.data.documents.support.sequence.Sequence;

/**
 * Manages auto-incremented sequences.
 */
public interface SequenceService extends GenericDocumentService<Sequence> {

  Sequence findNext(GenericSequencedDocument document);

}
