package org.jrestful.data.repositories.support.sequence;

import org.jrestful.data.documents.support.sequence.GenericSequencedDocument;
import org.jrestful.data.documents.support.sequence.Sequence;
import org.jrestful.data.repositories.support.GenericDocumentRepository;

public interface SequenceRepository extends GenericDocumentRepository<Sequence> {

  Sequence findNext(GenericSequencedDocument document);

}
