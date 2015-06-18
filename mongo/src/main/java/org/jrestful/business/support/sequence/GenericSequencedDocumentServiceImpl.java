package org.jrestful.business.support.sequence;

import java.util.List;

import org.jrestful.business.support.GenericDocumentServiceImpl;
import org.jrestful.data.documents.support.sequence.GenericSequencedDocument;
import org.jrestful.data.documents.support.sequence.Sequence;
import org.jrestful.data.repositories.support.sequence.GenericSequencedDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Generic abstract class for a sequenced document service.
 * 
 * @param <R>
 *            The repository of the sequenced document type to manage.
 * @param <D>
 *            The sequenced document type to manage.
 */
public abstract class GenericSequencedDocumentServiceImpl<R extends GenericSequencedDocumentRepository<D>, D extends GenericSequencedDocument>
    extends GenericDocumentServiceImpl<R, D> implements GenericSequencedDocumentService<D> {

  @Autowired
  private SequenceService sequenceService;

  public GenericSequencedDocumentServiceImpl(R repository) {
    super(repository);
  }

  @Override
  public D findBySequence(long sequence) {
    return repository.findBySequence(sequence);
  }

  @Override
  public List<D> insert(Iterable<D> documents) {
    for (D document : documents) {
      setSequence(document);
    }
    return super.insert(documents);
  }

  @Override
  public D insert(D document) {
    setSequence(document);
    return super.insert(document);
  }

  @Override
  public List<D> save(Iterable<D> documents) {
    for (D document : documents) {
      setSequence(document);
    }
    return super.save(documents);
  }

  @Override
  public D save(D document) {
    setSequence(document);
    return super.save(document);
  }

  private void setSequence(D document) {
    if (document.getSequence() == null) {
      Sequence sequence = sequenceService.findNext(document);
      document.setSequence(sequence.getValue());
    }
  }

}
