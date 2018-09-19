package com.mmall.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * 使用joda工具类（需导入pom），格式化日期
 * Created By Cx On 2018/9/2 15:40
 */
public class DateTimeUtil {

    //一般通用格式
    private static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date str2Date(String dateTimeStr,String formatStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static Date str2Date(String dateTimeStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String date2Str(Date date,String formatStr){
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    public static String date2Str(Date date){
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }
}
