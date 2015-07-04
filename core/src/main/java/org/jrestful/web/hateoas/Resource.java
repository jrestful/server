package org.jrestful.web.hateoas;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.hateoas.core.LinkBuilderSupport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * HAL resource.
 * 
 * @param <T>
 *          The type of content to serialize.
 */
public class Resource<T> {

  private final T content;

  private final Map<String, ResourceLink> links = new LinkedHashMap<>();

  public Resource(T content, String href) {
    this.content = content;
    addLink(Link.SELF, href);
  }

  public Resource(T content, LinkBuilderSupport<?> hrefBuilder) {
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

  public LinkList addLinkList(String rel) throws IllegalArgumentException {
    if (links.containsKey(rel)) {
      throw new IllegalArgumentException("A link with this rel already exists");
    }
    LinkList linkList = new LinkList();
    links.put(rel, linkList);
    return linkList;
  }

  @JsonIgnore
  public Link getLink(String rel) {
    ResourceLink link = links.get(rel);
    return (Link) (link.isLink() ? link : null);
  }

  @JsonIgnore
  public LinkList getLinkList(String rel) {
    ResourceLink link = links.get(rel);
    return (LinkList) (link.isLinkList() ? link : null);
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
