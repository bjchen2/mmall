package com.mmall.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态枚举类
 * Created By Cx On 2018/9/18 16:29
 */
@Getter
@AllArgsConstructor
public enum OrderStatusEnum implements CodeEnum {
    CANCELED(0,"已取消"),
    NO_PAY(10,"未支付"),
    PAID(20,"已付款"),
    SHIPPED(40,"已发货"),
    ORDER_SUCCESS(50,"订单完成"),
    ORDER_CLOSE(60,"订单关闭");

    private int code;
    private String value;

}
