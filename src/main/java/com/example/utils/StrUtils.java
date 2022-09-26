package com.example.utils;

import jdk.internal.instrumentation.Logger;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Slf4j
public class StrUtils {
    private static final long nd = 1000 * 24 * 60 * 60;
    private static final long nh = 1000 * 60 * 60;
    private static final long nm = 1000 * 60;

    public static Date StrToDate(String str) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date StrToDate(String str, String format1) {

        SimpleDateFormat format = new SimpleDateFormat(format1);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static long computationTime(Date startTime, Date endTime) {
        try {
            log.info("开始时间->{}, 结束时间->{}", startTime, endTime);
            long diff = endTime.getTime() - startTime.getTime();
//            long day = diff / nd;
            return diff / nh;
//            long min = diff % nd % nh / nm;
//            long sec = diff % nd % nh % nm / 1000;
        } catch (Exception e) {
            log.info("计算两个时间段时间差出错了, {}", e);
            return 0;
        }
    }

    public static long computationDay(Date startTime, Date endTime) {
        try {
            log.info("开始时间->{}, 结束时间->{}", startTime, endTime);
            long diff = endTime.getTime() - startTime.getTime();
//            long day = diff / nd;
            return diff / nd;
//            long min = diff % nd % nh / nm;
//            long sec = diff % nd % nh % nm / 1000;
        } catch (Exception e) {
            log.info("计算两个时间段时间差出错了, {}", e);
            return 0;
        }
    }

    public static long timeDiff(Date startTime, Date endTime) {
        try {
            log.info("开始时间->{}, 结束时间->{}", startTime, endTime);
            long diff = endTime.getTime() - startTime.getTime();
            long day = diff / nd;
            long min = diff % nd % nh / nm;
            long sec = diff % nd % nh % nm / 1000;
            return diff / nh;
        } catch (Exception e) {
            log.info("计算两个时间段时间差出错了, {}", e);
            return 0;
        }
    }

    public static boolean isNumber(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
