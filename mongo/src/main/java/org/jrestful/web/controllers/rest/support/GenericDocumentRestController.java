package org.jrestful.web.controllers.rest.support;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.jrestful.business.support.GenericDocumentService;
import org.jrestful.data.documents.support.GenericDocument;
import org.jrestful.web.hateoas.PageableResources;
import org.jrestful.web.hateoas.Resource;
import org.jrestful.web.hateoas.Resources;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Generic abstract class for a document REST controller.
 * 
 * @param <D>
 *          The document type to manage.
 */
public abstract class GenericDocumentRestController<S extends GenericDocumentService<D>, D extends GenericDocument> extends GenericRestController {

  protected final S service;

  public GenericDocumentRestController(S service) {
    this.service = service;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<PageableResources<D>> list(@RequestParam(defaultValue = "0") int pageIndex, @RequestParam(defaultValue = "25") int pageSize) {
    Pageable pageRequest = new PageRequest(pageIndex, pageSize);
    Page<D> page = service.findAll(pageRequest);
    List<Resource<D>> documentResources = new ArrayList<>();
    for (D document : page) {
      Resource<D> documentResource = new Resource<>(document, linkTo(methodOn(getClass()).get(document.getId())));
      addAdditionalLinks(documentResource);
      documentResources.add(documentResource);
    }
    PageableResources<D> resources = new PageableResources<>(documentResources, linkTo(methodOn(getClass()).list(pageIndex, pageSize)));
    if (!page.isFirst()) {
      resources.addLink("first", linkTo(methodOn(getClass()).list(0, pageSize)));
      resources.addLink("previous", linkTo(methodOn(getClass()).list(pageIndex - 1, pageSize)));
    }
    if (!page.isLast()) {
      resources.addLink("next", linkTo(methodOn(getClass()).list(pageIndex + 1, pageSize)));
      resources.addLink("last", linkTo(methodOn(getClass()).list(page.getTotalPages() - 1, pageSize)));
    }
    addAdditionalLinks(resources);
    resources.setPageIndex(pageIndex);
    resources.setPageSize(pageSize);
    resources.setTotalPages(page.getTotalPages());
    resources.setTotalItems(page.getTotalElements());
    return new ResponseEntity<>(resources, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Resource<D>> get(@PathVariable String id) {
    D document = service.findOne(id);
    if (document == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      Resource<D> resource = new Resource<>(document, linkTo(methodOn(getClass()).get(document.getId())));
      addAdditionalLinks(resource);
      return new ResponseEntity<>(resource, HttpStatus.OK);
    }
  }

  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Resource<D>> create(@RequestBody D document) {
    document = service.insert(document);
    Resource<D> resource = new Resource<>(document, linkTo(methodOn(getClass()).get(document.getId())));
    addAdditionalLinks(resource);
    return new ResponseEntity<>(resource, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Resource<D>> update(@PathVariable String id, @RequestBody D document) {
    document = service.save(document);
    Resource<D> resource = new Resource<>(document, linkTo(methodOn(getClass()).get(document.getId())));
    addAdditionalLinks(resource);
    return new ResponseEntity<>(resource, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@PathVariable String id) {
    service.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  protected void addAdditionalLinks(Resource<D> resource) {
    // no-op
  }

  protected void addAdditionalLinks(Resources<D> resources) {
    // no-op
  }

}
