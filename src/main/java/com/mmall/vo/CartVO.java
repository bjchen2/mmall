package com.mmall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created By Cx On 2018/9/5 15:53
 */
@Data
public class CartVO {

    private List<CartProductVO> cartProductVoList;
    //购物车总价
    private BigDecimal cartTotalPrice;
    //是否全选
    private Boolean allChecked;
    private String imageHost;
}
