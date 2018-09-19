package com.mmall.service;

import com.mmall.pojo.User;

/**
 * 用户有关接口
 * Created By Cx On 2018/8/24 23:56
 */
public interface UserService {

    /**
     * 用户登录验证
     */
    User login(String username,String password);

    /**
     * 用户注册
     */
    void register(User user);

    /**
     * 检查type的value是否存在
     */
    Boolean checkExist(String type, String value);

    /**
     * 通过用户名获取用户验证问题
     */
    String getQuestionByUsername(String username);

    /**
     * 校验用户的密保问题和答案是否正确
     */
    String checkAnswer(String username, String question, String answer);

    /**
     * 忘记密码时，修改密码
     * 传进来的密码是未经MD5加密的
     */
    void resetPasswordByToken(String username, String newPassword, String token);

    /**
     * 已登录时，修改密码
     * 传进来的新密码和旧密码都未经MD5加密
     */
    void resetPasswordByOldPass(User user,String newPassword);

    /**
     * 更新用户信息
     */
    void updateInfo(User user);


    /**
     * 检验user是否为管理员
     */
    Boolean checkAdminRole(int userId);
}
