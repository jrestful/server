package org.jrestful.business.support;

import java.util.List;

import org.jrestful.data.documents.support.GenericDocument;
import org.jrestful.data.repositories.support.GenericDocumentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public abstract class GenericDocumentServiceImpl<R extends GenericDocumentRepository<D>, D extends GenericDocument> extends GenericServiceImpl
    implements GenericDocumentService<D> {

  protected final R repository;

  public GenericDocumentServiceImpl(R repository) {
    this.repository = repository;
  }

  @Override
  public long count() {
    return repository.count();
  }

  @Override
  public void delete(String id) {
    repository.delete(id);
  }

  @Override
  public void delete(Iterable<D> documents) {
    repository.delete(documents);
  }

  @Override
  public void delete(D document) {
    repository.delete(document);
  }

  @Override
  public void deleteAll() {
    repository.deleteAll();
  }

  @Override
  public boolean exists(String id) {
    return repository.exists(id);
  }

  @Override
  public List<D> findAll() {
    return repository.findAll();
  }

  @Override
  public Iterable<D> findAll(Iterable<String> ids) {
    return repository.findAll(ids);
  }

  @Override
  public Page<D> findAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  @Override
  public List<D> findAll(Sort sort) {
    return repository.findAll(sort);
  }

  @Override
  public D findOne(String id) {
    return repository.findOne(id);
  }

  @Override
  public List<D> insert(Iterable<D> documents) {
    return repository.insert(documents);
  }

  @Override
  public D insert(D document) {
    return repository.insert(document);
  }

  @Override
  public List<D> save(Iterable<D> documents) {
    return repository.save(documents);
  }

  @Override
  public D save(D document) {
    return repository.save(document);
  }

}
