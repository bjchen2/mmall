package com.mmall.dao;

import com.mmall.pojo.Category;
import com.mmall.utils.FTPUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;

/**
 * Created By Cx On 2018/8/30 23:12
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉Junit Spring的配置文件位置
@ContextConfiguration({"classpath:applicationContext.xml"})
public class CategoryMapperTest {

    @Autowired
    CategoryMapper categoryMapper;


    @Test
    @Rollback
    public void insert() {
        Category category = new Category("可s乐",100005,false);
        categoryMapper.insert(category);
    }

    @Test
    public void getByCategoryId() {
    }

    @Test
    public void updateCategoryNameByCategoryId() {
    }

    @Test
    public void getBrotherByParentId() {
    }
}