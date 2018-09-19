package com.mmall.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Created by geely
 */
public class MD5Util {

    private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private static String byteArrayToHexString(byte b[]) {
        //在单线程情况下优先使用StringBuilder，效率更高，StringBuffer能保证线程安全
        StringBuilder resultSb = new StringBuilder();
        for (byte bb: b){
            resultSb.append(byteToHexString(bb));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * 返回编码为utf_8的大写MD5
     */
    private static String MD5Encode(String origin) {
        String resultString;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            return null;
        }
        return resultString.toUpperCase();
    }

    public static String MD5EncodeUtf8(String origin) {
        //加盐值，防止被轻易遍历破解
        origin = origin + PropertiesUtil.getProperty("password.salt");
        return MD5Encode(origin);
    }

}
