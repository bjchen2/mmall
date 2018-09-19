package com.mmall.service.impl;

import com.mmall.dao.CategoryMapper;
import com.mmall.enums.ResultEnum;
import com.mmall.exception.SellException;
import com.mmall.pojo.Category;
import com.mmall.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created By Cx On 2018/8/29 13:02
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void addCategory(String categoryName, int parentId) {
        if(StringUtils.isBlank(categoryName)){
            log.error("[添加类目]添加失败，类目名为空");
            throw new SellException(ResultEnum.ERROR.getCode(),"类目名为空");
        }
        if (parentId != 0){
            if (categoryMapper.getByCategoryId(parentId)==null){
                log.error("[添加类目]添加失败，父类目Id不存在，parentId={}",parentId);
                throw new SellException(ResultEnum.ERROR.getCode(),"父类目不存在");
            }
        }
        //true表示这个分类是可用的
        Category category = new Category(categoryName,parentId,true);

        if(categoryMapper.insert(category) < 1){
            log.error("[添加类目]添加失败，服务器连接失败");
            throw new SellException(ResultEnum.ERROR.getCode(),"服务器连接失败");
        }
    }

    @Override
    public void updateCategoryName(int categoryId, String categoryName) {
        if (StringUtils.isBlank(categoryName)){
            log.error("[更新类目名称]更新失败，类目名称为空,categoryName={}",categoryName);
            throw new SellException(ResultEnum.ERROR.getCode(),"类目名称为空");
        }
        if (categoryMapper.updateCategoryNameByCategoryId(categoryId,categoryName) < 1){
            log.error("[更新类目名称]更新失败，Id不存在,categoryId={}",categoryId);
            throw new SellException(ResultEnum.ERROR.getCode(),"Id不存在");
        }
    }

    @Override
    public List<Category> getBrotherByParentId(int parentId) {
        return categoryMapper.getBrotherByParentId(parentId);
    }

    @Override
    public Set<Integer> getDeepChildrenByCategoryId(Integer categoryId) {
        Set<Integer> ans = new HashSet<>();
        ans.add(categoryId);
        addChildren(ans,categoryId);
        return ans;
    }

    /**
     * 查询categoryId的所有儿子，并把ID放入set
     */
    private void addChildren(Set<Integer> ans,int categoryId){
        List<Category> categories = getBrotherByParentId(categoryId);
        if (categories.isEmpty()) return;
        //将categories每个元素的ID生成一个Set添加进ans（category.getId()不能改为categoryId）
        ans.addAll(categories.stream().map(Category::getId).collect(Collectors.toSet()));
        for (Category category : categories){
            addChildren(ans,category.getId());
        }
    }
}
