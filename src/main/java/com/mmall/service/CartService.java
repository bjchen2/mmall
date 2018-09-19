package com.mmall.service;

import com.mmall.pojo.Cart;
import com.mmall.vo.CartVO;

import java.util.List;

/**
 * 购物车有关
 * Created By Cx On 2018/9/5 15:50
 */
public interface CartService {
    /**
     * 添加商品进购物车
     * 返回添加后的购物车信息
     */
    CartVO add(Integer userId, Integer productId, Integer quantity);

    /**
     * 返回购物车信息
     */
    CartVO list(Integer userId);

    /**
     * 更新购物车商品数量
     */
    CartVO updateQuantity(Integer userId, Integer productId, Integer quantity);

    /**
     * 删除该用户的某些商品
     */
    CartVO delAllByUserId(Integer userId, List<Integer> productIds);

    /**
     * 修改某个用户的所有购物车状态
     * 用于全选 或 全不选
     */
    CartVO updateAllCheckedByUserId(Integer userId,Integer checked);

    /**
     * 修改某个用户购物车中的某个商品选择状态
     */
    CartVO updateOneCheckedByUserIdAndProductId(Integer userId,Integer productId,Integer checked);

    /**
     * 获取某个用户的购物车商品数量
     * 注意：是数量而不是种类，如：A商品买了5件，则返回5
     */
    Integer getCartProductCount(Integer userId);
}
