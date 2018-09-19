package com.mmall.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created By Cx On 2018/9/18 22:42
 */
@Data
public class OrderItemVO {
    private Long orderNo;

    private Integer productId;

    private String productName;

    private String productImage;

    private BigDecimal currentUnitPrice;

    private Integer quantity;

    private BigDecimal totalPrice;

    private String createTime;
}
