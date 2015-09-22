package org.jrestful.web.controllers.rest.support;

import static org.jrestful.web.controllers.rest.support.RestResponse.noContent;
import static org.jrestful.web.controllers.rest.support.RestResponse.notFound;
import static org.jrestful.web.controllers.rest.support.RestResponse.ok;
import static org.jrestful.web.hateoas.support.LinkBuilder.link;
import static org.jrestful.web.hateoas.support.LinkBuilder.to;

import org.jrestful.business.support.GenericSequencedDocumentService;
import org.jrestful.data.documents.support.sequence.GenericSequencedDocument;
import org.jrestful.web.hateoas.RestResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<RestResource<D>> getBySequence(@PathVariable long sequence) {
    D document = service.findOneBySequence(sequence);
    if (document == null) {
      return notFound();
    } else {
      RestResource<D> resource = new RestResource<>(document, link(to(getClass()).get(document.getId())));
      addAdditionalLinks(resource);
      return ok(resource);
    }
  }

  @RequestMapping(value = "/{sequence}", method = RequestMethod.PUT, params = "by=sequence", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RestResource<D>> updateBySequence(@PathVariable long sequence, @RequestBody D document) {
    document = service.save(document);
    RestResource<D> resource = new RestResource<>(document, link(to(getClass()).get(document.getId())));
    addAdditionalLinks(resource);
    return ok(resource);
  }

  @RequestMapping(value = "/{sequence}", method = RequestMethod.DELETE, params = "by=sequence")
  public ResponseEntity<?> deleteBySequence(@PathVariable long sequence) {
    service.deleteBySequence(sequence);
    return noContent();
  }

}
