package org.jrestful.web.util.prerender;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.CommandInfo;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.HttpVerb;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.google.common.collect.ImmutableMap;

public class PrerenderDriver extends RemoteWebDriver implements TakesScreenshot {

  public static final Map<String, CommandInfo> CUSTOM_COMMANDS;
  static {
    Map<String, CommandInfo> customCommands = new HashMap<String, CommandInfo>();
    customCommands.put("executePhantomScript", new CommandInfo("/session/:sessionId/phantom/execute", HttpVerb.POST));
    CUSTOM_COMMANDS = ImmutableMap.copyOf(customCommands);
  }

  public PrerenderDriver(PrerenderDriverService service) {
    super(new PrerenderCommandExecutor(service), DesiredCapabilities.phantomjs());
  }

  @Override
  public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
    String base64 = (String) execute(DriverCommand.SCREENSHOT).getValue();
    return target.convertFromBase64Png(base64);
  }

}