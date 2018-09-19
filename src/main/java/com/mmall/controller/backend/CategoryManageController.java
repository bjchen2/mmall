package com.mmall.controller.backend;

import com.mmall.enums.ResultEnum;
import com.mmall.exception.SellException;
import com.mmall.service.CategoryService;
import com.mmall.utils.ResultUtil;
import com.mmall.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created By Cx On 2018/8/29 13:00
 */
@RestController
@RequestMapping("/manage/category")
@Slf4j
public class CategoryManageController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加类目
     * data 包含 categoryName，parentId(int)
     */
    @PostMapping("/add_category")
    public ResultVO addCategory(@RequestBody Map<String,Object>data){
        String categoryName = (String) data.get("categoryName");
        int parentId;
        try {
            parentId = (int) data.get("parentId");
        }catch (NullPointerException e){
            log.error("【添加类目名称】父类目Id为空");
            throw new SellException(ResultEnum.ERROR.getCode(),"【添加类目名称】参数错误，父类目Id为空");
        }
        categoryService.addCategory(categoryName,parentId);
        return ResultUtil.success();
    }

    /**
     * 更新类目名称
     * data 包含 categoryName，categoryId(int)
     */
    @PostMapping("/set_category_name")
    public ResultVO setCategoryName(@RequestBody Map<String,Object>data){
        try {
            categoryService.updateCategoryName((int)data.get("categoryId"),(String) data.get("categoryName"));
        }catch (NullPointerException e){
            log.error("【更新类目名称】类目Id为空");
            throw new SellException(ResultEnum.ERROR.getCode(),"【更新类目名称】参数错误，类目Id为空");
        }
        return ResultUtil.success();
    }

    /**
     * 查看某类目的所有同级类目（即parentId相同的类目）
     * data 包含 parentId(int),默认不传为0
     */
    @PostMapping("/get_category_brother")
    public ResultVO getCategoryBrother(@RequestBody Map<String,Object>data){
        //查询子节点的category信息,并且不递归,保持平级
        int parentId = data.get("parentId")==null?0:(int)data.get("parentId");
        return ResultUtil.success(categoryService.getBrotherByParentId(parentId));
    }

    /**
     * 查看某类目及所有子孙类目id（包含子类目的子类目……）
     * data 包含 categoryId(int) ，默认不传为0
     */
    @PostMapping("/get_deep_category")
    public ResultVO getDeepChildrenByCategoryId(@RequestBody Map<String,Object>data){
        //查询所有子孙节点
        int categoryId = data.get("categoryId")==null?0:(int)data.get("categoryId");
        return ResultUtil.success(categoryService.getDeepChildrenByCategoryId(categoryId));
    }
}
