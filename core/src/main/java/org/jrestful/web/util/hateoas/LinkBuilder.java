package org.jrestful.web.util.hateoas;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

@Component
public class LinkBuilder implements EmbeddedValueResolverAware {

  private static final LinkBuilderFactory FACTORY = new LinkBuilderFactory();

  public static ControllerLinkBuilder link(Object invocationValue) {
    return FACTORY.linkTo(invocationValue);
  }

  public static <T> T to(Class<T> controller, Object... parameters) {
    return ControllerLinkBuilder.methodOn(controller, parameters);
  }

  @Override
  public void setEmbeddedValueResolver(StringValueResolver resolver) {
    FACTORY.setResolver(resolver);
  }

}
