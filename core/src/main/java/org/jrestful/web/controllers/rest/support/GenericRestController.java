package org.jrestful.web.controllers.rest.support;

import org.jrestful.web.controllers.support.GenericController;
import org.jrestful.web.hateoas.support.LinkBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

/**
 * Generic abstract class for a REST controller.
 */
public abstract class GenericRestController extends GenericController {

  @Autowired
  private LinkBuilder builder;

  protected final ControllerLinkBuilder link(Object invocationValue) {
    return builder.linkTo(invocationValue);
  }

  protected final <T extends GenericRestController> T to(Class<T> controller) {
    return builder.methodOn(controller);
  }

}
