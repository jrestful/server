package org.jrestful.web.controllers.rest.support;

import org.springframework.hateoas.ResourceSupport;

public class RestResource<T> extends ResourceSupport {

  private final T content;

  public RestResource(T content) {
    this.content = content;
  }

  public T getContent() {
    return content;
  }

}
