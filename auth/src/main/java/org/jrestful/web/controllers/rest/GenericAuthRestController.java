package org.jrestful.web.controllers.rest;

import static org.jrestful.web.controllers.rest.support.RestResponse.conflict;
import static org.jrestful.web.controllers.rest.support.RestResponse.created;
import static org.jrestful.web.controllers.rest.support.RestResponse.ok;
import static org.jrestful.web.controllers.rest.support.RestResponse.unprocessableEntity;
import static org.jrestful.web.hateoas.support.LinkBuilder.link;
import static org.jrestful.web.hateoas.support.LinkBuilder.to;

import java.io.Serializable;

import org.jrestful.web.hateoas.RestResource;
import org.jrestful.web.security.auth.exceptions.EmailAlreadyExistsException;
import org.jrestful.web.security.auth.exceptions.SignUpException;
import org.jrestful.web.security.auth.exceptions.UserDataNotValidException;
import org.jrestful.web.security.auth.user.AuthUser;
import org.jrestful.web.security.auth.user.AuthUserProfile;
import org.jrestful.web.security.auth.user.AuthUserService;
import org.jrestful.web.security.auth.user.CurrentUser;
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
      return created(new RestResource<U>(user, link(to(getClass()).profile())));
    } catch (UserDataNotValidException e) {
      return unprocessableEntity();
    } catch (EmailAlreadyExistsException e) {
      return conflict();
    } catch (SignUpException e) {
      throw new IllegalStateException("SignUpException subclass '" + e.getClass().getSimpleName() + "' not managed");
    }
  }

  protected AuthUserProfile<U, K> createUserProfile(U user) {
    return new AuthUserProfile<>(user);
  }
  
}
