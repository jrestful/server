package org.jrestful.business;

import org.jrestful.business.support.GenericDocumentServiceImpl;
import org.jrestful.data.documents.Sequence;
import org.jrestful.data.documents.support.GenericSequencedDocument;
import org.jrestful.data.repositories.SequenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Manages auto-incremented sequences.
 */
@Service
public final class SequenceServiceImpl extends GenericDocumentServiceImpl<SequenceRepository, Sequence> implements SequenceService {

  @Autowired
  public SequenceServiceImpl(SequenceRepository repository) {
    super(repository);
  }

  @Override
  public Sequence findNext(GenericSequencedDocument document) {
    return repository.findNext(document);
  }

}
