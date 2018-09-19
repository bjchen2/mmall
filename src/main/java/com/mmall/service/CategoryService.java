package com.mmall.service;

import com.mmall.pojo.Category;

import java.util.List;
import java.util.Set;

/**
 * Created By Cx On 2018/8/29 13:01
 */
public interface CategoryService {

    void addCategory(String categoryName,int parentId);

    /**
     * 通过id更新类目名称
     */
    void updateCategoryName(int categoryId,String categoryName);

    /**
     * 根据parentId获取所有兄弟类目
     */
    List<Category> getBrotherByParentId(int parentId);

    /**
     * 查询某个类目下的所有类目
     * 即获取某个类目的子孙节点
     * 返回该类目及其子孙节点的ID
     */
    Set<Integer> getDeepChildrenByCategoryId(Integer categoryId);
}
