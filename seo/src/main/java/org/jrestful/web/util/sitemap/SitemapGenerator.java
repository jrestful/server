package org.jrestful.web.util.sitemap;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Generates a sitemap via JAXB.
 */
public class SitemapGenerator {

  private static final String ENCODING = "UTF-8";

  private static final String SCHEMA = "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd";

  public static void generate(UrlEntries urlEntries, File file) throws JAXBException {
    Marshaller marshaller = JAXBContext.newInstance(UrlEntries.class).createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
    marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, SCHEMA);
    marshaller.marshal(urlEntries, file);
  }

}
