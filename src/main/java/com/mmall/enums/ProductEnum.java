package com.mmall.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created By Cx On 2018/9/4 17:56
 */
@Getter
@AllArgsConstructor
public enum ProductEnum {
    ON_SALE(1,"在架上"),
    ;

    private final Integer code;
    private final String msg;
}
