package com.ennova.outscreen.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;

public class DateUtils {
    @SuppressLint("SimpleDateFormat")
    public static String getYYMMDD(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
        return sdf.format(time);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getYMDHMS(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(time);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getMDHM(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("M月d日 HH:mm");
        return sdf.format(new Date(time));
    }

    @SuppressLint("SimpleDateFormat")
    public static String getMD(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("M月d日");
        return sdf.format(new Date(time));
    }

    @SuppressLint("SimpleDateFormat")
    public static String getMDFromStringDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("M月d日");
        long time = getStringToDate(date);
        return sdf.format(new Date(time));
    }

    @SuppressLint("SimpleDateFormat")
    public static String getWeekFromStringDate(String date) {
        long time = getStringToDate(date);
        return getWeek(time);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getHHMM(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date(time));
    }


    public static String getGeneralTime(long time) {
        long nowTime = System.currentTimeMillis();
        long betweenTime = nowTime - time;

        if (betweenTime >= 0 && betweenTime < 60 * 1000) {
            return "刚刚";
        } else if (betweenTime >= 60 * 1000 && betweenTime < 3600 * 1000) {
            int minute = (int) (betweenTime / (60 * 1000));
            return minute + "分钟前";
        }
//        else if (betweenTime >= 60 * 1000 && betweenTime < 24 * 3600 * 1000) {
//            int hour = (int) (betweenTime / (3600 * 1000));
//            return hour + "小时前";
//        }
        else if (getYYMMDD(nowTime).equals(getYYMMDD(time))) {
            return getHHMM(time);
        } else {
            return getYYMMDD(time);
        }
    }

    /*将字符串转为时间戳*/
    public static long getStringToDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static String getWeek(long time) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(new Date(time));
        int week = cd.get(Calendar.DAY_OF_WEEK); //获取星期
        String weekString;
        switch (week) {
            case Calendar.SUNDAY:
                weekString = "周日";
                break;
            case Calendar.MONDAY:
                weekString = "周一";
                break;
            case Calendar.TUESDAY:
                weekString = "周二";
                break;
            case Calendar.WEDNESDAY:
                weekString = "周三";
                break;
            case Calendar.THURSDAY:
                weekString = "周四";
                break;
            case Calendar.FRIDAY:
                weekString = "周五";
                break;
            default:
                weekString = "周六";
                break;
        }
        return weekString;
    }
}
