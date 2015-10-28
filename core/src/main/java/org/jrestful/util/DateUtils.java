package org.jrestful.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Operations on dates.
 */
public final class DateUtils {

  public interface PredefinedDateFormat {

    String format(Date date);

    Date parse(String date) throws ParseException;

  }

  public static final PredefinedDateFormat RFC_1123 = new PredefinedDateFormat() {

    @Override
    public String format(Date date) {
      return build().format(date);
    }

    @Override
    public Date parse(String date) throws ParseException {
      return build().parse(date);
    }

    private DateFormat build() {
      SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
      dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
      dateFormat.setLenient(false);
      return dateFormat;
    }

  };

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
