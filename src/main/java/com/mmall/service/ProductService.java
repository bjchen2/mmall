package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVO;

/**
 * Created By Cx On 2018/9/1 23:23
 */
public interface ProductService {

    /**
     * 添加或更新商品
     * 区别在于： 更新商品，商品Id不为空
     */
    void saveOrUpdateProduct(Product product);

    /**
     * 通过商品Id更新商品上下架状态
     */
    void setSaleStatus(int productId,int productStatus);

    /**
     * 获取商品详情
     */
    ProductDetailVO getProductDetail(Integer productId);

    /**
     * 通过分页获取商品列表
     * 使用了mybatis pagehelper轮子
     */
    PageInfo getProductList(int pageNum,int pageSize);

    /**
     * 通过商品名称和商品ID模糊查询 所有 商品
     * 分页返回 所有 符合条件的商品列表
     * 用于后台
     */
    PageInfo getProductByNameAndId(String productName,Integer productId,int pageNum,int pageSize);

    /**
     * 通过类目和商品名称 ，以 orderBy 的顺序 （key和categoryId都为null则相当于查询所有上架商品）
     * 分页返回符合条件的 已上架 商品
     * 用于客户端
     */
    PageInfo getOnSaleByKeyAndCategoryId(String key, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy);


}
