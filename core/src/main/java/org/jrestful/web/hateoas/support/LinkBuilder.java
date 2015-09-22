package org.jrestful.web.hateoas.support;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.hateoas.mvc.ControllerLinkBuilderFactory;
import org.springframework.stereotype.Component;

@Component
public class LinkBuilder {

  private static final ControllerLinkBuilderFactory FACTORY = new ControllerLinkBuilderFactory();

  @Autowired
  public LinkBuilder(UriPlaceholdersResolver resolver) {
    FACTORY.setUriComponentsContributors(Arrays.asList(resolver));
  }

  public static ControllerLinkBuilder link(Object invocationValue) {
    return FACTORY.linkTo(invocationValue);
  }

  public static <T> T to(Class<T> controller, Object... parameters) {
    return ControllerLinkBuilder.methodOn(controller, parameters);
  }

}
