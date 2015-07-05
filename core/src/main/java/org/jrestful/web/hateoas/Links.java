package org.jrestful.web.hateoas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.springframework.hateoas.core.LinkBuilderSupport;

/**
 * List of HAL links.
 */
public class Links implements ResourceLink, List<Link> {

  private final List<Link> links = new ArrayList<>();

  public Link add(String href) {
    Link link = new Link(href);
    links.add(link);
    return link;
  }

  public Link add(LinkBuilderSupport<?> hrefBuilder) {
    return add(hrefBuilder.toString());
  }

  @Override
  public int size() {
    return links.size();
  }

  @Override
  public boolean isEmpty() {
    return links.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return links.contains(o);
  }

  @Override
  public Iterator<Link> iterator() {
    return links.iterator();
  }

  @Override
  public Object[] toArray() {
    return links.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return links.toArray(a);
  }

  @Override
  public boolean add(Link e) {
    return links.add(e);
  }

  @Override
  public boolean remove(Object o) {
    return links.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return links.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends Link> c) {
    return links.addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends Link> c) {
    return links.addAll(index, c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return links.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return links.retainAll(c);
  }

  @Override
  public void clear() {
    links.clear();
  }

  @Override
  public Link get(int index) {
    return links.get(index);
  }

  @Override
  public Link set(int index, Link element) {
    return links.set(index, element);
  }

  @Override
  public void add(int index, Link element) {
    links.add(index, element);
  }

  @Override
  public Link remove(int index) {
    return links.remove(index);
  }

  @Override
  public int indexOf(Object o) {
    return links.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return links.lastIndexOf(o);
  }

  @Override
  public ListIterator<Link> listIterator() {
    return links.listIterator();
  }

  @Override
  public ListIterator<Link> listIterator(int index) {
    return links.listIterator(index);
  }

  @Override
  public List<Link> subList(int fromIndex, int toIndex) {
    return links.subList(fromIndex, toIndex);
  }

}
