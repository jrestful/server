package org.jrestful.web.seo.sitemap;

import org.jrestful.web.util.sitemap.UrlEntries;

public interface SitemapBuilder {

  /**
   * Will be called on server startup and every day at 5 am. The sitemap will be generated in <code>${app.dir}/resources/sitemap.xml</code>, where the
   * <code>app.dir</code> property is specified in the <code>jrestfulAppProps</code> properties file.
   * 
   * @param appUrl
   *          The application base URL, specified by the <code>app.url</code> property in the <code>jrestfulAppProps</code> properties file.
   * @return The URL entries that the sitemap must list.
   */
  UrlEntries build(String appUrl);

}
