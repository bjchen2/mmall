package com.mmall.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车商品
 * Created By Cx On 2018/9/5 15:53
 */
@Data
public class CartProductVO {

    private Integer cartId;
    private Integer userId;
    private Integer productId;
    //此商品是否勾选
    private Integer productChecked;
    //购物车中此商品的数量(如果数量大于productStock，则强制等于productStock)
    private Integer quantity;
    //购买前的剩余数量
    private Integer productStock;
    private String productName;
    private String productSubtitle;
    private String productMainImage;
    private BigDecimal productPrice;
    private Integer productStatus;
    private BigDecimal productTotalPrice;


    //限制数量的一个返回结果，如果quantity大于productStock则返回"LIMIT_NUM_FAIL",不超过则返回"LIMIT_NUM_SUCCESS"
    private String limitQuantity;
}
