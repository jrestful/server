package org.jrestful.web.seo.sitemap;

import org.jrestful.web.util.sitemap.UrlEntries;

public interface SitemapBuilder {

  UrlEntries build(String appUrl);

}
