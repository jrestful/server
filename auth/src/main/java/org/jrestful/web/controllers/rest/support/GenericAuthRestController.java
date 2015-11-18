package org.jrestful.web.controllers.rest.support;

import static org.jrestful.web.beans.RestResponse.created;
import static org.jrestful.web.beans.RestResponse.noContent;
import static org.jrestful.web.beans.RestResponse.ok;
import static org.jrestful.web.util.hateoas.LinkBuilder.link;
import static org.jrestful.web.util.hateoas.LinkBuilder.to;

import java.io.Serializable;

import org.jrestful.business.exceptions.HttpStatusException;
import org.jrestful.business.support.GenericAuthUser;
import org.jrestful.business.support.GenericAuthUserService;
import org.jrestful.web.beans.AuthUserProfile;
import org.jrestful.web.beans.RestResource;
import org.jrestful.web.security.auth.CurrentUser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

// TODO customize 403
public abstract class GenericAuthRestController<S extends GenericAuthUserService<U, K>, U extends GenericAuthUser<K>, K extends Serializable> extends
    GenericRestController {

  protected final S service;

  public GenericAuthRestController(S service) {
    this.service = service;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<?> getProfile() {
    AuthUserProfile<U, K> userProfile = createUserProfile(CurrentUser.<U> get());
    RestResource<AuthUserProfile<U, K>> resource = new RestResource<>(userProfile, link(to(getClass()).getProfile()));
    return ok(resource);
  }

  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> signUp(@RequestBody U user) {
    try {
      user = service.signUp(user);
      return created(new RestResource<>(user, link(to(getClass()).getProfile())));
    } catch (HttpStatusException e) {
      return e.toResponseEntity();
    }
  }

  @RequestMapping(method = RequestMethod.PATCH)
  public ResponseEntity<?> confirm(@RequestParam String token) {
    try {
      U user = service.confirm(token);
      if (user == null) {
        return noContent();
      } else {
        return ok(new RestResource<>(user, link(to(getClass()).getProfile())));
      }
    } catch (HttpStatusException e) {
      return e.toResponseEntity();
    }
  }

  protected AuthUserProfile<U, K> createUserProfile(U user) {
    return new AuthUserProfile<>(user);
  }

}
