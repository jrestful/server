package org.jrestful.web.controllers.rest.support;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.jrestful.business.support.user.GenericUserService;
import org.jrestful.data.documents.support.user.GenericUser;
import org.jrestful.web.beans.UserProfile;
import org.jrestful.web.hateoas.RestResource;
import org.jrestful.web.security.auth.user.AuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public abstract class GenericUserRestController<S extends GenericUserService<U>, U extends GenericUser> extends
    GenericSequencedDocumentRestController<S, U> {

  public GenericUserRestController(S service) {
    super(service);
  }

  @RequestMapping(value = "/profile", method = RequestMethod.GET)
  public ResponseEntity<RestResource<UserProfile>> profile() {
    UserProfile userProfile = new UserProfile();
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      Object details = auth.getDetails();
      if (details instanceof AuthUser) {
        userProfile = new UserProfile((AuthUser<?>) details);
      }
    }
    RestResource<UserProfile> resource = new RestResource<>(userProfile, linkTo(methodOn(getClass()).profile()));
    return new ResponseEntity<>(resource, HttpStatus.OK);
  }

}
