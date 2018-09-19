package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.ShippingService;
import com.mmall.utils.ResultUtil;
import com.mmall.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 收获地址有关接口
 * Created By Cx On 2018/9/9 9:48
 */
@RestController
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    ShippingService shippingService;

    @GetMapping("/list")
    public ResultVO getAll(@RequestParam(defaultValue = "1") int pageNum,@RequestParam(defaultValue = "10") int pageSize,
                           HttpSession httpSession){
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        return ResultUtil.success(shippingService.getAllByUserId(user.getId(),pageNum,pageSize));
    }

    @GetMapping("/one/{shippingId}")
    public ResultVO getOneByShippingId(@PathVariable Integer shippingId,HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return ResultUtil.success(shippingService.getOneByUserIdAndShippingId(user.getId(),shippingId));
    }

    /**
     * 添加用户收货地址，并返回新增收获地址的 shippingId
     */
    @PostMapping("/add")
    public ResultVO add(@RequestBody Shipping shipping , HttpSession session){
        //将shipping的userId加上,从session获取，防止前端数据篡改
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        shipping.setUserId(user.getId());
        Map<String,Object> data = new HashMap<>();
        data.put("shippingId",shippingService.insert(shipping).getId());
        return ResultUtil.success(data);
    }

    @PostMapping("/update")
    public ResultVO update(@RequestBody Shipping shipping , HttpSession session){
        //将shipping的userId加上,从session获取，防止前端数据篡改
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        shipping.setUserId(user.getId());
        shippingService.updateByUserIdAndShippingId(shipping);
        return ResultUtil.success();
    }

    @DeleteMapping("/one/{shippingId}")
    public ResultVO del(@PathVariable Integer shippingId,HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        shippingService.deleteByUserIdAndShippingId(user.getId(),shippingId);
        return ResultUtil.success();
    }

}
