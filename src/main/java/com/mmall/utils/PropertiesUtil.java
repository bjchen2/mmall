package com.mmall.utils;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 获取配置文件的值
 * Created By Cx On 2018/9/2 16:11
 */
public class PropertiesUtil {

    private static Properties properties;

    static {
        String fileName="mmall.properties";
        properties = new Properties();
        try {
            properties.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key){
        String value = properties.getProperty(key.trim());
        return value.trim();
    }

}
