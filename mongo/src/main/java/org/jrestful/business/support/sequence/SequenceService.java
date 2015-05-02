package org.jrestful.business.support.sequence;

import org.jrestful.business.support.GenericDocumentService;
import org.jrestful.data.documents.support.sequence.GenericSequencedDocument;
import org.jrestful.data.documents.support.sequence.Sequence;

public interface SequenceService extends GenericDocumentService<Sequence> {

  Sequence findNext(GenericSequencedDocument document);

}
