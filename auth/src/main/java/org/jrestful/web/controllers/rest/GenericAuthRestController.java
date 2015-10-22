package org.jrestful.web.controllers.rest;

import static org.jrestful.web.controllers.rest.support.RestResponse.created;
import static org.jrestful.web.controllers.rest.support.RestResponse.ok;
import static org.jrestful.web.hateoas.support.LinkBuilder.link;
import static org.jrestful.web.hateoas.support.LinkBuilder.to;

import java.io.Serializable;

import org.jrestful.business.exceptions.HttpStatusException;
import org.jrestful.business.support.AuthUserService;
import org.jrestful.data.documents.support.AuthUser;
import org.jrestful.web.beans.AuthUserProfile;
import org.jrestful.web.hateoas.RestResource;
import org.jrestful.web.security.auth.CurrentUser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public abstract class GenericAuthRestController<S extends AuthUserService<U, K>, U extends AuthUser<K>, K extends Serializable> {

  protected final S service;

  public GenericAuthRestController(S service) {
    this.service = service;
  }

  @RequestMapping(value = "/profile", method = RequestMethod.GET)
  public ResponseEntity<RestResource<AuthUserProfile<U, K>>> profile() {
    AuthUserProfile<U, K> userProfile = createUserProfile(CurrentUser.<U> get());
    RestResource<AuthUserProfile<U, K>> resource = new RestResource<>(userProfile, link(to(getClass()).profile()));
    return ok(resource);
  }

  @RequestMapping(value = "/signUp", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RestResource<U>> signUp(@RequestBody U user) {
    try {
      user = service.signUp(user);
      return created(new RestResource<>(user, link(to(getClass()).profile())));
    } catch (HttpStatusException e) {
      return e.build();
    }
  }

  protected AuthUserProfile<U, K> createUserProfile(U user) {
    return new AuthUserProfile<>(user);
  }

}
