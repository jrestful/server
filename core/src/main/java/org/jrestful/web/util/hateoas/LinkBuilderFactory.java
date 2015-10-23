package org.jrestful.web.util.hateoas;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.hateoas.core.DummyInvocationUtils.MethodInvocation;
import org.springframework.hateoas.mvc.ControllerLinkBuilderFactory;
import org.springframework.util.StringValueResolver;
import org.springframework.web.util.UriComponentsBuilder;

public class LinkBuilderFactory extends ControllerLinkBuilderFactory {

  private StringValueResolver resolver;

  private final ConcurrentHashMap<Method, String> resolvedPaths = new ConcurrentHashMap<>();

  @Override
  protected UriComponentsBuilder applyUriComponentsContributer(UriComponentsBuilder builder, MethodInvocation invocation) {
    Method method = invocation.getMethod();
    if (!resolvedPaths.containsKey(method)) {
      String resolvedPath = resolver.resolveStringValue(builder.build().getPath());
      resolvedPaths.putIfAbsent(method, resolvedPath);
    }
    builder.replacePath(resolvedPaths.get(method));
    return super.applyUriComponentsContributer(builder, invocation);
  }

  public void setResolver(StringValueResolver resolver) {
    this.resolver = resolver;
  }

}
