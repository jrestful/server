package org.jrestful.web.controllers.rest.support;

import static org.jrestful.web.beans.RestResponse.noContent;
import static org.jrestful.web.beans.RestResponse.notFound;
import static org.jrestful.web.beans.RestResponse.ok;
import static org.jrestful.web.util.hateoas.LinkBuilder.link;
import static org.jrestful.web.util.hateoas.LinkBuilder.to;

import org.jrestful.business.support.GenericSequencedDocumentService;
import org.jrestful.data.documents.support.GenericSequencedDocument;
import org.jrestful.web.beans.RestResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Generic abstract class for a sequenced document REST controller.
 * 
 * @param <D>
 *          The document type to manage.
 */
public abstract class GenericSequencedDocumentRestController<S extends GenericSequencedDocumentService<D>, D extends GenericSequencedDocument>
    extends GenericDocumentRestController<S, D> {

  public GenericSequencedDocumentRestController(S service) {
    super(service);
  }

  @RequestMapping(value = "/{sequence}", method = RequestMethod.GET, params = "by=sequence")
  public ResponseEntity<?> getBySequence(@PathVariable long sequence) {
    D document = service.findOneBySequence(sequence);
    if (document == null) {
      return notFound();
    } else {
      RestResource<D> resource = new RestResource<>(document, link(to(getClass()).get(document.getId())));
      addAdditionalLinks(resource);
      return ok(resource);
    }
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/{sequence}", method = RequestMethod.PUT, params = "by=sequence", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateBySequence(@PathVariable long sequence, @RequestBody D payload) {
    D document = service.findOneBySequence(sequence);
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
  @RequestMapping(value = "/{sequence}", method = RequestMethod.DELETE, params = "by=sequence")
  public ResponseEntity<?> deleteBySequence(@PathVariable long sequence) {
    D document = service.findOneBySequence(sequence);
    if (document == null) {
      return notFound();
    } else {
      service.delete(document);
      return noContent();
    }
  }

}
