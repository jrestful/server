package org.jrestful.web.seo.sitemap;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.jrestful.web.util.sitemap.SitemapGenerator;
import org.jrestful.web.util.sitemap.UrlEntries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SitemapScheduledGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger(SitemapScheduledGenerator.class);

  private final String appUrl;

  private final SitemapBuilder sitemapBuilder;

  private final File sitemap;

  @Autowired
  public SitemapScheduledGenerator(@Value("${app.url}") String appUrl, SitemapBuilder sitemapBuilder, @Value("${app.dir}") String appDir) {
    this.appUrl = appUrl;
    this.sitemapBuilder = sitemapBuilder;
    sitemap = new File(appDir, "resources/sitemap.xml");
    sitemap.getParentFile().mkdirs();
  }

  @PostConstruct
  @Scheduled(cron = "0 0 5 * * ?")
  public void generate() {
    try {
      UrlEntries urlEntries = sitemapBuilder.build(appUrl);
      SitemapGenerator.generate(urlEntries, sitemap);
      LOGGER.debug("Sitemap successfully generated: " + sitemap);
    } catch (JAXBException e) {
      LOGGER.error("An error occurred while generating a sitemap", e);
    }
  }

}
