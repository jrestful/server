package org.jrestful.business.support;

import java.util.List;

import org.jrestful.business.support.GenericService;
import org.jrestful.data.documents.support.GenericDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Generic interface for a document service.
 * 
 * @param <D>
 *            The document type to manage.
 */
public interface GenericDocumentService<D extends GenericDocument> extends GenericService {

  long count();

  void delete(String id);

  void delete(Iterable<D> documents);

  void delete(D document);

  void deleteAll();

  boolean exists(String id);

  List<D> findAll();

  Iterable<D> findAll(Iterable<String> ids);

  Page<D> findAll(Pageable pageable);

  List<D> findAll(Sort sort);

  D findOne(String id);

  List<D> insert(Iterable<D> documents);

  D insert(D document);

  List<D> save(Iterable<D> documents);

  D save(D document);

}
