package org.jrestful.web.controllers.rest;

import org.jrestful.web.beans.UserProfile;
import org.jrestful.web.controllers.rest.support.GenericRestController;
import org.jrestful.web.security.auth.user.AuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api-${app.apiVersion}/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRestController extends GenericRestController {

  @RequestMapping(value = "/profile", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public UserProfile profile() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      Object details = auth.getDetails();
      if (details instanceof AuthUser) {
        return new UserProfile((AuthUser<?>) details);
      }
    }
    return new UserProfile();
  }

}
