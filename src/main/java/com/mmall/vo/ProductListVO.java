package com.mmall.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created By Cx On 2018/9/2 19:48
 */
@Data
public class ProductListVO {

    private Integer id;
    private Integer categoryId;
    private String name;
    private String subtitle;
    private String mainImage;
    private BigDecimal price;
    private Integer status;

    private String imageHost;
}
