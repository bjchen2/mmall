package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.mmall.common.Const;
import com.mmall.config.AlipayConfig;
import com.mmall.exception.SellException;
import com.mmall.pojo.User;
import com.mmall.service.OrderService;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.ResultUtil;
import com.mmall.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By Cx On 2018/9/11 20:14
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    OrderService orderService;


    /**
     * shippingId 为收获信息Id
     */
    @PostMapping("/create")
    public ResultVO create (Integer shippingId, HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        return ResultUtil.success(orderService.create(user.getId(),shippingId));
    }

    /**
     * 取消订单 （仅限于未付款订单，因为支付宝退款接口还未写）
     */
    @PostMapping("/cancel")
    public ResultVO cancel(Long orderNo, HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        orderService.cancel(user.getId(),orderNo);
        return ResultUtil.success();
    }

    /**
     * 获取用户购物车中已选商品
     */
    @GetMapping("/get_checked_product")
    public ResultVO getCheckedProduct(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        return ResultUtil.success(orderService.getOrderCartProduct(user.getId()));
    }

    /**
     * 查看订单详情
     */
    @GetMapping("/detail")
    public ResultVO detail(HttpSession session,Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        return ResultUtil.success(orderService.getOrderDetail(user.getId(),orderNo));
    }

    /**
     * 查看订单列表
     */
    @GetMapping("/list")
    public ResultVO list(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                         @RequestParam(value = "pageSize",defaultValue = "10") int pageSize, HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        return ResultUtil.success(orderService.getOrderList(user.getId(),pageNum,pageSize));
    }

    /**
     * 当面付之扫码支付操作
     * orderNo 为订单号
     */
    @PostMapping("/pay/{orderNo}")
    public ResultVO pay(@PathVariable Long orderNo, HttpSession session, HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        String path = request.getSession().getServletContext().getRealPath("upload");
        return ResultUtil.success(orderService.pay(orderNo,user.getId(),path));
    }

    /**
     * 支付宝回调函数处理
     * 如果正确，修改订单状态，并将支付信息持久化到数据库
     * https://docs.open.alipay.com/194/103296/
     * 返回request中含有以下数据：
     *  {
     *   "gmt_create": "2018-09-17 23:06:11",
     *   "charset": "UTF-8",
     *   "seller_email": "sortns2356@sandbox.com",
     *   "subject": "happymmall扫码支付，订单号：1492091102371",
     *   "body": "订单1492091102371购买商品共花费：3299.00元",
     *   "buyer_id": "2088102176650673",
     *   "invoice_amount": "3299.00",
     *   "notify_id": "61756d335a2132ad4f98592da3f4b0dl69",
     *   "fund_bill_list": "[{\"amount\":\"3299.00\",\"fundChannel\":\"ALIPAYACCOUNT\"}]",
     *   "notify_type": "trade_status_sync",
     *   "trade_status": "TRADE_SUCCESS",
     *   "receipt_amount": "3299.00",
     *   "app_id": "2016091700531640",
     *   "buyer_pay_amount": "3299.00",
     *   "seller_id": "2088102176023819",
     *   "gmt_payment": "2018-09-17 23:06:18",
     *   "notify_time": "2018-09-17 23:06:19",
     *   "version": "1.0",
     *   "out_trade_no": "1492091102371",
     *   "total_amount": "3299.00",
     *   "trade_no": "2018091721001004670500293787",
     *   "auth_app_id": "2016091700531640",
     *   "buyer_logon_id": "syn***@sandbox.com",
     *   "point_amount": "0.00"
     *   "sign_type": **************
     *   "sign": ***********
     * }
     */
    @PostMapping("/alipay_callback")
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> params = new HashMap<>();

        //将请求参数处理后放进 params 中
        request.getParameterMap().forEach((key,value)->{
            String result = "";
            for (int i = 0 ; i < value.length; i++){
               result = i == value.length-1 ? result.concat(value[i]) : result.concat(value[i]).concat(",");
            }
            params.put(key,result);
        });
        System.out.println(JsonUtil.toJson(params));
        //验证回调是否为支付宝发的，并且避免重复回调
        //第一步： 在通知返回参数列表中，除去sign、sign_type两个参数外，凡是通知返回回来的参数皆是待验签的参数
        // 但 sign 参数的除去，在支付宝SDK中已经帮忙代做了
        params.remove("sign_type");
        try {
            //第二步： 将剩下参数进行url_decode, 然后进行字典排序，组成字符串，得到待签名字符串：
            //第三步： 将签名参数（sign）使用base64解码为字节码串。
            //第四步： 使用RSA的验签方法，通过签名字符串、签名参数（经过base64解码）及支付宝公钥验证签名。
            //调用支付宝SDK验签，注意第二个参数是 alipayPublicKey 不是 publicKey
            boolean checkResult = AlipaySignature.rsaCheckV2(params, AlipayConfig.alipayPublicKey,AlipayConfig.charset,AlipayConfig.signType);
            if (!checkResult){
                //如果验签失败
                log.error("非法请求，验签失败");
                return ResultUtil.error("非法请求，验签失败");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝回调异常",e);
            return ResultUtil.error("支付宝回调异常");
        }

        //第五步：需要严格校验通知数据的正确性。
       if ( ! orderService.checkAliCallBack(params)) {
           return Const.AliCallBack.RESPONSE_FAILED;
       }
        return Const.AliCallBack.RESPONSE_SUCCESS;
    }

    /**
     * 通过订单号，查询该用户的订单是否已付款
     * 若已支付返回 true
     */
    @GetMapping("/query_order_pay_status")
    public ResultVO queryOrderPayStatus(Long orderNo, HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        return ResultUtil.success(orderService.queryOrderPayStatus(user.getId(),orderNo));
    }

}
