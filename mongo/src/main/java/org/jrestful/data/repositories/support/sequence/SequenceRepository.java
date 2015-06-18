package org.jrestful.data.repositories.support.sequence;

import org.jrestful.data.documents.support.sequence.GenericSequencedDocument;
import org.jrestful.data.documents.support.sequence.Sequence;
import org.jrestful.data.repositories.support.GenericDocumentRepository;

/**
 * Manages auto-incremented sequenced.
 */
public interface SequenceRepository extends GenericDocumentRepository<Sequence> {

  Sequence findNext(GenericSequencedDocument document);

}
