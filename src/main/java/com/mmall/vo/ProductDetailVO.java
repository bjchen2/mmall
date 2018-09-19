package com.mmall.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by geely
 */
@Data
public class ProductDetailVO {

    private Integer id;
    private Integer categoryId;
    private String name;
    private String subtitle;
    private String mainImage;
    private String subImages;
    private String detail;
    private BigDecimal price;
    private Integer stock;
    private Integer status;

    private String createTime;
    private String updateTime;

    //图片存储url前缀
    private String imageHost;
    private Integer parentCategoryId;
}
