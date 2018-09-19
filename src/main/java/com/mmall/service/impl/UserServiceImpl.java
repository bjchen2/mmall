package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.enums.ResultEnum;
import com.mmall.exception.SellException;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import com.mmall.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created By Cx On 2018/8/25 0:03
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        if ( !checkExist(Const.USERNAME,username)){
            log.error("[用户登录]登录失败，用户名不存在,username={}",username);
            throw new SellException(ResultEnum.ERROR.getCode(),"用户名不存在");
        }
        //MD5转换
        password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,password);
        if (user == null){
            log.error("[用户登录]登录失败，用户名或密码错误,username={}",username);
            throw new SellException(ResultEnum.ERROR.getCode(),"用户名或密码错误");
        }
        return user;
    }

    @Override
    public void register(User user) {
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        user.setRole(Const.Role.ROLE_CUSTOMER);
        if (checkExist(Const.USERNAME,user.getUsername())){
            log.error("[用户注册]用户注册失败，用户名已存在,username={}",user.getUsername());
            throw new SellException(ResultEnum.ERROR.getCode(),"用户名已存在");
        }
        else if (checkExist(Const.EMAIL,user.getEmail())){
            log.error("[用户注册]用户注册失败，邮箱已被使用,email={}",user.getEmail());
            throw new SellException(ResultEnum.ERROR.getCode(),"邮箱已被使用");
        }
        else if (userMapper.insert(user) < 1){
            log.error("[用户注册]用户注册失败，服务器异常");
            throw new SellException(ResultEnum.ILLEGAL_ARGUMENT);
        }
    }

    @Override
    public Boolean checkExist(String type, String value) {
        if (Const.EMAIL.equals(type)){
            //验证email是否存在
            return userMapper.checkEmail(value) > 0;
        }
        else if (Const.USERNAME.equals(type)){
            return userMapper.checkUsername(value) > 0;
        }
        else {
            log.error("[参数校验]请求校验参数错误，type={}",type);
            throw new SellException(ResultEnum.ERROR.getCode(),"参数错误");
        }
    }

    @Override
    public String getQuestionByUsername(String username) {
        if ( !checkExist(Const.USERNAME,username)){
            log.error("[获取验证问题]获取失败，用户名不存在，username={}",username);
            throw new SellException(ResultEnum.ERROR.getCode(),"用户名不存在");
        }
        return userMapper.getQuestionByUsername(username);
    }

    @Override
    public String checkAnswer(String username, String question, String answer) {
        if (userMapper.checkAnswer(username,question,answer) < 1){
            log.error("[验证问题校验]校验失败，答案错误");
            throw new SellException(ResultEnum.ERROR.getCode(),"答案错误");
        }
        String token = UUID.randomUUID().toString();
        TokenCache.setValue(Const.TOKEN_PREFIX.concat(username),token);
        return token;
    }

    @Override
    public void resetPasswordByToken(String username, String newPassword, String token) {
        if (StringUtils.isBlank(token)){
            log.error("[更新密码]更新失败，token为空，token={}",token);
            throw new SellException(ResultEnum.ERROR.getCode(),"token为空");
        }
        if (!token.equals(TokenCache.getKey(Const.TOKEN_PREFIX.concat(username)))){
            log.error("[更新密码]更新失败，token错误，token={}",token);
            throw new SellException(ResultEnum.ERROR.getCode(),"token错误");
        }
        newPassword = MD5Util.MD5EncodeUtf8(newPassword);
        if (userMapper.resetPasswordByToken(username,newPassword) < 1){
            log.error("[更新密码]更新失败，该用户不存在，username={}",username);
            throw new SellException(ResultEnum.ERROR.getCode(),"用户不存在");
        }
    }

    @Override
    public void resetPasswordByOldPass(User user, String newPassword) {
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        newPassword = MD5Util.MD5EncodeUtf8(newPassword);
        if (userMapper.resetPasswordByOldPass(user,newPassword) < 1){
            log.error("[更新密码]更新失败，旧密码错误，username={}",user.getUsername());
            throw new SellException(ResultEnum.ERROR.getCode(),"旧密码错误");
        }
    }

    @Override
    public void updateInfo(User user) {
        if (user.getEmail() != null && userMapper.checkEmailById(user.getId(),user.getEmail()) < 1){
            log.error("[更新用户信息]更新失败，邮箱已存在，email={}",user.getEmail());
            throw new SellException(ResultEnum.ERROR.getCode(),"邮箱已存在");
        }
        if (userMapper.updateUserInfo(user) < 0){
            log.error("[更新用户信息]更新失败，服务器连接失败，userId={}",user.getId());
            throw new SellException(ResultEnum.ERROR.getCode(),"服务器连接失败");
        }
    }

    @Override
    public Boolean checkAdminRole(int userId) {
        User user = userMapper.getById(userId);
        return user.getRole().equals(Const.Role.ROLE_ADMIN);
    }


}
