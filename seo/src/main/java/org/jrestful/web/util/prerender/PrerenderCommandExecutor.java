package org.jrestful.web.util.prerender;

import java.io.IOException;
import java.net.ConnectException;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.Response;

import com.google.common.base.Throwables;

/**
 * Allows GhostDriver to bind on an IP (broadly similar to
 * {@link PhantomJSCommandExecutor}).
 */
public class PrerenderCommandExecutor extends HttpCommandExecutor {

  private static final String CONNECTION_REFUSED = "Connection refused";

  private final PrerenderDriverService service;

  public PrerenderCommandExecutor(PrerenderDriverService service) {
    super(PrerenderDriver.CUSTOM_COMMANDS, service.getUrl());
    this.service = service;
  }

  @Override
  public Response execute(Command command) throws IOException {
    if (DriverCommand.NEW_SESSION.equals(command.getName())) {
      service.start();
    }
    try {
      return super.execute(command);
    } catch (Throwable t) {
      Throwable rootCause = Throwables.getRootCause(t);
      if (rootCause instanceof ConnectException && CONNECTION_REFUSED.equals(rootCause.getMessage()) && !service.isRunning()) {
        throw new WebDriverException("The PhantomJS/GhostDriver server has unexpectedly died", t);
      }
      Throwables.propagateIfPossible(t);
      throw new WebDriverException(t);
    } finally {
      if (DriverCommand.QUIT.equals(command.getName())) {
        service.stop();
      }
    }
  }

}
