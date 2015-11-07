package org.jrestful.web.util.prerender;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.jrestful.util.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

/**
 * Prerenders pages when bots are requesting them, so that they can see the
 * fully loaded content. Prerendered pages will be cached for one day.
 */
public class Prerenderer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Prerenderer.class);

  private static final int ONE_DAY = 1;

  private static final int TWO_SECONDS = 2;

  private static final int TEN_SECONDS = 10;

  private static final int IN_MILLIS = 1000;

  private static final Charset CHARSET = Charset.forName("UTF-8");

  private final File prerenderedDir;

  private final PrerenderDriverService driverService;

  public Prerenderer(File prerenderedDir, PrerenderDriverService driverService) {
    this.prerenderedDir = prerenderedDir;
    this.driverService = driverService;
  }

  public void prerender(String baseUrl, String prerenderUri, HttpServletResponse response) throws Exception {
    synchronized (Prerenderer.class) {
      File prerenderedFile = new File(prerenderedDir, toFilename(prerenderUri));
      byte[] htmlBytes;
      if (isUpToDate(prerenderedFile)) {
        htmlBytes = Files.toByteArray(prerenderedFile);
        LOGGER.debug("URL " + baseUrl + prerenderUri + " formerly fetched, returning " + prerenderedFile);
      } else {
        htmlBytes = fetch(baseUrl + prerenderUri).getBytes(CHARSET);
        Files.write(htmlBytes, prerenderedFile);
        LOGGER.debug("URL " + baseUrl + prerenderUri + " successfully fetched, returning " + prerenderedFile);
      }
      try (InputStream htmlStream = new ByteArrayInputStream(htmlBytes)) {
        response.addHeader("Content-Type", "text/html;charset=UTF-8");
        response.addHeader("Vary", "Accept-Encoding");
        ByteStreams.copy(htmlStream, response.getOutputStream());
      }
    }
  }

  private boolean isUpToDate(File prerenderedFile) {
    if (prerenderedFile.exists()) {
      Date threshold = DateUtils.removeToNow(Calendar.DAY_OF_MONTH, ONE_DAY);
      return prerenderedFile.lastModified() > threshold.getTime();
    } else {
      return false;
    }
  }

  private String toFilename(String prerenderUri) {
    return prerenderUri.replaceAll("[^a-zA-Z0-9/]+", "-").replaceAll("/+", "_") + ".html";
  }

  private String fetch(String url) throws InterruptedException {
    WebDriver driver = null;
    try {
      driver = new PrerenderDriver(driverService);
      fetch(driver, url);
      try {
        LOGGER.debug("Waiting for body to be loaded");
        WebDriverWait wait = new WebDriverWait(driver, TEN_SECONDS);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("body.loading")));
        Thread.sleep(TWO_SECONDS * IN_MILLIS);
      } catch (TimeoutException e) {
        LOGGER.warn("Body still has class 'loading' after 10 seconds, returning page source as is");
      }
      return driver.getPageSource();
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  private void fetch(final WebDriver driver, String url) throws InterruptedException {
    Thread thread = new Thread(new Runnable() {

      @Override
      public void run() {
        driver.get(Thread.currentThread().getName());
      }

    }, url);
    thread.start();
    thread.join(TEN_SECONDS * IN_MILLIS);
    if (thread.isAlive()) {
      thread.interrupt();
      throw new TimeoutException("Timeout of 10 seconds reached when accessing URL " + url);
    }
  }

}
