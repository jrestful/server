package org.jrestful.web.hateoas;

import java.util.List;

import org.springframework.hateoas.core.LinkBuilderSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * List of REST resources, with HATEOAS over HAL.
 * 
 * @param <T>
 *          The type of content to serialize.
 */
public class RestResources<T> extends RestResource<List<RestResource<T>>> {

  public RestResources(List<RestResource<T>> resources, String href) {
    super(resources, href);
    Links items = addLinks("items");
    for (RestResource<T> resource : resources) {
      items.add(resource.getHref());
    }
  }

  public RestResources(List<RestResource<T>> resources, LinkBuilderSupport<?> hrefBuilder) {
    this(resources, hrefBuilder.toString());
  }

  @Override
  @JsonUnwrapped(enabled = false)
  @JsonProperty("_embedded")
  public List<RestResource<T>> getContent() {
    return super.getContent();
  }

}