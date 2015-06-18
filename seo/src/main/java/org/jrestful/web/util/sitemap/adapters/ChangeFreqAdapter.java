package org.jrestful.web.util.sitemap.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.jrestful.web.util.sitemap.UrlEntry.ChangeFreq;

/**
 * Serializes a <code>&lt;changefreq /></code> tag, as described by the <a
 * href="http://www.sitemaps.org/protocol.html">Sitemap protocol</a>.
 */
public class ChangeFreqAdapter extends XmlAdapter<String, ChangeFreq> {

  @Override
  public String marshal(ChangeFreq changeFreq) throws Exception {
    return changeFreq.getDisplay();
  }

  @Override
  public ChangeFreq unmarshal(String changeFreq) throws Exception {
    return ChangeFreq.getByDisplay(changeFreq);
  }

}
