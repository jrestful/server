package org.jrestful.web.controllers.rest.support;

import static org.jrestful.web.controllers.rest.support.RestResponse.ok;
import static org.jrestful.web.hateoas.support.LinkBuilder.link;
import static org.jrestful.web.hateoas.support.LinkBuilder.to;

import org.jrestful.business.support.user.GenericUserService;
import org.jrestful.data.documents.support.user.GenericUser;
import org.jrestful.web.hateoas.RestResource;
import org.jrestful.web.security.auth.user.AuthUserProfile;
import org.jrestful.web.security.auth.user.CurrentUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public abstract class GenericUserRestController<S extends GenericUserService<U>, U extends GenericUser> extends
    GenericSequencedDocumentRestController<S, U> {

  public GenericUserRestController(S service) {
    super(service);
  }

  @RequestMapping(value = "/profile", method = RequestMethod.GET)
  public ResponseEntity<RestResource<AuthUserProfile<U, String>>> profile() {
    AuthUserProfile<U, String> userProfile = createUserProfile(CurrentUser.<U> get());
    RestResource<AuthUserProfile<U, String>> resource = new RestResource<>(userProfile, link(to(getClass()).profile()));
    return ok(resource);
  }

  protected AuthUserProfile<U, String> createUserProfile(U user) {
    return new AuthUserProfile<>(user);
  }

}
