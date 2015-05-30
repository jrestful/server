package org.jrestful.util;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CharMatcher;

public final class UrlUtils {

  private static final String DASH = "-";

  private static final CharMatcher DASH_MATCHER = CharMatcher.is(DASH.charAt(0));

  private static final Pattern NON_ALPHANUM_PATTERN = Pattern.compile("[^A-Za-z0-9]+");

  public static String formatForUrl(String input) {
    return DASH_MATCHER.trimFrom(NON_ALPHANUM_PATTERN.matcher(StringUtils.stripAccents(input)).replaceAll(DASH)).toLowerCase();
  }

  private UrlUtils() {
  }

}
