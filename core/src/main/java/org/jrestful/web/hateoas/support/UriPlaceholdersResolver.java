package org.jrestful.web.hateoas.support;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.MethodParameter;
import org.springframework.hateoas.mvc.UriComponentsContributor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class UriPlaceholdersResolver implements UriComponentsContributor, EmbeddedValueResolverAware {

  private final ConcurrentHashMap<Method, String> resolvedPaths = new ConcurrentHashMap<>();

  private StringValueResolver resolver;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return true;
  }

  @Override
  public void enhance(UriComponentsBuilder builder, MethodParameter parameter, Object value) {
    Method method = parameter.getMethod();
    if (!resolvedPaths.containsKey(method)) {
      String resolvedPath = resolver.resolveStringValue(builder.build().getPath());
      resolvedPaths.putIfAbsent(method, resolvedPath);
    }
    builder.replacePath(resolvedPaths.get(method));
  }

  @Override
  public void setEmbeddedValueResolver(StringValueResolver resolver) {
    this.resolver = resolver;
  }

}
