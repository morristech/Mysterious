package com.darin.mysterious.utils;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FormatUtils {

    public static final String FORMAT_12H = "h:mm:ss";
    public static final String FORMAT_24H = "HH:mm:ss";
    public static final String FORMAT_12H_SHORT = "h:mm a";
    public static final String FORMAT_24H_SHORT = "HH:mm";
    public static final String FORMAT_DATE = "MMMM d yyyy";

    public static String getFormat(Context context) {
        return DateFormat.is24HourFormat(context) ? FORMAT_24H : FORMAT_12H;
    }

    public static String getShortFormat(Context context) {
        return DateFormat.is24HourFormat(context) ? FORMAT_24H_SHORT : FORMAT_12H_SHORT;
    }

    public static String format(Context context, Date time) {
        return format(time, getFormat(context));
    }

    public static String formatShort(Context context, Date time) {
        return format(time, getShortFormat(context));
    }

    public static String format(Date time, String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(time);
    }

    public static String formatMillis(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) % TimeUnit.DAYS.toMinutes(1);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1);

        if (days > 0)
            return String.format(Locale.getDefault(), "%dd %02dh %02dm %02ds", days, hours, minutes, seconds);
        else if (hours > 0)
            return String.format(Locale.getDefault(), "%dh %02dm %02ds", hours, minutes, seconds);
        else if (minutes > 0)
            return String.format(Locale.getDefault(), "%dm %02ds", minutes, seconds);
        else return String.format(Locale.getDefault(), "%ds", seconds);
    }
}
