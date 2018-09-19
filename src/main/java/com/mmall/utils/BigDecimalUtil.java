package com.mmall.utils;

import java.math.BigDecimal;

/**
 * 涉及金额的项目，都应该使用BigDecimal（String构造方法），否则会出现精度缺失
 * 数据库映射出来的BigDecimal类可以直接使用不用再调用String构造器
 * 但为了保险，统一使用工具类处理
 * Created By Cx On 2018/9/6 16:15
 */
public class BigDecimalUtil {

    //参数用double再转换为String不是多此一举，而是防止传参格式错误
    public static BigDecimal add(Double v1, Double v2){
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.add(b2);
    }

    public static BigDecimal sub(Double v1,Double v2){
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.subtract(b2);
    }


    public static BigDecimal mul(Double v1,Double v2){
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.multiply(b2);
    }

    public static BigDecimal div(Double v1,Double v2){
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        //保留2位小数,保留方式为：四舍五入
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);
    }
}
