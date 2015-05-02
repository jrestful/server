package org.jrestful.business.support.sequence;

import org.jrestful.business.support.GenericDocumentServiceImpl;
import org.jrestful.data.documents.support.sequence.GenericSequencedDocument;
import org.jrestful.data.documents.support.sequence.Sequence;
import org.jrestful.data.repositories.support.sequence.SequenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
