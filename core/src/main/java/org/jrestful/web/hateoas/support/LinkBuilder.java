package org.jrestful.web.hateoas.support;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.hateoas.mvc.ControllerLinkBuilderFactory;
import org.springframework.stereotype.Component;

@Component
public class LinkBuilder {

  private final ControllerLinkBuilderFactory factory = new ControllerLinkBuilderFactory();

  @Autowired
  public LinkBuilder(UriPlaceholdersResolver resolver) {
    factory.setUriComponentsContributors(Arrays.asList(resolver));
  }

  public ControllerLinkBuilder linkTo(Object invocationValue) {
    return factory.linkTo(invocationValue);
  }

  public <T> T methodOn(Class<T> controller, Object... parameters) {
    return ControllerLinkBuilder.methodOn(controller, parameters);
  }

}
