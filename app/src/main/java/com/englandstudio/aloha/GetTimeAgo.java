package com.englandstudio.aloha;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GetTimeAgo {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    private static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = currentDate().getTime();
        if (time > now || time <= 0) {
            return "Vừa xong";
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Vừa xong";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1 phút trước";
        } else if (diff < 60 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " phút trước";
        } else if (diff < 2 * HOUR_MILLIS) {
            return "1 giờ trước";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " giờ trước";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Hôm qua";
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            String HOUR = hour + "";
            String MINUTE = minute + "";
            if (hour < 10) {
                HOUR = "0" + hour;
            }
            if (minute < 10) {
                MINUTE = "0" + minute;
            }
            return day + " tháng " + month + " " + year + " lúc " + HOUR + ":" + MINUTE;
        }
    }

    public static String getHour(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = currentDate().getTime();

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        String HOUR = hour + "";
        String MINUTE = minute + "";


        if (hour < 10) {
            HOUR = "0" + hour;
        }
        if (minute < 10) {
            MINUTE = "0" + minute;
        }
        if (time > now || time <= 0) {
            return "Vừa xong";
        }

        final long diff = now - time;
        if (diff < 12 * HOUR_MILLIS) {
            return HOUR + ":" + MINUTE;
        } else {
            return day + " tháng " + month + " " + year + " lúc " + HOUR + ":" + MINUTE;
        }
    }
}
