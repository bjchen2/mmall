package com.mmall.controller.portal;

import com.mmall.enums.ProductEnum;
import com.mmall.enums.ResultEnum;
import com.mmall.exception.SellException;
import com.mmall.service.ProductService;
import com.mmall.utils.ResultUtil;
import com.mmall.vo.ProductDetailVO;
import com.mmall.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created By Cx On 2018/9/4 17:52
 */
@RestController
@RequestMapping("/product")
@Slf4j
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("/one/{productId}")
    public ResultVO getOneById(@PathVariable("productId")Integer productId){
        ProductDetailVO product = productService.getProductDetail(productId);
        if (!product.getStatus().equals(ProductEnum.ON_SALE.getCode())){
            log.error("[客户端查询商品]商品未上架，productId={}",product.getId());
            throw new SellException(ResultEnum.ERROR.getCode(),"商品未上架");
        }
        return ResultUtil.success(product);
    }

    /**
     * 通过关键字 或 类目Id 查询符合条件的商品列表
     * 按 orderBy 的顺序，分页返回
     * orderBy 要求格式： A_B  ， A为排序关键字，B为升序（asc）或降序（desc），如按价格升序,则 orderBy 为 price_asc
     */
    @GetMapping("/list")
    public ResultVO getListByKeyAndCategoryId(@RequestParam(required = false) String key,
                                             @RequestParam(required = false) Integer categoryId,
                                             @RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                             @RequestParam(defaultValue = "price_asc")String orderBy){
        return ResultUtil.success(productService.getOnSaleByKeyAndCategoryId(key,categoryId,pageNum,pageSize,orderBy));
    }

}
