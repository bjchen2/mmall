package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.pojo.User;
import com.mmall.service.CartService;
import com.mmall.utils.ResultUtil;
import com.mmall.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * Created By Cx On 2018/9/5 15:49
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    /**
     * 获取某用户的购物车信息
     */
    @GetMapping("/list")
    public ResultVO list(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return ResultUtil.success(cartService.list(user.getId()));
    }

    /**
     * 添加商品进购物车
     * data 包含 productId(int),quantity(int)：数量
     */
    @PostMapping("/add")
    public ResultVO add(@RequestBody Map<String,Object> data , HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return ResultUtil.success(cartService.add(user.getId(),(Integer) data.get("productId"),(Integer) data.get("quantity")));
    }

    /**
     * 更新购物车某商品数量
     * data 包含 productId(int),quantity(int)：数量
     */
    @PostMapping("/update_quantity")
    public ResultVO updateQuantity (@RequestBody Map<String,Object> data , HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return ResultUtil.success(cartService.updateQuantity(user.getId(),(Integer) data.get("productId"),(Integer) data.get("quantity")));
    }

    /**
     * 删除该用户的某些商品
     */
    @DeleteMapping("/delete")
    public ResultVO delList(@RequestBody List<Integer> productIds, HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return ResultUtil.success(cartService.delAllByUserId(user.getId(),productIds));
    }

    /**
     * 全选
     */
    @PostMapping("/checked_all")
    public ResultVO checkedAll(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return ResultUtil.success(cartService.updateAllCheckedByUserId(user.getId(),Const.Cart.CHECKED));
    }

    /**
     * 全不选
     */
    @PostMapping("/un_checked_all")
    public ResultVO uncheckedAll(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return ResultUtil.success(cartService.updateAllCheckedByUserId(user.getId(),Const.Cart.UN_CHECKED));
    }

    /**
     * 选择一个
     */
    @PostMapping("/checked_one")
    public ResultVO checkedOne(@RequestBody Integer productId, HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return ResultUtil.success(cartService.updateOneCheckedByUserIdAndProductId(user.getId(),productId,Const.Cart.CHECKED));
    }

    /**
     * 不选一个
     */
    @PostMapping("/un_checked_one")
    public ResultVO uncheckedOne(@RequestBody Integer productId, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return ResultUtil.success(cartService.updateOneCheckedByUserIdAndProductId(user.getId(), productId, Const.Cart.UN_CHECKED));
    }

    /**
     * 获取购物车中的商品数量
     */
    @GetMapping("/cart_product_count")
    public ResultVO getCartProductCount(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return ResultUtil.success(cartService.getCartProductCount(user.getId()));
    }
}
