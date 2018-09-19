package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.dao.UserMapper;
import com.mmall.enums.ResultEnum;
import com.mmall.exception.SellException;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.CartService;
import com.mmall.utils.BigDecimalUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.CartProductVO;
import com.mmall.vo.CartVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By Cx On 2018/9/5 15:50
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class CartServiceImpl implements CartService {

    @Autowired
    CartMapper cartMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ProductMapper productMapper;

    private CartProductVO cart2CartProductVO(Cart cart){
        Product product = productMapper.getById(cart.getProductId());
        if (product == null){
            log.error("[获取购物车信息]获取失败，商品不存在,cartId={}，productId={}",cart.getId(),cart.getProductId());
            return null;
        }
        //如果商品存在
        CartProductVO cartProductVO = new CartProductVO();
        //添加购物车信息
        cartProductVO.setCartId(cart.getId());
        cartProductVO.setUserId(cart.getUserId());
        cartProductVO.setProductId(cart.getProductId());
        cartProductVO.setProductChecked(cart.getChecked());
        //添加商品信息
        cartProductVO.setProductStatus(product.getStatus());
        cartProductVO.setProductMainImage(product.getMainImage());
        cartProductVO.setProductPrice(product.getPrice());
        cartProductVO.setProductSubtitle(product.getSubtitle());
        cartProductVO.setProductName(product.getName());
        cartProductVO.setProductStock(product.getStock());
        if (product.getStock() < cart.getQuantity()){
            //如果库存少于订单需要数量，强行将其改为库存,并更新数据库信息
            cartProductVO.setQuantity(product.getStock());
            cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
            //更新购物车信息
            Cart cartForQuantity = new Cart();
            cartForQuantity.setId(cart.getId());
            cartForQuantity.setQuantity(product.getStock());
            cartMapper.updateQuantityAndCheckedById(cart);
        }else {
            cartProductVO.setQuantity(cart.getQuantity());
            cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
        }
        //计算总价
        BigDecimal totalPrice = BigDecimalUtil.mul(cartProductVO.getQuantity().doubleValue(),
                cartProductVO.getProductPrice().doubleValue());
        cartProductVO.setProductTotalPrice(totalPrice);
        return cartProductVO;
    }

    private List<CartProductVO> cart2CartProductVO(List<Cart> carts){
        List<CartProductVO> cartProductVOs = new ArrayList<>();
        for (Cart cart : carts){
            CartProductVO cartProductVO = cart2CartProductVO(cart);
            if (cartProductVO != null){
                //如果信息无误，放入购物车列表
                cartProductVOs.add(cartProductVO);
            }
        }
        return cartProductVOs;
    }

    private CartVO cartList2CartVO(List<Cart> carts){
        CartVO cartVO = new CartVO();
        BigDecimal totalPrice = new BigDecimal("0");
        List<CartProductVO> cartProductVOs = cart2CartProductVO(carts);
        cartVO.setCartProductVoList(cartProductVOs);
        for (CartProductVO cartProductVO : cartProductVOs){
            //将已勾选商品价格加上，形成总价
            if (cartProductVO.getProductChecked().equals(Const.Cart.CHECKED)){
                totalPrice = BigDecimalUtil.add(totalPrice.doubleValue(), cartProductVO.getProductTotalPrice().doubleValue());
            }
        }
        cartVO.setCartTotalPrice(totalPrice);
        cartVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        if (!CollectionUtils.isEmpty(carts)){
            //如果非空，才能取首个元素，否则会导致数组越界
            cartVO.setAllChecked(cartMapper.isAllCheckedByUserId(carts.get(0).getUserId()));
        }
        return cartVO;
    }

    @Override
    public CartVO add(Integer userId, Integer productId, Integer quantity) {
        if (userMapper.getById(userId) == null){
            log.error("[添加购物车信息]添加失败，用户不存在,userId={}",userId);
            throw new SellException(ResultEnum.ERROR.getCode(),"用户不存在");
        }
        if (productMapper.getById(productId) == null){
            log.error("[添加购物车信息]添加失败，商品不存在,productId={}",productId);
            throw new SellException(ResultEnum.ERROR.getCode(),"商品不存在");
        }
        if (quantity == null){
            log.error("[添加购物车信息]添加失败，添加数量为空,quantity=null");
            throw new SellException(ResultEnum.ERROR.getCode(),"添加数量为空");
        }
        Cart cart = cartMapper.getOneByUserIdAndProductId(userId,productId);
        if (cart != null){
            //该商品已被添加进购物车
            quantity += cart.getQuantity();
            cart.setQuantity(quantity);
            cartMapper.updateQuantityAndCheckedById(cart);
        }else {
            //未添加过该商品
            cart = new Cart();
            cart.setQuantity(quantity);
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setChecked(Const.Cart.CHECKED);
            cartMapper.insert(cart);
        }
        return list(userId);
    }

    @Override
    public CartVO list(Integer userId) {
        List<Cart> carts = cartMapper.getAllByUserId(userId);
        return cartList2CartVO(carts);
    }

    @Override
    public CartVO updateQuantity(Integer userId, Integer productId, Integer quantity) {
        Cart cart = cartMapper.getOneByUserIdAndProductId(userId,productId);
        if (cart == null){
            log.error("【更新购物车信息】更新失败，该条信息不存在，userId={},productId={}",userId,productId);
            throw new SellException(ResultEnum.ERROR.getCode(),"参数有误，该信息不存在");
        }
        cart.setQuantity(quantity);
        if (cartMapper.updateQuantityAndCheckedById(cart) < 1){
            log.error("【更新购物车信息】更新失败，服务器连接失败");
            throw new SellException(ResultEnum.ERROR.getCode(),"服务器连接失败");
        }
        return list(userId);
    }

    @Override
    public CartVO delAllByUserId(Integer userId, List<Integer> productIds) {
        if (CollectionUtils.isEmpty(productIds)){
            log.error("[删除购物车商品]删除失败,商品ID列表为空");
            throw new SellException(ResultEnum.ERROR.getCode(),"商品ID列表为空");
        }
        cartMapper.delAllByUserId(userId,productIds);
        return list(userId);
    }

    @Override
    public CartVO updateAllCheckedByUserId(Integer userId, Integer checked) {
        cartMapper.updateAllCheckedByUserId(userId,checked);
        return list(userId);
    }

    @Override
    public CartVO updateOneCheckedByUserIdAndProductId(Integer userId, Integer productId, Integer checked) {
        cartMapper.updateOneCheckedByUserIdAndProductId(userId,productId,checked);
        return list(userId);
    }

    @Override
    public Integer getCartProductCount(Integer userId) {
        return cartMapper.getCartProductCount(userId);
    }


}
