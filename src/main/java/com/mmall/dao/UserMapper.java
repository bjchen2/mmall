package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 通过id获取用户信息
     */
    User getById(int id);

    //查询用户名是否存在，若存在，返回1，否则返回0
    int checkUsername(String username);

    //用于注册时查询邮箱是否存在，若存在，返回1，否则返回0
    int checkEmail(String email);

    //用于更新用户信息时 检验修改email是否存在
    int checkEmailById(@Param("userId")Integer userId,@Param("email")String email);

    //查询账号密码符合的用户
    User selectLogin(@Param("username")String username,@Param("password")String password);

    /**
     * 通过用户名获取用户验证问题
     */
    String getQuestionByUsername(String username);

    /**
     * 检查密保问题和答案是否正确
     */
    int checkAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);

    /**
     * 忘记密码时使用，通过token重置密码
     */
    int resetPasswordByToken(@Param("username")String username, @Param("newPassword")String newPassword);

    /**
     * 登录时使用，通过旧密码重置新密码
     */
    int resetPasswordByOldPass(@Param("user") User user,@Param("newPassword")String newPassword);

    /**
     * 更新用户信息
     */
    int updateUserInfo(User user);

    /**
     * 查询该 userId 是否存在
     */
    Boolean isExistByUserId(Integer userId);
}