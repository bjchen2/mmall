package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.dao.ShippingMapper;
import com.mmall.dao.UserMapper;
import com.mmall.enums.ResultEnum;
import com.mmall.exception.SellException;
import com.mmall.pojo.Shipping;
import com.mmall.service.ShippingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created By Cx On 2018/9/9 9:52
 */
@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    ShippingMapper shippingMapper;
    @Autowired
    UserMapper userMapper;

    @Override
    public PageInfo getAllByUserId(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(shippingMapper.getAllByUserId(userId));
    }

    @Override
    public Shipping insert(Shipping shipping) {
        if ( !userMapper.isExistByUserId(shipping.getUserId())){
            log.error("[添加用户收货地址】添加失败，登录信息不正确，userId不存在");
            throw new SellException(ResultEnum.ERROR.getCode(),"登录信息不正确,请重新登录");
        }
        shippingMapper.insert(shipping);
        return shipping;
    }

    @Override
    public void updateByUserIdAndShippingId(Shipping shipping) {
        if ( !userMapper.isExistByUserId(shipping.getUserId())){
            log.error("[添加用户收货地址】添加失败，登录信息不正确，userId不存在");
            throw new SellException(ResultEnum.ERROR.getCode(),"登录信息不正确,请重新登录");
        }
        shippingMapper.updateByUserIdAndShippingId(shipping);
    }

    @Override
    public void deleteByUserIdAndShippingId(Integer userId, Integer shippingId) {
        if ( !userMapper.isExistByUserId(userId)){
            log.error("[添加用户收货地址】添加失败，登录信息不正确，userId不存在");
            throw new SellException(ResultEnum.ERROR.getCode(),"登录信息不正确,请重新登录");
        }
        shippingMapper.deleteByUserIdAndShippingId(userId,shippingId);
    }

    @Override
    public Shipping getOneByUserIdAndShippingId(Integer userId, Integer shippingId) {
        if ( !userMapper.isExistByUserId(userId)){
            log.error("[添加用户收货地址】添加失败，登录信息不正确，userId不存在");
            throw new SellException(ResultEnum.ERROR.getCode(),"登录信息不正确,请重新登录");
        }
        return shippingMapper.getOneByUserIdAndShippingId(userId,shippingId);
    }
}
