package com.mmall.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created By Cx On 2018/8/24 23:37
 */
@Getter
@AllArgsConstructor
public enum ResultEnum {
    SUCCESS(0,"请求成功"),
    ERROR(1,"请求失败"),
    NEED_LOGIN(10,"需要登录"),
    ILLEGAL_ARGUMENT(12,"服务器异常");

    private final Integer code;
    private final String msg;
}
