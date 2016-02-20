package org.jrestful.web.util.sitemap;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jrestful.web.util.sitemap.adapters.ChangeFreqAdapter;
import org.jrestful.web.util.sitemap.adapters.LastModAdapter;
import org.jrestful.web.util.sitemap.adapters.PriorityAdapter;

import com.google.common.collect.ImmutableMap;

/**
 * Serialized to an <code>&lt;url /></code> tag, as described by the <a
 * href="http://www.sitemaps.org/protocol.html">Sitemap protocol</a>.
 */
@XmlType(propOrder = { "loc", "lastMod", "changeFreq", "priority" })
public class UrlEntry {

  /**
   * Available values for the <code>&lt;changefreq /></code> tag, as described
   * by the <a href="http://www.sitemaps.org/protocol.html">Sitemap
   * protocol</a>.
   */
  public enum ChangeFreq {

    ALWAYS("always"),

    HOURLY("hourly"),

    DAILY("daily"),

    WEEKLY("weekly"),

    MONTHLY("monthly"),

    YEARLY("yearly"),

    NEVER("never");

    private static final Map<String, ChangeFreq> BY_DISPLAY;
    static {
      Map<String, ChangeFreq> byDisplay = new HashMap<>();
      for (ChangeFreq changeFreq : values()) {
        byDisplay.put(changeFreq.getDisplay(), changeFreq);
      }
      BY_DISPLAY = ImmutableMap.copyOf(byDisplay);
    }

    public static ChangeFreq getByDisplay(String display) {
      return BY_DISPLAY.get(display);
    }

    private final String display;

    private ChangeFreq(String display) {
      this.display = display;
    }

    public String getDisplay() {
      return display;
    }

  }

  private final String loc;

  private Date lastMod;

  private ChangeFreq changeFreq;

  private Float priority;

  public UrlEntry(String loc) {
    this.loc = loc;
  }

  @XmlElement(name = "loc", required = true)
  public String getLoc() {
    return loc;
  }

  @XmlElement(name = "lastmod")
  @XmlJavaTypeAdapter(LastModAdapter.class)
  public Date getLastMod() {
    return lastMod;
  }

  @XmlElement(name = "changefreq")
  @XmlJavaTypeAdapter(ChangeFreqAdapter.class)
  public ChangeFreq getChangeFreq() {
    return changeFreq;
  }

  @XmlElement(name = "priority")
  @XmlJavaTypeAdapter(PriorityAdapter.class)
  public Float getPriority() {
    return priority;
  }

  public void setLastMod(Date lastMod) {
    this.lastMod = lastMod;
  }

  public void setChangeFreq(ChangeFreq changeFreq) {
    this.changeFreq = changeFreq;
  }

  public void setPriority(Float priority) {
    this.priority = priority;
  }

}
