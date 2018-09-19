package com.mmall.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.GoodsDetail;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.config.AlipayConfig;
import com.mmall.dao.*;
import com.mmall.enums.OrderStatusEnum;
import com.mmall.enums.PaymentTypeEnum;
import com.mmall.enums.ProductStatusEnum;
import com.mmall.enums.ResultEnum;
import com.mmall.exception.SellException;
import com.mmall.pojo.*;
import com.mmall.service.OrderService;
import com.mmall.utils.*;
import com.mmall.vo.OrderItemVO;
import com.mmall.vo.OrderProductVO;
import com.mmall.vo.OrderVO;
import com.mmall.vo.ShippingVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created By Cx On 2018/9/11 20:16
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    PayInfoMapper payInfoMapper;
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    ShippingMapper shippingMapper;

    private List<OrderItem> cartList2orderItemList(Integer userId, List<Cart> cartList){
        List<OrderItem> orderItemList = new ArrayList<>();
        if(CollectionUtils.isEmpty(cartList)){
            log.error("[创建订单]创建失败，购物车为空");
            throw new SellException(ResultEnum.ERROR.getCode(),"购物车为空");
        }

        //获取购物车中所有产品的信息,先将数据一次性从数据库中拿出来，而不是每次拿一个，否则效率很低
        List<Product> productList = productMapper.getAllByIds(cartList.stream().map(Cart::getProductId).
                collect(Collectors.toList()));
        Map<Integer,Product> productMap = productList.stream().collect(Collectors.toMap(Product::getId,e->e));
        //校验购物车的数据,包括产品的状态和数量
        for(Cart cartItem : cartList){
            OrderItem orderItem = new OrderItem();
            //获取商品信息
            Product product = productMap.get(cartItem.getProductId());
            if(ProductStatusEnum.ON_SALE.getCode() != product.getStatus()){
                log.error("[创建订单]创建失败，勾选产品已下架，product = {}",product);
                throw new SellException(ResultEnum.ERROR.getCode(),"勾选产品已下架");
            }

            //校验库存
            if(cartItem.getQuantity() > product.getStock()){
                log.error("[创建订单]创建失败，勾选产品库存不足，product = {}",product);
                throw new SellException(ResultEnum.ERROR.getCode(),"勾选产品库存不足");
            }

            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartItem.getQuantity().
                    doubleValue()));
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    // assemble ： 装配
    private Order assembleOrder(Integer userId,Integer shippingId,BigDecimal payment){
        Order order = new Order();
        //生成订单号
        long orderNo = System.currentTimeMillis()+new Random().nextInt(100);
        order.setOrderNo(orderNo);
        order.setStatus(OrderStatusEnum.NO_PAY.getCode());
        //todo 因为还没有邮费功能，所以默认所有包邮
        order.setPostage(0);
        order.setPaymentType(PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPayment(payment);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        if( orderMapper.insert(order) < 1){
            log.error("[创建订单]创建失败，服务器连接失败，order = {}",order);
            throw new SellException(ResultEnum.ERROR.getCode(),"服务器连接失败");
        }
        return order;
    }

    private OrderVO assembleOrderVO(Order order,List<OrderItem> orderItemList){
        OrderVO orderVO = new OrderVO();
        if (order == null || orderItemList == null){
            log.error("[组装订单信息]组装失败，组装参数为空，order = {}， orderItemList = {}",order,orderItemList);
            throw new SellException(ResultEnum.ERROR.getCode(),"组装参数为空");
        }

        BeanUtils.copyProperties(order,orderVO);

        PaymentTypeEnum paymentTypeEnum = EnumUtil.getByCode(order.getPaymentType(),PaymentTypeEnum.class);
        if (paymentTypeEnum == null){
            log.error("[组装订单信息]组装失败，支付类型有误，paymentType = {}",order.getPaymentType());
            throw new SellException(ResultEnum.ERROR.getCode(),"支付类型有误");
        }
        orderVO.setPaymentTypeDesc(paymentTypeEnum.getValue());

        OrderStatusEnum orderStatusEnum =  EnumUtil.getByCode(order.getStatus(),OrderStatusEnum.class);
        if (orderStatusEnum == null){
            log.error("[组装订单信息]组装失败，订单状态有误，Status = {}",order.getStatus());
            throw new SellException(ResultEnum.ERROR.getCode(),"订单状态有误");
        }
        orderVO.setStatusDesc(orderStatusEnum.getValue());

        Shipping shipping = shippingMapper.getOneByShippingId(order.getShippingId());
        if(shipping == null){
            log.error("[组装订单信息]组装失败，shippingId有误，shippingId = {}",order.getShippingId());
            throw new SellException(ResultEnum.ERROR.getCode(),
                    "用户收获信息不完善，请确认shippingId是否正确，若正确，请先填写用户收获信息");
        }
        orderVO.setReceiverName(shipping.getReceiverName());
        orderVO.setShippingVO(shipping2VO(shipping));

        orderVO.setPaymentTime(DateTimeUtil.date2Str(order.getPaymentTime()));
        orderVO.setSendTime(DateTimeUtil.date2Str(order.getSendTime()));
        orderVO.setEndTime(DateTimeUtil.date2Str(order.getEndTime()));
        orderVO.setCreateTime(DateTimeUtil.date2Str(order.getCreateTime()));
        orderVO.setCloseTime(DateTimeUtil.date2Str(order.getCloseTime()));

        orderVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        List<OrderItemVO> orderItemVoList = orderItemList.stream().map(this::orderItem2VO).collect(Collectors.toList());

        orderVO.setOrderItemVOList(orderItemVoList);
        return orderVO;
    }

    private ShippingVO shipping2VO(Shipping shipping){
        ShippingVO shippingVO = new ShippingVO();
        BeanUtils.copyProperties(shipping,shippingVO);
        return shippingVO;
    }

    private OrderItemVO orderItem2VO(OrderItem orderItem){
        OrderItemVO orderItemVO = new OrderItemVO();
        BeanUtils.copyProperties(orderItem,orderItemVO);

        orderItemVO.setCreateTime(DateTimeUtil.date2Str(orderItem.getCreateTime()));
        return orderItemVO;
    }

    private List<OrderVO> OrderList2VOList(List<Order> orderList,Integer userId){
        List<OrderVO> orderVoList = new ArrayList<>();
        for(Order order : orderList){
            List<OrderItem>  orderItemList;
            if(userId == null){
                //管理员查询的时候 不需要传userId
                orderItemList = orderItemMapper.getAllByOrderNo(order.getOrderNo());
            }else{
                orderItemList = orderItemMapper.getAllByOrderNoAndUserId(order.getOrderNo(),userId);
            }
            OrderVO orderVo = assembleOrderVO(order,orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    /**
     * 减库存操作
     */
    private void reduceProductStock(List<OrderItem> orderItemList){
        //获取购物车中所有产品的信息,先将数据一次性从数据库中拿出来，而不是每次拿一个，否则效率很低
        List<Product> productList = productMapper.getAllByIds(orderItemList.stream().map(OrderItem::getProductId).
                collect(Collectors.toList()));
        Map<Integer,Product> productMap = productList.stream().collect(Collectors.toMap(Product::getId,e->e));
        productList.clear();
        for(OrderItem orderItem : orderItemList){
            Product product = productMap.get(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productList.add(product);
        }
        productMapper.updateStockByIds(productList);
    }

    /**
     * 清空购物车中已选商品
     */
    private void cleanCart(List<Cart> cartList){
        List<Integer> cartIds = cartList.stream().map(Cart::getId).collect(Collectors.toList());
        cartMapper.delAllByIds(cartIds);
    }

    @Override
    public OrderVO create(Integer userId, Integer shippingId) {
        //从购物车中获取数据
        List<Cart> cartList = cartMapper.getAllCheckedByUserId(userId);

        //通过购物车信息获取商品详细信息
        List<OrderItem> orderItemList = cartList2orderItemList(userId,cartList);

        //计算订单总价
        BigDecimal payment = new BigDecimal("0");
        for(OrderItem orderItem : orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }

        //生成订单
        Order order = this.assembleOrder(userId,shippingId,payment);

        //给orderItem列表添加订单号信息
        for(OrderItem orderItem : orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }

        //mybatis 批量插入
        orderItemMapper.insertAll(orderItemList);

        //生成成功,我们要减少我们产品的库存
        reduceProductStock(orderItemList);

        //清空购物车已购买商品
        cleanCart(cartList);

        //返回给前端数据
        return assembleOrderVO(order,orderItemList);
    }

    @Override
    public void cancel(Integer userId, Long orderNo) {
        Order order  = orderMapper.getOneByOrderNoAndUserId(orderNo,userId);
        if(order == null){
            log.error("[取消订单]取消失败，该订单不存在，orderNo={},userId={}",orderNo,userId);
            throw new SellException(ResultEnum.ERROR.getCode(),"该订单不存在");
        }
        //TODO 一期没有支付宝退款接口，所以只支持取消未付款订单
        if(order.getStatus() != OrderStatusEnum.NO_PAY.getCode()){
            log.error("[取消订单]取消失败，订单已付款,无法取消，orderNo={}",orderNo);
            throw new SellException(ResultEnum.ERROR.getCode(),"订单已付款,无法取消");
        }

        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(OrderStatusEnum.CANCELED.getCode());

        if (orderMapper.updateByOrderId(updateOrder) < 1){
            log.error("[取消订单]取消失败，服务器更新失败，orderNo={}",orderNo);
            throw new SellException(ResultEnum.ERROR.getCode(),"服务器更新失败");
        }
    }

    @Override
    public OrderProductVO getOrderCartProduct(Integer userId) {
        OrderProductVO orderProductVO = new OrderProductVO();

        //从购物车中获取数据
        List<Cart> cartList = cartMapper.getAllCheckedByUserId(userId);
        List<OrderItem> orderItemList =  cartList2orderItemList(userId,cartList);

        List<OrderItemVO> orderItemVoList = new ArrayList<>();

        BigDecimal payment = new BigDecimal("0");
        for(OrderItem orderItem : orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(orderItem2VO(orderItem));
        }
        orderProductVO.setProductTotalPrice(payment);
        orderProductVO.setOrderItemVOList(orderItemVoList);
        orderProductVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return orderProductVO;
    }

    @Override
    public OrderVO getOrderDetail(Integer userId, Long orderNo) {
        Order order = orderMapper.getOneByOrderNoAndUserId(orderNo,userId);
        if(order == null){
            log.error("[查询订单]查询失败，该订单不存在，orderNo={},userId={}",orderNo,userId);
            throw new SellException(ResultEnum.ERROR.getCode(),"该订单不存在");
        }
        List<OrderItem> orderItemList = orderItemMapper.getAllByOrderNoAndUserId(orderNo,userId);
        return assembleOrderVO(order,orderItemList);
    }

    @Override
    public PageInfo getOrderList(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.getAllByUserId(userId);
        List<OrderVO> orderVoList = OrderList2VOList(orderList,userId);
        return new PageInfo<>(orderVoList);
    }




    @Override
    public PageInfo manageList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.getAll();
        List<OrderVO> orderVOList = OrderList2VOList(orderList,null);
        return new PageInfo<>(orderVOList);
    }

    @Override
    public OrderVO manageDetail(Long orderNo) {
        Order order = orderMapper.getOneByOrderNo(orderNo);
        List<OrderItem> orderItemList = orderItemMapper.getAllByOrderNo(orderNo);
        if (order == null || orderItemList == null){
            log.error("[查询订单详情]查询失败，订单信息为空，order = {}， orderItemList = {}",order,orderItemList);
            throw new SellException(ResultEnum.ERROR.getCode(),"订单信息为空");
        }
        return assembleOrderVO(order,orderItemList);
    }

    @Override
    public PageInfo manageSearch(Long orderNo, int pageNum, int pageSize) {
        //todo 一期精准查询 orderNo ，后期会改为模糊查询多个参数，如： orderNo、Name 等
        PageHelper.startPage(pageNum,pageSize);
        Order order = orderMapper.getOneByOrderNo(orderNo);
        List<OrderItem> orderItemList = orderItemMapper.getAllByOrderNo(orderNo);
        if (order == null || orderItemList == null){
            log.error("[查询订单]查询失败，订单信息为空，order = {}， orderItemList = {}",order,orderItemList);
            throw new SellException(ResultEnum.ERROR.getCode(),"订单信息为空");
        }
        return new PageInfo<>(Lists.newArrayList(assembleOrderVO(order,orderItemList)));
    }

    @Override
    public void manageSendGoods(Long orderNo) {
        Order order = orderMapper.getOneByOrderNo(orderNo);
        if (order == null){
            log.error("[订单发货]发货失败，订单信息为空，order = null");
            throw new SellException(ResultEnum.ERROR.getCode(),"订单信息为空");
        }
        if (!order.getStatus().equals(OrderStatusEnum.PAID.getCode())){
            log.error("[订单发货]发货失败，订单状态有误，orderStatus = {}", order.getStatus());
            throw new SellException(ResultEnum.ERROR.getCode(),"订单状态有误");
        }
        order.setStatus(OrderStatusEnum.SHIPPED.getCode());
        order.setSendTime(new Date());
        orderMapper.updateByOrderId(order);
    }




    @Override
    public Map<String, String> pay(Long orderNo, Integer userId, String path) {
        Map<String,String> result = new HashMap<>();
        Order order = orderMapper.getOneByOrderNoAndUserId(orderNo,userId);
        if (order == null){
            log.error("[支付订单]支付失败，该订单不存在，orderNo={},userId={}",orderNo,userId);
            throw new SellException(ResultEnum.ERROR.getCode(),"订单不存在");
        }
        result.put("orderNo", orderNo.toString());

        //初始化支付宝接入SDK
        //读取配置文件
        AlipayConfig.init(PropertiesUtil.getProperty("zfbinfo.url"));
        //初始化,    注意：这里是 alipayPublicKey 不是publicKey
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.openApiDomain, AlipayConfig.appid, AlipayConfig.privateKey,
                AlipayConfig.format, AlipayConfig.charset, AlipayConfig.alipayPublicKey, AlipayConfig.signType);
        //创建API对应的request类
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest ();
        //创建API对应的 response 类
        AlipayTradePrecreateResponse response;
        //创建API对应的数据类
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();

        // 接入支付宝，生成支付二维码
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = orderNo.toString();
        model.setOutTradeNo(outTradeNo);
        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        model.setSubject("happymmall扫码支付，订单号：".concat(outTradeNo));
        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();
        model.setTotalAmount(totalAmount);
        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        model.setUndiscountableAmount("0");
        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        model.setBody("订单".concat(outTradeNo).concat("购买商品共花费：").concat(totalAmount).concat("元"));
        // 支付超时时间，取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点
        model.setQrCodeTimeoutExpress("120m");

        // 商品明细列表，订单包含的商品列表信息，这四项必填，其余可选
        List<OrderItem> orderItemList = orderItemMapper.getAllByOrderNoAndUserId(orderNo,userId);
        List<GoodsDetail> goodsDetails = new ArrayList<>();
        for (OrderItem orderItem : orderItemList){
            GoodsDetail goodsDetail = new GoodsDetail();
            goodsDetail.setGoodsId(orderItem.getProductId().toString());
            goodsDetail.setGoodsName(orderItem.getProductName());
            goodsDetail.setPrice(orderItem.getCurrentUnitPrice().toString());
            goodsDetail.setQuantity(orderItem.getQuantity().longValue());
            //添加进列表
            goodsDetails.add(goodsDetail);
        }
        model.setGoodsDetail(goodsDetails);
        //设置请求数据
        request.setBizModel(model);
        //设置支付宝回调url
        request.setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"));
        try {
            //发送请求
            response = alipayClient.execute(request);
            if(!response.isSuccess()){
               log.error("[支付订单]支付失败，订单信息有误,拒绝生成订单");
               throw new SellException(ResultEnum.ERROR.getCode(),"支付失败");
            }
        } catch (AlipayApiException e) {
            log.error("[支付订单]支付失败，订单信息有误,拒绝生成订单",e);
            throw new SellException(ResultEnum.ERROR.getCode(),"支付失败");
        }
        File folder = new File(path);
        if (!folder.exists()){
            //设置为可写 并 创建目录
            if (!(folder.setWritable(true) && folder.mkdirs())){
                log.error("【支付】创建临时目录失败");
                throw new SellException(ResultEnum.ERROR.getCode(),"创建临时目录失败");
            }
        }
        //二维码保存路径
        String qrPath = String.format(path.concat("/qr-%s.png"),response.getOutTradeNo());
        // 将二维码图片持久化到所指定的路径下
        ZxingUtil.getQRCodeImge(response.getQrCode(),256,qrPath);
        File targetFile = new File(qrPath);
        try {
            if ( !FTPUtil.uploadFile(targetFile)){
                log.error("[上传文件]上传二维码异常，ftp服务器连接失败");
                throw new SellException(ResultEnum.ERROR.getCode(),"上传二维码异常");
            }
            //删除工作目录的文件
            if (!targetFile.delete()){
                log.error("【支付】删除临时文件失败");
                throw new SellException(ResultEnum.ERROR.getCode(),"删除临时文件失败");
            }
        } catch (IOException e) {
            log.error("[上传文件]上传二维码异常，e={}",e);
            throw new SellException(ResultEnum.ERROR.getCode(),"上传二维码异常");
        }
        String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile.getName();
        result.put("qrUrl",qrUrl);
        return result;
    }

    @Override
    public Boolean checkAliCallBack(Map<String, String> params) {
        //读取配置文件
        AlipayConfig.init(PropertiesUtil.getProperty("zfbinfo.url"));
        Long orderNo = Long.valueOf(params.get("out_trade_no"));
        String tradeStatus = params.get("trade_status");
        Order order = orderMapper.getOneByOrderNo(orderNo);

        //参数校验
        if (!(params.get("seller_id").equals(AlipayConfig.sellerId) && order != null &&
                new BigDecimal(params.get("total_amount")).equals(order.getPayment()) )){
            log.error("[支付宝回调]参数校验失败，回调参数有误,params={}",params);
            return false;
        }

        //订单状态校验
        if (order.getStatus() >= OrderStatusEnum.PAID.getCode()){
            //如果订单已付款，说明重复回调了。赶紧返回一个true，告诉服务器别回调了
            log.error("[支付宝回调]重复回调，订单已付款，orderStatus={}",order.getStatus());
            return true;
        }

        //校验支付状态
        if (!tradeStatus.equals(Const.AliCallBack.TRADE_STATUS_TRADE_SUCCESS)){
            log.error("[支付宝回调]支付状态有误,tradeStatus={}",tradeStatus);
            return false;
        }

        //更新订单状态
        order.setStatus(OrderStatusEnum.PAID.getCode());
        order.setCreateTime(DateTimeUtil.str2Date(params.get("gmt_create")));
        orderMapper.updateByOrderId(order);

        //添加交易信息
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(orderNo);
        //1-支付宝,2-微信
        payInfo.setPayPlatform(1);
        payInfo.setPlatformNumber(params.get("trade_no"));
        payInfo.setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);

        return true;
    }

    @Override
    public Boolean queryOrderPayStatus(Integer userId, Long orderNo) {
        Order order = orderMapper.getOneByOrderNoAndUserId(orderNo,userId);
        if (order == null){
            log.error("[查询订单支付状态]查询失败，该订单不存在，orderNo={},userId={}",orderNo,userId);
            throw new SellException(ResultEnum.ERROR.getCode(),"订单不存在");
        }
        return order.getStatus() >= OrderStatusEnum.PAID.getCode();
    }



}
