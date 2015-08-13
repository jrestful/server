package org.jrestful.web.hateoas;

import java.util.List;

import org.springframework.hateoas.core.LinkBuilderSupport;

/**
 * Pageable list of REST resources, with HATEOAS over HAL.
 * 
 * @param <T>
 *          The type of content to serialize.
 */
public class PageableRestResources<T> extends RestResources<T> {

  private int pageIndex;

  private int pageSize;

  private int totalPages;

  private long totalItems;

  public PageableRestResources(List<RestResource<T>> resources, String href) {
    super(resources, href);
  }

  public PageableRestResources(List<RestResource<T>> resources, LinkBuilderSupport<?> hrefBuilder) {
    this(resources, hrefBuilder.toString());
  }

  public int getPageIndex() {
    return pageIndex;
  }

  public void setPageIndex(int pageIndex) {
    this.pageIndex = pageIndex;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  public long getTotalItems() {
    return totalItems;
  }

  public void setTotalItems(long totalItems) {
    this.totalItems = totalItems;
  }

}