package org.jrestful.web.controllers.rest.support;

import static org.jrestful.web.beans.RestResponse.created;
import static org.jrestful.web.beans.RestResponse.noContent;
import static org.jrestful.web.beans.RestResponse.notFound;
import static org.jrestful.web.beans.RestResponse.ok;
import static org.jrestful.web.util.hateoas.LinkBuilder.link;
import static org.jrestful.web.util.hateoas.LinkBuilder.to;

import java.util.ArrayList;
import java.util.List;

import org.jrestful.business.support.GenericDocumentService;
import org.jrestful.data.documents.support.GenericDocument;
import org.jrestful.web.beans.PagedRestResources;
import org.jrestful.web.beans.RestResource;
import org.jrestful.web.beans.RestResources;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
  public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int pageIndex, @RequestParam(defaultValue = "25") int pageSize) {
    Pageable pageRequest = new PageRequest(pageIndex, pageSize);
    Page<D> page = service.findAll(pageRequest);
    List<RestResource<D>> documentResources = new ArrayList<>();
    for (D document : page) {
      RestResource<D> documentResource = new RestResource<>(document, link(to(getClass()).get(document.getId())));
      addAdditionalLinks(documentResource);
      documentResources.add(documentResource);
    }
    PagedRestResources<D> resources = new PagedRestResources<>(documentResources, link(to(getClass()).list(pageIndex, pageSize)));
    if (!page.isFirst()) {
      resources.addLink("first", link(to(getClass()).list(0, pageSize)));
      resources.addLink("previous", link(to(getClass()).list(pageIndex - 1, pageSize)));
    }
    if (!page.isLast()) {
      resources.addLink("next", link(to(getClass()).list(pageIndex + 1, pageSize)));
      resources.addLink("last", link(to(getClass()).list(page.getTotalPages() - 1, pageSize)));
    }
    addAdditionalLinks(resources);
    resources.setPageIndex(pageIndex);
    resources.setPageSize(pageSize);
    resources.setTotalPages(page.getTotalPages());
    resources.setTotalItems(page.getTotalElements());
    return ok(resources);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<?> get(@PathVariable String id) {
    D document = service.findOne(id);
    if (document == null) {
      return notFound();
    } else {
      RestResource<D> resource = new RestResource<>(document, link(to(getClass()).get(document.getId())));
      addAdditionalLinks(resource);
      return ok(resource);
    }
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> create(@RequestBody D document) {
    document = service.insert(document);
    RestResource<D> resource = new RestResource<>(document, link(to(getClass()).get(document.getId())));
    addAdditionalLinks(resource);
    return created(resource);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> update(@PathVariable String id, @RequestBody D payload) {
    D document = service.findOne(id);
    if (document == null) {
      return notFound();
    } else {
      service.validatePayload(payload);
      service.copyPayload(payload, document);
      document = service.save(document);
      RestResource<D> resource = new RestResource<>(document, link(to(getClass()).get(document.getId())));
      addAdditionalLinks(resource);
      return ok(resource);
    }
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@PathVariable String id) {
    D document = service.findOne(id);
    if (document == null) {
      return notFound();
    } else {
      service.delete(document);
      return noContent();
    }
  }

  protected void addAdditionalLinks(RestResource<D> resource) {
    // no-op
  }

  protected void addAdditionalLinks(RestResources<D> resources) {
    // no-op
  }

}
