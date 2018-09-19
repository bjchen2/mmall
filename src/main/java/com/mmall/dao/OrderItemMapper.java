package com.mmall.dao;

import com.mmall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int insert(OrderItem orderItem);

    int insertAll(List<OrderItem> orderItems);

    List<OrderItem> getAllByOrderNoAndUserId(@Param("orderNo") Long orderNo, @Param("userId") Integer userId);

    /**
     * 用于管理员查询订单详情，用户查询请使用 订单号+用户ID 的方法查询，防止越权
     */
    List<OrderItem> getAllByOrderNo(Long orderNo);
}