package com.gempukku.swccgo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtils {
    public static int getCurrentDate() {
        Calendar date = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        return date.get(Calendar.YEAR) * 10000 + (date.get(Calendar.MONTH) + 1) * 100 + date.get(Calendar.DAY_OF_MONTH);
    }

    public static int getCurrentMinute() {
        Calendar date = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        return date.get(Calendar.YEAR) * 100000000 + (date.get(Calendar.MONTH) + 1) * 1000000 + date.get(Calendar.DAY_OF_MONTH) * 10000 + date.get(Calendar.HOUR_OF_DAY) * 100 + date.get(Calendar.MINUTE);
    }

    public static String getStringDateWithHour() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(new Date());
    }

    public static String formatDateWithHour(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(date);
    }

    public static Date parseDateWithHour(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.parse(date);
    }

    public static int offsetDate(int start, int dayOffset) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            Date date = format.parse(String.valueOf(start));
            date.setDate(date.getDate() + dayOffset);
            return Integer.parseInt(format.format(date));
        } catch (ParseException exp) {
            throw new RuntimeException("Can't parse date", exp);
        }
    }

    public static int getMondayBeforeOrOn(int date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            Date current = format.parse(String.valueOf(date));
            if (current.getDay() == 0)
                return offsetDate(date, -6);
            else
                return offsetDate(date, 1 - current.getDay());
        } catch (ParseException exp) {
            throw new RuntimeException("Can't parse date", exp);
        }
    }
}
