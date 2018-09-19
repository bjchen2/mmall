package com.mmall.dao;

import com.mmall.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int insert(Order order);

    Order getOneByOrderNoAndUserId(@Param("orderNo")Long orderNo,@Param("userId")Integer userId);

    /**
     * 因为支付宝回调不会有 用户信息，所以使用该方法，其余情况建议使用 订单号+用户ID 的方法，防止越权问题
     */
    Order getOneByOrderNo(Long orderNo);

    /**
     * 查询某用户的所有订单
     */
    List<Order> getAllByUserId(Integer userId);

    /**
     * 获取所有订单  ——用于管理员
     */
    List<Order> getAll();

    int updateByOrderId(Order order);
}