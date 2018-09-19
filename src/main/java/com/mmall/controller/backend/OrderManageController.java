package com.mmall.controller.backend;

import com.mmall.service.OrderService;
import com.mmall.utils.ResultUtil;
import com.mmall.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created By Cx On 2018/9/19 15:23
 */
@RestController
@RequestMapping("/order/manage")
public class OrderManageController {

    @Autowired
    private OrderService orderService;

    /**
     * 分页返回所有订单列表
     */
    @GetMapping("/list")
    public ResultVO orderList(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                              @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        return ResultUtil.success(orderService.manageList(pageNum,pageSize));
    }

    /**
     * 获取某个订单的详情
     */
    @GetMapping("/detail")
    public ResultVO orderDetail( Long orderNo){
        return ResultUtil.success(orderService.manageDetail(orderNo));
    }


    /**
     * 分页返回符合查询条件的订单列表
     * todo  一期精准查询 orderNo ，后期会改为模糊查询 orderNo、Name等
     */
    @GetMapping("/search")
    public ResultVO orderSearch(Long orderNo,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                                @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        return ResultUtil.success(orderService.manageSearch(orderNo,pageNum,pageSize));
    }

    /**
     * 订单发货，修改订单状态为发货
     */
    @PostMapping("/send_goods")
    public ResultVO orderSendGoods( Long orderNo){
        orderService.manageSendGoods(orderNo);
        return ResultUtil.success();
    }

}
