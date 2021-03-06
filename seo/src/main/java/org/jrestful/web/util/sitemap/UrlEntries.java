package org.jrestful.web.util.sitemap;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Serialized to an <code>&lt;urlset /></code> tag, as described by the <a
 * href="http://www.sitemaps.org/protocol.html">Sitemap protocol</a>.
 */
@XmlRootElement(name = "urlset")
public class UrlEntries {

  private final List<UrlEntry> urls = new ArrayList<>();

  public void add(UrlEntry url) {
    urls.add(url);
  }

  @XmlElement(name = "url", required = true)
  public List<UrlEntry> getUrls() {
    return urls;
  }

}
