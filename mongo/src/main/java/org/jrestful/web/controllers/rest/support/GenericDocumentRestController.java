package org.jrestful.web.controllers.rest.support;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.jrestful.business.support.GenericDocumentService;
import org.jrestful.data.documents.support.GenericDocument;
import org.jrestful.web.hateoas.Resource;
import org.jrestful.web.hateoas.ResourceList;
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
 *          The document type to manage.
 */
public abstract class GenericDocumentRestController<D extends GenericDocument> extends GenericRestController {

  protected final GenericDocumentService<D> service;

  public GenericDocumentRestController(GenericDocumentService<D> service) {
    this.service = service;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<ResourceList<D>> list() {
    List<D> documents = service.findAll();
    List<Resource<D>> resources = new ArrayList<>();
    for (D document : documents) {
      Resource<D> resource = new Resource<>(document, linkTo(methodOn(getClass()).get(document.getId())));
      resources.add(resource);
    }
    ResourceList<D> resourceList = new ResourceList<>(resources, linkTo(methodOn(getClass()).list()));
    return new ResponseEntity<>(resourceList, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Resource<D>> get(@PathVariable String id) {
    D document = service.findOne(id);
    if (document == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      Resource<D> resource = new Resource<>(document, linkTo(methodOn(getClass()).get(document.getId())));
      return new ResponseEntity<>(resource, HttpStatus.OK);
    }
  }

  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Resource<D>> create(@RequestBody D document) {
    document = service.insert(document);
    Resource<D> resource = new Resource<>(document, linkTo(methodOn(getClass()).get(document.getId())));
    return new ResponseEntity<>(resource, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Resource<D>> update(@PathVariable String id, @RequestBody D document) {
    document = service.save(document);
    Resource<D> resource = new Resource<>(document, linkTo(methodOn(getClass()).get(document.getId())));
    return new ResponseEntity<>(resource, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@PathVariable String id) {
    service.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
