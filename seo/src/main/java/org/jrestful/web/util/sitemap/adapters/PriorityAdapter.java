package org.jrestful.web.util.sitemap.adapters;

import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PriorityAdapter extends XmlAdapter<String, Float> {

  private static final Locale ENGLISH = Locale.ENGLISH;

  private static final String PRIORITY_FORMAT = "%.1f";

  @Override
  public String marshal(Float priority) throws Exception {
    return String.format(ENGLISH, PRIORITY_FORMAT, priority);
  }

  @Override
  public Float unmarshal(String priority) throws Exception {
    return Float.parseFloat(priority);
  }

}
