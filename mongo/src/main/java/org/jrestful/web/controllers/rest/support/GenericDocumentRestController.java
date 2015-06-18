package org.jrestful.web.controllers.rest.support;

import java.util.List;

import org.jrestful.business.support.GenericDocumentService;
import org.jrestful.data.documents.support.GenericDocument;
import org.jrestful.web.controllers.rest.support.GenericRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Generic abstract class for a document REST controller.
 * 
 * @param <D>
 *            The document type to manage.
 */
public abstract class GenericDocumentRestController<D extends GenericDocument> extends GenericRestController {

  protected final GenericDocumentService<D> service;

  public GenericDocumentRestController(GenericDocumentService<D> service) {
    this.service = service;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<D>> list() {
    return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<D> get(@PathVariable String id) {
    D document = service.findOne(id);
    if (document == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<>(document, HttpStatus.OK);
    }
  }

  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<D> create(@RequestBody D document) {
    return new ResponseEntity<>(service.insert(document), HttpStatus.CREATED);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<D> update(@PathVariable String id, @RequestBody D document) {
    return new ResponseEntity<>(service.save(document), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<D> delete(@PathVariable String id) {
    service.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
