package org.jrestful.web.hateoas;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * HAL link, with a mandatory "href" attribute.
 */
public class Link implements ResourceLink, Map<String, String> {

  public static final String SELF = "self";

  public static final String HREF = "href";

  private final Map<String, String> attributes = new LinkedHashMap<>();

  public Link(String href) {
    attributes.put(HREF, href);
  }

  public Link with(String name, String value) {
    attributes.put(name, value);
    return this;
  }

  @Override
  public boolean isLink() {
    return true;
  }

  @Override
  public boolean isLinkList() {
    return false;
  }

  @Override
  public int size() {
    return attributes.size();
  }

  @Override
  public boolean isEmpty() {
    return attributes.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return attributes.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return attributes.containsValue(value);
  }

  @Override
  public String get(Object key) {
    return attributes.get(key);
  }

  @Override
  public String put(String key, String value) {
    return attributes.put(key, value);
  }

  @Override
  public String remove(Object key) {
    return attributes.remove(key);
  }

  @Override
  public void putAll(Map<? extends String, ? extends String> m) {
    attributes.putAll(m);
  }

  @Override
  public void clear() {
    attributes.clear();
  }

  @Override
  public Set<String> keySet() {
    return attributes.keySet();
  }

  @Override
  public Collection<String> values() {
    return attributes.values();
  }

  @Override
  public Set<Map.Entry<String, String>> entrySet() {
    return attributes.entrySet();
  }

}
