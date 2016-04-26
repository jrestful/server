package org.jrestful.web.controllers.rest.support;

import static org.jrestful.web.beans.RestResponse.created;
import static org.jrestful.web.beans.RestResponse.noContent;
import static org.jrestful.web.beans.RestResponse.ok;
import static org.jrestful.web.util.hateoas.LinkBuilder.link;
import static org.jrestful.web.util.hateoas.LinkBuilder.to;

import java.io.Serializable;
import java.util.Map;

import org.jrestful.business.support.GenericAuthUser;
import org.jrestful.business.support.GenericAuthUserService;
import org.jrestful.web.beans.AuthUserProfile;
import org.jrestful.web.beans.RestResource;
import org.jrestful.web.security.auth.CurrentUser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public abstract class GenericAuthRestController<S extends GenericAuthUserService<U, K>, U extends GenericAuthUser<K>, K extends Serializable> extends
    GenericRestController {

  protected final S service;

  public GenericAuthRestController(S service) {
    this.service = service;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<?> getProfile() {
    AuthUserProfile<U, K> userProfile = createUserProfile(CurrentUser.<U> get());
    return ok(new RestResource<>(userProfile, link(to(getClass()).getProfile())));
  }

  @PreAuthorize("isAnonymous()")
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> signUp(@RequestBody U user) {
    user = service.signUp(user);
    return created(new RestResource<>(user, link(to(getClass()).getProfile())));
  }

  @PreAuthorize("isAnonymous()")
  @RequestMapping(method = RequestMethod.PATCH, params = "type=signUpEmailConfirmation", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> confirmSignUpEmail(@RequestBody Map<String, String> payload) {
    U user = service.confirmSignUpEmail(payload.get("token"));
    return ok(new RestResource<>(user, link(to(getClass()).getProfile())));
  }

  @PreAuthorize("isAnonymous()")
  @RequestMapping(method = RequestMethod.PATCH, params = "type=tempPasswordGeneration", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> generateTempPassword(@RequestBody Map<String, String> payload) {
    service.generateTempPassword(payload.get("email"));
    return noContent();
  }

  @PreAuthorize("isAuthenticated()")
  @RequestMapping(method = RequestMethod.PATCH, params = "type=passwordChange", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload) {
    service.changePassword(CurrentUser.<U> get().getId(), payload.get("currentPassword"), payload.get("newPassword"));
    return noContent();
  }

  protected AuthUserProfile<U, K> createUserProfile(U user) {
    return new AuthUserProfile<>(user);
  }

}
