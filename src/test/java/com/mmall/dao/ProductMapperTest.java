package com.mmall.dao;

import com.mmall.pojo.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Created By Cx On 2018/9/6 16:30
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉Junit Spring的配置文件位置
@ContextConfiguration({"classpath:applicationContext.xml"})
public class ProductMapperTest {

    @Autowired
    ProductMapper productMapper;

    @Test
    public void getById() {
        Product a = productMapper.getById(26);
        Product b = productMapper.getById(28);
//        BigDecimal c = new BigDecimal(2.05);
//        BigDecimal d = new BigDecimal(3.01);
        System.out.println(a.getPrice().add(b.getPrice()));
        System.out.println(new BigDecimal("2.05").add(new BigDecimal(Double.toString(0.01))));
    }
}