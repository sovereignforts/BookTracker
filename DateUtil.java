package com.booktracker.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private static final SimpleDateFormat KEY_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private static final SimpleDateFormat DISPLAY_FORMAT =
            new SimpleDateFormat("EEE, MMM d", Locale.getDefault());

    public static String todayKey() {
        return KEY_FORMAT.format(new Date());
    }

    public static String toDisplayDate(String dateKey) {
        try {
            Date d = KEY_FORMAT.parse(dateKey);
            return d != null ? DISPLAY_FORMAT.format(d) : dateKey;
        } catch (Exception e) {
            return dateKey;
        }
    }

    public static String formatSeconds(long seconds) {
        if (seconds < 60) return seconds + "s";
        long mins = seconds / 60;
        if (mins < 60) return mins + " min";
        return (mins / 60) + "h " + (mins % 60) + "m";
    }

    public static String formatMillis(long millis) {
        return formatSeconds(millis / 1000);
    }
}
