package org.jrestful.web.controllers.rest.support;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.jrestful.business.support.user.GenericUserService;
import org.jrestful.data.documents.support.user.GenericUser;
import org.jrestful.web.beans.UserProfile;
import org.jrestful.web.hateoas.Resource;
import org.jrestful.web.security.auth.user.AuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public abstract class GenericUserRestController<U extends GenericUser> extends GenericDocumentRestController<U> {

  public GenericUserRestController(GenericUserService<U> service) {
    super(service);
  }

  @RequestMapping(value = "/profile", method = RequestMethod.GET)
  public ResponseEntity<Resource<UserProfile>> profile() {
    UserProfile userProfile = new UserProfile();
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      Object details = auth.getDetails();
      if (details instanceof AuthUser) {
        userProfile = new UserProfile((AuthUser<?>) details);
      }
    }
    Resource<UserProfile> resource = new Resource<>(userProfile, linkTo(methodOn(getClass()).profile()));
    return new ResponseEntity<>(resource, HttpStatus.OK);
  }

}