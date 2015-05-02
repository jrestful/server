package org.jrestful.util;

import java.util.Calendar;
import java.util.Date;

public final class DateUtils {

  public static Date addToNow(int field, int amount) {
    Calendar calendar = Calendar.getInstance();
    calendar.add(field, amount);
    return calendar.getTime();
  }

  public static Date removeToNow(int field, int amount) {
    return addToNow(field, -amount);
  }

  private DateUtils() {
  }

}
