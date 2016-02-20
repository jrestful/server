package org.jrestful.web.util.sitemap.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Serializes a <code>&lt;lastmod /></code> tag, as described by the <a
 * href="http://www.sitemaps.org/protocol.html">Sitemap protocol</a>.
 */
public class LastModAdapter extends XmlAdapter<String, Date> {

  private static final String LAST_MOD_FORMAT = "yyyy-MM-dd";

  @Override
  public String marshal(Date lastMod) throws Exception {
    return new SimpleDateFormat(LAST_MOD_FORMAT).format(lastMod);
  }

  @Override
  public Date unmarshal(String lastMod) throws Exception {
    return new SimpleDateFormat(LAST_MOD_FORMAT).parse(lastMod);
  }

}
