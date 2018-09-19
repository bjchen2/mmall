package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.pojo.Shipping;

/**
 * Created By Cx On 2018/9/9 9:52
 */
public interface ShippingService {

    /**
     * 通过 userId 分页返回某用户的所有收获地址
     */
    PageInfo getAllByUserId(Integer userId,int pageNum,int pageSize);

    /**
     * 添加收获地址
     */
    Shipping insert(Shipping shipping);

    /**
     * 需要保证参数 shipping 中的 Id 属性是非空的
     */
    void updateByUserIdAndShippingId(Shipping shipping);

    /**
     * 必须要有 userId 用来校验该 shipping 是否为该用户的，防止越权
     */
    void deleteByUserIdAndShippingId(Integer userId,Integer shippingId);

    /**
     * userId 用于防止越权
     */
    Shipping getOneByUserIdAndShippingId(Integer userId,Integer shippingId);

}
