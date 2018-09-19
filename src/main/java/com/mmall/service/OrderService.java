package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.vo.OrderProductVO;
import com.mmall.vo.OrderVO;

import java.util.Map;

/**
 * Created By Cx On 2018/9/11 20:16
 */
public interface OrderService {

    /**
     * 创建订单，shippingId 为收获信息Id
     */
    OrderVO create(Integer userId, Integer shippingId);

    /**
     * 取消订单 （仅限于未付款订单，因为支付宝退款接口还未写）
     */
    void cancel(Integer userId,Long orderNo);

    /**
     * 获取用户购物车中已选商品
     */
    OrderProductVO getOrderCartProduct(Integer userId);

    /**
     * 获取订单详情
     */
    OrderVO getOrderDetail(Integer userId, Long orderNo);

    /**
     * 获取订单列表
     */
    PageInfo getOrderList(Integer userId, int pageNum, int pageSize);



    /*===================================================后台管理员相关===============================================*/

    /**
     * 获取所有订单信息
     */
    PageInfo manageList(int pageNum,int pageSize);

    /**
     * 获取某个订单的详情
     */
    OrderVO manageDetail(Long orderNo);

    /**
     * todo  一期精准查询 orderNo ，后期会改为模糊查询 orderNo、Name等，所以使用分页返回
     */
    PageInfo manageSearch(Long orderNo,int pageNum,int pageSize);

    /**
     * 更新订单信息为已发货
     */
    void manageSendGoods(Long orderNo);


    /*=======================================================支付宝相关===============================================*/

    /**
     * 支付订单号为 orderNo 的订单,将得到的二维码持久化到 path 路径下并上传到 FTP 服务器， userId 用于校验订单是否属于该用户
     * 返回Map中包含： 支付二维码 FTP 服务器 URL ， 支付的订单ID
     */
    Map<String,String> pay(Long orderNo,Integer userId,String path);

    /**
     * 校验支付回调参数是否正确
     * 如果正确，修改订单状态，并将支付信息持久化到数据库
     */
    Boolean checkAliCallBack(Map<String,String>params);

    /**
     * 通过订单号，查询该用户的订单是否已付款
     */
    Boolean queryOrderPayStatus(Integer userId, Long orderNo);
}
