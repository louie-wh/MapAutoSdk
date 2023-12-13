package com.baidu.mapautosdk.util;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class StringFormatUtils {

    public static String carFormatTimeString(int time) {
        if (time < 60) {
            return "1分钟";
        }

        // long day1 = time / (24 * 3600);
        long hour1 = time / 3600;
        long minute1 = time % 3600 / 60;
        long second1 = time % 60;

        if (minute1 > 59) {
            hour1 += 1;
            minute1 = 0;
        }

        StringBuffer buf = new StringBuffer();
        if (hour1 < 1) {
            if (minute1 >= 1) {
                // 1小时以内
                buf.append(minute1 + "分钟");
            }
        } else {
            buf.append(hour1 + "小时");
            if (minute1 >= 1) {
                buf.append(minute1 + "分");
            }
        }

        return buf.toString();
    }

    public static String carFormatTimeStringProcessDay(int time) {
        return carFormatTimeStringProcessDay(time, true);
    }

    public static String carFormatTimeStringProcessDay(int time, boolean needDetailed) {
        if (time < 60) {
            return "1分钟";
        }
        long day1 = time / (24 * 3600);
        long hour1 = time % (24 * 3600) / 3600;
        long minute1 = time % 3600 / 60;
        long second1 = time % 60;

        StringBuffer buf = new StringBuffer();

        if (day1 >= 1) {
            buf.append(day1 + "天");
        }
        if (hour1 >= 1) {
            if (needDetailed || buf.length() == 0) {
                buf.append(hour1 + "小时");
            }
        }
        if (minute1 >= 1) {
            if (needDetailed || buf.length() == 0) {
                buf.append(minute1 + "分钟");
            }
        }
        return buf.toString();
    }

    /**
     * 格式化米数为距离描述（为驾车页提供，由于算路距离过短需求新增 v10.8.0）
     *
     * @param meters 米
     * @return
     */
    public static String formatDistanceStringForRouteResult(int meters) {
        String strDis = "0米"; // fixed bug WMAP-9319 ，在只有一个点的情况下返回距离为0米
        if (meters >= 1000 * 10) {
            int integerKm = meters / 1000;
            if (meters % 1000 >= 500) {
                integerKm += 1;
            }
            strDis = String.format("%d公里", integerKm); // 行驶……公里
        } else if (meters >= 1000) {
            float fdis = (float) (meters / 1000.0);
            strDis = String.format("%.1f公里", fdis); // 行驶……公里
        } else if (meters >= 0) {
            int dis = meters;
            if (dis > 10) {
                dis = (meters + 5) / 10 * 10;
            } else if (dis < 1) {
                dis = 1;
            }
            strDis = String.format("%d米", dis); // 行驶……米
        } else {
            strDis = "1米";
        }

        return strDis;
    }

    /**
     * 格式化路线偏好信息(个人熟悉->个人熟悉的路线)
     */
    public static String formatRoutePrefer(String prefer) {
        if (TextUtils.isEmpty(prefer)) {
            return null;
        }
        String newPrefer;
        String endSuffix = "路线";
        if (prefer.endsWith(endSuffix)) {
            newPrefer = prefer;
        } else {
            newPrefer = prefer + "的" + endSuffix;
        }
        return newPrefer;
    }

    /**
     * 获取当前时间
     */
    public static String getDateStr(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        Date curDate = new Date(System.currentTimeMillis());
        String createDate = formatter.format(curDate);
        return createDate;
    }

    /**
     * 时间转换为时间戳
     */
    public static Long dateToStamp(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = 0;
        if (date != null) {
            ts = date.getTime();
        }
        return ts;
    }

    /**
     * 时间戳转换为时间
     */
    public static String stampToDate(Long s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date(s);
        res = simpleDateFormat.format(date);
        return res;
    }
}
