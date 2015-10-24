package org.jrestful.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Operations on dates.
 */
public final class DateUtils {

  public static Date add(Date date, int field, int amount) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(field, amount);
    return calendar.getTime();
  }

  public static Date addToNow(int field, int amount) {
    return add(new Date(), field, amount);
  }

  public static Date remove(Date date, int field, int amount) {
    return add(date, field, -amount);
  }

  public static Date removeToNow(int field, int amount) {
    return remove(new Date(), field, amount);
  }

  private DateUtils() {
  }

}
