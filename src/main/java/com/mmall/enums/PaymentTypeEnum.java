package com.mmall.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created By Cx On 2018/9/18 20:15
 */
@Getter
@AllArgsConstructor
public enum PaymentTypeEnum implements CodeEnum {
    ONLINE_PAY(1,"在线支付");

    private int code;
    private String value;

}
