package com.fudan.stress.common;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {
    public static String  timestampToString(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time));
    }

    public static long stringToTimestamp(String date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date, new ParsePosition(0)).getTime();
    }

    public static int getHourByTimestamp(long time) {
        String date = timestampToString(time);
        int hour = Integer.parseInt(date.substring(11, 13));
        return hour;
    }

    public static int getMinuteByTimeStamp(long time) {
        String date = timestampToString(time);
        int minute = Integer.parseInt(date.substring(14, 16));
        return minute;
    }

}
