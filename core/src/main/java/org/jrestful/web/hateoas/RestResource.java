package org.jrestful.web.hateoas;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.hateoas.core.LinkBuilderSupport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * REST resource, with HATEOAS over HAL.
 * 
 * @param <T>
 *          The type of content to serialize.
 */
public class RestResource<T> {

  public static final String HAL_MEDIA_TYPE = "application/hal+json";

  private final T content;

  private final Map<String, ResourceLink> links = new LinkedHashMap<>();

  public RestResource(T content, String href) {
    this.content = content;
    addLink(Link.SELF, href);
  }

  public RestResource(T content, LinkBuilderSupport<?> hrefBuilder) {
    this(content, hrefBuilder.toString());
  }

  public Link addLink(String rel, String href) throws IllegalArgumentException {
    if (links.containsKey(rel)) {
      throw new IllegalArgumentException("A link with this rel already exists");
    }
    Link link = new Link(href);
    links.put(rel, link);
    return link;
  }

  public Link addLink(String rel, LinkBuilderSupport<?> hrefBuilder) throws IllegalArgumentException {
    return addLink(rel, hrefBuilder.toString());
  }

  public Links addLinks(String rel) throws IllegalArgumentException {
    if (links.containsKey(rel)) {
      throw new IllegalArgumentException("A link with this rel already exists");
    }
    Links links = new Links();
    this.links.put(rel, links);
    return links;
  }

  public Link getLink(String rel) {
    ResourceLink link = links.get(rel);
    return (Link) (link instanceof Link ? link : null);
  }

  public Links getLinks(String rel) {
    ResourceLink link = links.get(rel);
    return (Links) (link instanceof Links ? link : null);
  }

  @JsonIgnore
  public String getHref() {
    return ((Link) links.get(Link.SELF)).get(Link.HREF);
  }

  @JsonUnwrapped
  public T getContent() {
    return content;
  }

  @JsonProperty("_links")
  private Map<String, ResourceLink> getLinks() {
    return links;
  }

}
