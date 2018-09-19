package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created By Cx On 2018/8/25 0:38
 */
public interface Const {
    String CURRENT_USER = "currentUser";
    String EMAIL = "email";
    String USERNAME = "username";
    /**
     * 生成token的前缀，即生成token的格式应该是token_XXXX
     */
    String TOKEN_PREFIX = "token_";

    /**
     * 商品排序方式
     * 使用set，查询速度O(1),List为O(n)
     */
    Set<String> PRODUCTS_ORDER_BY = Sets.newHashSet("price_desc","price_asc");

    interface Role{
        int ROLE_CUSTOMER = 0;    //普通用户
        int ROLE_ADMIN = 1;    //管理员
    }

    interface Cart{
        int CHECKED = 1;//购物车选中状态
        int UN_CHECKED = 0;//购物车未选中状态

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";   //购物车添加商品数量大于剩余库存（强制等于剩余库存）
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";    //购物车添加商品数量不大于剩余库存
    }

    interface AliCallBack{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }
}
