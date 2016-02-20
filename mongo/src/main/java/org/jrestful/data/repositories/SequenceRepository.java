package org.jrestful.data.repositories;

import org.jrestful.data.documents.Sequence;
import org.jrestful.data.documents.support.GenericSequencedDocument;
import org.jrestful.data.repositories.support.GenericDocumentRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Manages auto-incremented sequenced.
 */
@NoRepositoryBean
public interface SequenceRepository extends GenericDocumentRepository<Sequence> {

  Sequence findNext(GenericSequencedDocument document);

}
