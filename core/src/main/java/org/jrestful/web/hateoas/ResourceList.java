package org.jrestful.web.hateoas;

import java.util.List;

import org.springframework.hateoas.core.LinkBuilderSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * List of HAL resources.
 * 
 * @param <T>
 *          The type of content to serialize.
 */
public class ResourceList<T> extends Resource<List<Resource<T>>> {

  public ResourceList(List<Resource<T>> resources, String href) {
    super(resources, href);
    LinkList items = addLinkList("item");
    for (Resource<T> resource : resources) {
      items.add(resource.getHref());
    }
  }

  public ResourceList(List<Resource<T>> resources, LinkBuilderSupport<?> hrefBuilder) {
    this(resources, hrefBuilder.toString());
  }

  @Override
  @JsonUnwrapped(enabled = false)
  @JsonProperty("_embedded")
  public List<Resource<T>> getContent() {
    return super.getContent();
  }

}