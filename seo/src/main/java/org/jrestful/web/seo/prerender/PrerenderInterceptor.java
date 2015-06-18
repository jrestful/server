package org.jrestful.web.seo.prerender;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jrestful.web.interceptors.UrlInterceptor;
import org.jrestful.web.util.prerender.PrerenderDriverService;
import org.jrestful.web.util.prerender.Prerenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Intercepts requests to prerender pages when bots are requesting them, so that
 * they can see the fully loaded content.
 */
public class PrerenderInterceptor extends HandlerInterceptorAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(PrerenderInterceptor.class);

  private static final Pattern DIGITS = Pattern.compile("^\\d+$");

  public static class BotsUserAgents implements Predicate<String> {

    private static final Set<String> BOTS_USER_AGENTS;
    static {
      Set<String> botsUserAgents = new HashSet<>();
      botsUserAgents.add("googlebot");
      botsUserAgents.add("yahoo");
      botsUserAgents.add("bingbot");
      botsUserAgents.add("baiduspider");
      botsUserAgents.add("facebookexternalhit");
      botsUserAgents.add("twitterbot");
      botsUserAgents.add("rogerbot");
      botsUserAgents.add("linkedinbot");
      botsUserAgents.add("embedly");
      BOTS_USER_AGENTS = ImmutableSet.copyOf(botsUserAgents);
    }

    private final String userAgent;

    private BotsUserAgents(String userAgent) {
      this.userAgent = userAgent.toLowerCase();
    }

    @Override
    public boolean apply(String botUserAgent) {
      return userAgent.contains(botUserAgent);
    }

    public static boolean match(String userAgent) {
      return !Strings.isNullOrEmpty(userAgent) && Iterables.any(BOTS_USER_AGENTS, new BotsUserAgents(userAgent));
    }

  }

  private final File prerenderedDir;

  private final PrerenderDriverService driverService;

  @Autowired
  public PrerenderInterceptor(@Value("${app.dir}") String appDir, @Value("${phantomjs.path}") String phantomjsPath,
      @Value("${phantomjs.bind}") String phantomjsBind) throws IOException {
    prerenderedDir = new File(appDir, "prerendered");
    prerenderedDir.mkdirs();
    for (File file : prerenderedDir.listFiles()) {
      file.delete();
    }
    if (DIGITS.matcher(phantomjsBind).matches()) {
      driverService = new PrerenderDriverService(new File(phantomjsPath), Integer.parseInt(phantomjsBind));
    } else {
      String ip;
      int port;
      try {
        String[] parts = phantomjsBind.split(":");
        ip = parts[0];
        port = Integer.parseInt(parts[1]);
      } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
        throw new IllegalArgumentException("Property 'phantomjs.bind' must either be a single port or follow the syntax 'server:port'", e);
      }
      driverService = new PrerenderDriverService(new File(phantomjsPath), ip, port);
    }
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    if (isPrerenderable(request) && isBot(request)) {
      String baseUrl = request.getAttribute(UrlInterceptor.BASE_URL).toString();
      String prerenderUri = request.getAttribute("prerenderUri").toString();
      LOGGER.debug("A bot is requesting URL " + baseUrl + prerenderUri + ", prerendering needed");
      try {
        Prerenderer prerenderer = new Prerenderer(prerenderedDir, driverService);
        prerenderer.prerender(baseUrl, prerenderUri, response);
        return false;
      } catch (Exception e) {
        LOGGER.error("Could not prerender URL " + baseUrl + prerenderUri, e);
        return true;
      }
    }
    return true;
  }

  private boolean isBot(HttpServletRequest request) {
    return request.getParameterMap().containsKey("_escaped_fragment_") || BotsUserAgents.match(request.getHeader("User-Agent"));
  }

  private boolean isPrerenderable(HttpServletRequest request) {
    if (RequestMethod.GET.equals(RequestMethod.valueOf(request.getMethod()))) {
      if (request.getAttribute("prerenderUri") != null) {
        return true;
      } else if ("/".equals(request.getAttribute(UrlInterceptor.SHORT_URL))) {
        request.setAttribute("prerenderUri", "/");
        return true;
      }
    }
    return false;
  }

}