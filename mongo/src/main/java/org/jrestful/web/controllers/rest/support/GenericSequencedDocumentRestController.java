package org.jrestful.web.controllers.rest.support;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.jrestful.business.support.GenericSequencedDocumentService;
import org.jrestful.data.documents.support.sequence.GenericSequencedDocument;
import org.jrestful.web.hateoas.Resource;
import org.springframework.http.HttpStatus;
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
  public ResponseEntity<Resource<D>> getBySequence(@PathVariable Long sequence) {
    D document = service.findBySequence(sequence);
    if (document == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      Resource<D> resource = new Resource<>(document, linkTo(methodOn(getClass()).get(document.getId())));
      addAdditionalLinks(resource);
      return new ResponseEntity<>(resource, HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/{sequence}", method = RequestMethod.PUT, params = "by=sequence", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Resource<D>> updateBySequence(@PathVariable Long sequence, @RequestBody D document) {
    document = service.save(document);
    Resource<D> resource = new Resource<>(document, linkTo(methodOn(getClass()).get(document.getId())));
    addAdditionalLinks(resource);
    return new ResponseEntity<>(resource, HttpStatus.OK);
  }

  @RequestMapping(value = "/{sequence}", method = RequestMethod.DELETE, params = "by=sequence")
  public ResponseEntity<?> deleteBySequence(@PathVariable Long sequence) {
    service.deleteBySequence(sequence);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
