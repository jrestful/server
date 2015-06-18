package org.jrestful.web.util.prerender;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;

import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.service.DriverService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Allows GhostDriver to bind on an IP (broadly similar to
 * {@link PhantomJSDriverService}).
 */
public class PrerenderDriverService extends DriverService {

  private static final String URL_FIELD_NAME = "url";

  public PrerenderDriverService(File phantomjsPath, int port, String webdriver) throws IOException {
    super(phantomjsPath, port, ImmutableList.of("--webdriver=" + webdriver), ImmutableMap.<String, String> of());
  }

  public PrerenderDriverService(File phantomjsPath, int port) throws IOException {
    this(phantomjsPath, port, String.valueOf(port));
  }

  public PrerenderDriverService(File phantomjsPath, String ip, int port) throws IOException {
    this(phantomjsPath, port, ip + ":" + port);
    try {
      Field field = getClass().getSuperclass().getDeclaredField(URL_FIELD_NAME);
      field.setAccessible(true);
      field.set(this, new URL("http://" + ip + ":" + port));
      field.setAccessible(false);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new IllegalStateException("Unable to set DriverService#url to http://" + ip + ":" + port, e);
    }
  }

}
