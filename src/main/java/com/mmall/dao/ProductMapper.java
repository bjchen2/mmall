package com.mmall.dao;

import com.mmall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ProductMapper {
    int insert(Product product);

    int updateById(Product product);

    int updateStockByIds(List<Product> products);

    /**
     * 通过ID获得对象
     */
    Product getById(Integer productId);

    /**
     * 通过ID集合获得对象
     */
    List<Product> getAllByIds(List<Integer> productIds);

    /**
     * 获取所有商品
     */
    List<Product> getAll();

    /**
     * 通过商品名称和商品ID模糊查询 所有 商品
     * 用于后台
     */
    List<Product> getAllByProductIdAndName(@Param("id") Integer id, @Param("name") String name);

    /**
     * 通过类目和商品名称查询符合条件的 已上架 商品（key和categoryId都为null则相当于查询所有上架商品）
     * 用于客户端
     */
    List<Product> getOnSaleByProductNameAndCategoryId(@Param("name") String name,@Param("categoryIds") Set<Integer> categoryIds);
}