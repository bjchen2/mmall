package com.mmall.dao;

import com.mmall.config.AlipayConfig;
import com.mmall.utils.MD5Util;
import com.mmall.utils.PropertiesUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileNotFoundException;
import java.io.FileReader;

import static org.junit.Assert.*;

/**
 * Created By Cx On 2018/9/6 18:08
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉Junit Spring的配置文件位置
@ContextConfiguration({"classpath:applicationContext.xml"})
public class CartMapperTest {

    @Autowired
    CartMapper cartMapper;

    @Test
    public void insert() {
        System.out.println(MD5Util.MD5EncodeUtf8("admin"));
    }

    @Test
    public void getOneByUserIdAndProductId() {
//        try {
//            FileReader fileReader = new FileReader("D:\\MyEclipse 2016 CI\\WorkSpace\\mmall\\src\\main\\resources\\zfbinfo.properties");
//        } catch (FileNotFoundException e) {
//            System.out.println(".....");
//        }
        AlipayConfig.init("D:/MyEclipse 2016 CI/WorkSpace/mmall/src/main/resources/zfbinfo.properties");
        System.out.println(AlipayConfig.privateKey);
    }

    @Test
    public void update() {
        System.out.println( PropertiesUtil.getProperty("zfbinfo.url"));
    }

    @Test
    public void getAllByUserId() {
    }

    @Test
    public void isAllCheckedByUserId() {
        System.out.println(cartMapper.isAllCheckedByUserId(291));
    }
}