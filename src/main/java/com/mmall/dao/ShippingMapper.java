package com.mmall.dao;

import com.mmall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int insert(Shipping shipping);

    List<Shipping> getAllByUserId(Integer userId);

    Shipping getOneByUserIdAndShippingId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    Shipping getOneByShippingId(Integer shippingId);

    int updateByUserIdAndShippingId(Shipping shipping);

    int deleteByUserIdAndShippingId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

}