package com.mmall.dao;

import com.mmall.pojo.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper {

    int insert(Category category);

    /**
     * 通过id获得相应类目
     */
    Category getByCategoryId(int categoryId);

    /**
     * 通过categoryId更新类目名称
     */
    int updateCategoryNameByCategoryId(@Param("categoryId") int categoryId, @Param("categoryName")String categoryName);

    /**
     * 获取父类目相同的所有类目
     */
    List<Category> getBrotherByParentId(int parentId);
}