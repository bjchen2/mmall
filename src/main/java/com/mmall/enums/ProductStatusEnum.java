package com.mmall.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created By Cx On 2018/9/18 20:04
 */
@AllArgsConstructor
@Getter
public enum ProductStatusEnum {
    ON_SALE(1,"在售");

    private int code;
    private String value;
}
