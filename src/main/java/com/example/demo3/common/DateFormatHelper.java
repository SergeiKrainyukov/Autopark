package com.example.demo3.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateFormatHelper {
    private static final String dateFormatPattern = "dd.MM.yyyy HH:mm:ss";
    private static final String UTC_TIMEZONE = "UTC";

    public static String getZonedTimeStringFormatted(String timezone, Long dateMillis) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIMEZONE));
        calendar.setTime(new Date(dateMillis));
        DateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        return dateFormat.format(calendar.getTime());
    }

    public static String getZonedTimeStringFormatted(TimeZone timezone, Long dateMillis) {
        Calendar calendar = Calendar.getInstance(timezone);
        calendar.setTime(new Date(dateMillis));
        DateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
        dateFormat.setTimeZone(timezone);
        return dateFormat.format(calendar.getTime());
    }

    public static Long getLongDate(String date) {
        try {
            Date parsedDate = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(date);
            return parsedDate.getTime();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0L;
        }
    }
}
