package com.mmall.dao;

import com.mmall.utils.MD5Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created By Cx On 2018/8/30 18:38
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉Junit Spring的配置文件位置
@ContextConfiguration({"classpath:applicationContext.xml"})
public class UserMapperTest{

    @Autowired
    UserMapper userMapper;

    @Test
    public void insert() {
        System.out.println("=============================================");
        System.out.println(userMapper);
    }

    @Test
    public void getById() {
    }

    @Test
    public void checkUsername() {
    }

    @Test
    public void checkEmail() {
    }

    @Test
    public void checkEmailById() {
    }

    @Test
    public void selectLogin() {
        System.out.println(userMapper.selectLogin("admin", MD5Util.MD5EncodeUtf8("admin")));
    }

    @Test
    public void getQuestionByUsername() {
    }

    @Test
    public void checkAnswer() {
    }

    @Test
    public void resetPasswordByToken() {
    }

    @Test
    public void resetPasswordByOldPass() {
    }

    @Test
    public void updateUserInfo() {
    }
}