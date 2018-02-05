package com.syezon.note_xh.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间戳与时间字符串间的转换
 */
public class DateUtils {
    /**
     * 掉此方法输入所要转换的时间输入例如（"2014-06-14-16-09-00"，"yyyy-MM-dd-HH-mm-ss"）返回时间戳
     */
    public static String getTimeStamp(String time, String type) {
        SimpleDateFormat sdr = new SimpleDateFormat(type, Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
//            times = stf.substring(0, 10);
            times = stf;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return times;
    }

    /**
     * 调用此方法输入所要转换的时间戳输入例如（"1402733340","yyyy年MM月dd日HH时mm分"）输出（"2014年06月14日16时09分"）
     */
    public static String getTimeByTimeStamp(String timeStamp,String type) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdr = new SimpleDateFormat(type);
                Log.d("pppp",timeStamp);
        long lcc = Long.valueOf(timeStamp);
//        int i = Integer.parseInt(timeStamp);
//        String times = sdr.format(new Date(i * 1000L));
        String times = sdr.format(new Date(lcc));
        return times;
    }

    /**
     调用此方法输入所要转换的时间戳例如（1402733340）输出（""）
     */
    public static String getTime2(long timeStamp) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdr = new SimpleDateFormat("MM月dd日  #  HH:mm");
        return sdr.format(new Date(timeStamp)).replaceAll("#",
                getWeek(timeStamp));

    }

    /**
     *调用此方法返回星期几
     */
    private static String getWeek(long timeStamp) {
        int mydate = 0;
        String week = null;
        Calendar cd = Calendar.getInstance();
        cd.setTime(new Date(timeStamp));
        mydate = cd.get(Calendar.DAY_OF_WEEK);
        // 获取指定日期转换成星期几
        if (mydate == 1) {
            week = "周日";
        } else if (mydate == 2) {
            week = "周一";
        } else if (mydate == 3) {
            week = "周二";
        } else if (mydate == 4) {
            week = "周三";
        } else if (mydate == 5) {
            week = "周四";
        } else if (mydate == 6) {
            week = "周五";
        } else if (mydate == 7) {
            week = "周六";
        }
        return week;
    }

    /**
     * 并用分割符把时间分成时间数组
     */
    public static String[] getTimeArrayByTimeStamp(String timeStamp) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(timeStamp);
//        int i = Integer.parseInt(timeStamp);
//        String times = sdr.format(new Date(i * 1000L));
        String times = sdr.format(new Date(lcc));
        String[] fenge = times.split("[年月日时分秒]");
        return fenge;
    }

    /**
     * 输入时间戳变星期
     */
    public static String getWeekByTime(String timeStamp) {
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(timeStamp);
//        int i = Integer.parseInt(timeStamp);
        String week = null;
        Calendar cd = Calendar.getInstance();
//        cd.setTime(new Date(i * 1000L));
        cd.setTime(new Date(lcc));
        int mydate = cd.get(Calendar.DAY_OF_WEEK);
        if (mydate == 1) {
            week = "星期日";
        } else if (mydate == 2) {
            week = "星期一";
        } else if (mydate == 3) {
            week = "星期二";
        } else if (mydate == 4) {
            week = "星期三";
        } else if (mydate == 5) {
            week = "星期四";
        } else if (mydate == 6) {
            week = "星期五";
        } else if (mydate == 7) {
            week = "星期六";
        }
        return week;

    }

    /**
     * 输入日期,type如（"2014-06-14-16-09-00","yyyy-MM-dd-HH-mm-ss"）返回（星期数）
     */
    public static String getWeekByTimeStamp(String time,String type) {
        Date date = null;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdr = new SimpleDateFormat(type);
        int week_date = 0;
        String week = null;
        try {
            date = sdr.parse(time);
            Calendar cd = Calendar.getInstance();
            cd.setTime(date);
            week_date = cd.get(Calendar.DAY_OF_WEEK);
            // 获取指定日期转换成星期几
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (week_date == 1) {
            week = "星期日";
        } else if (week_date == 2) {
            week = "星期一";
        } else if (week_date == 3) {
            week = "星期二";
        } else if (week_date == 4) {
            week = "星期三";
        } else if (week_date == 5) {
            week = "星期四";
        } else if (week_date == 6) {
            week = "星期五";
        } else if (week_date == 7) {
            week = "星期六";
        }
        return week;
    }
}
