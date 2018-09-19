package com.mmall.dao;

import com.mmall.pojo.Cart;
import com.mmall.vo.CartVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int insert(Cart cart);

    /**
     * 通过 userId 和 productId 查询符合要求的订单
     */
    Cart getOneByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    /**
     * 通过 id 更新 quantity 和 checked （如果非空）
     */
    int updateQuantityAndCheckedById(Cart cart);

    /**
     * 获取某用户购物车中的所有商品
     */
    List<Cart> getAllByUserId(Integer userId);

    /**
     * 获取某用户购物车中的所有已选商品
     */
    List<Cart> getAllCheckedByUserId(Integer userId);

    /**
     * 判断用户的购物车是否全选
     */
    Boolean isAllCheckedByUserId(Integer userId);

    /**
     * 删除该用户的某些商品
     */
    int delAllByUserId(@Param("userId") Integer userId,@Param("productIds") List<Integer> productIds);

    int delAllByIds(List<Integer> cartIds);

    /**
     * 修改某个用户的所有购物车状态
     * 用于全选 或 全不选
     */
    int updateAllCheckedByUserId(@Param("userId")Integer userId, @Param("checked")Integer checked);

    /**
     * 修改某个用户购物车中的某个商品选择状态
     */
    int updateOneCheckedByUserIdAndProductId(@Param("userId")Integer userId,@Param("productId")Integer productId,@Param("checked")Integer checked);

    /**
     * 获取某个用户的购物车商品数量
     * 注意：是数量而不是种类，如：A商品买了5件，则返回5
     */
    int getCartProductCount(Integer userId);
}