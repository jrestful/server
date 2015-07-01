package org.jrestful.web.controllers.rest.support;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.jrestful.business.support.GenericDocumentService;
import org.jrestful.data.documents.support.GenericDocument;
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
  public ResponseEntity<RestResource<List<RestResource<D>>>> list() {
    List<D> documents = service.findAll();
    List<RestResource<D>> resources = new ArrayList<RestResource<D>>();
    for (D document : documents) {
      RestResource<D> resource = new RestResource<>(document);
      resource.add(linkTo(methodOn(getClass()).get(resource.getContent().getId())).withSelfRel());
      resources.add(resource);
    }
    RestResource<List<RestResource<D>>> resource = new RestResource<>(resources);
    resource.add(linkTo(methodOn(getClass()).list()).withSelfRel());
    return new ResponseEntity<>(resource, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<RestResource<D>> get(@PathVariable String id) {
    D document = service.findOne(id);
    if (document == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      RestResource<D> resource = new RestResource<>(document);
      resource.add(linkTo(methodOn(getClass()).get(resource.getContent().getId())).withSelfRel());
      return new ResponseEntity<>(resource, HttpStatus.OK);
    }
  }

  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RestResource<D>> create(@RequestBody D document) {
    RestResource<D> resource = new RestResource<>(service.insert(document));
    resource.add(linkTo(methodOn(getClass()).get(resource.getContent().getId())).withSelfRel());
    return new ResponseEntity<>(resource, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RestResource<D>> update(@PathVariable String id, @RequestBody D document) {
    RestResource<D> resource = new RestResource<>(service.save(document));
    resource.add(linkTo(methodOn(getClass()).get(resource.getContent().getId())).withSelfRel());
    return new ResponseEntity<>(resource, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@PathVariable String id) {
    service.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
