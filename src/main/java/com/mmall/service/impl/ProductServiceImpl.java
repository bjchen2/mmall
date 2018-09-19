package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.enums.ResultEnum;
import com.mmall.exception.SellException;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.CategoryService;
import com.mmall.service.ProductService;
import com.mmall.utils.DateTimeUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.ProductDetailVO;
import com.mmall.vo.ProductListVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created By Cx On 2018/9/1 23:24
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    CategoryService categoryService;

    private ProductDetailVO product2DetailVO(Product product){
        ProductDetailVO productDetailVO = new ProductDetailVO();
        BeanUtils.copyProperties(product,productDetailVO);

        //格式化时间
        productDetailVO.setCreateTime(DateTimeUtil.date2Str(product.getCreateTime()));
        productDetailVO.setUpdateTime(DateTimeUtil.date2Str(product.getUpdateTime()));

        //获取图片URL前缀
        productDetailVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        //获取父类目ID
        Category category = categoryMapper.getByCategoryId(product.getCategoryId());
        if (category == null){
            log.error("【查询商品详情】参数错误，商品类目不存在，CategoryId={}",product.getCategoryId());
            throw new SellException(ResultEnum.ERROR.getCode(),"商品类目不存在");
        }
        productDetailVO.setParentCategoryId(category.getParentId());

        return productDetailVO;
    }

    private ProductListVO product2ListVO(Product product){
        ProductListVO productListVO = new ProductListVO();
        BeanUtils.copyProperties(product,productListVO);
        productListVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return productListVO;
    }

    private List<ProductListVO> product2ListVO(List<Product> products){
        return products.stream().map(this::product2ListVO).collect(Collectors.toList());
    }

    @Override
    public void saveOrUpdateProduct(Product product) {
        if (product == null){
            log.error("【添加/更新商品】参数错误，product为空");
            throw new SellException(ResultEnum.ERROR.getCode(),"product为空");
        }
        if (categoryMapper.getByCategoryId(product.getCategoryId())==null){
            log.error("【添加/更新商品】参数错误，商品类目不存在，CategoryId={}",product.getCategoryId());
            throw new SellException(ResultEnum.ERROR.getCode(),"商品类目不存在");
        }
        String subImages = product.getSubImages();
        if ( !StringUtils.isBlank(subImages)){
            //将子图的第一张图赋值给主图
            String mainImage = subImages.contains(",")?subImages.substring(0,subImages.indexOf(",")):subImages;
            product.setMainImage(mainImage);
        }
        if ( product.getId() != null){
            //更新操作
            if (productMapper.updateById(product) < 1){
                log.error("[更新商品]，商品不存在，productId={}",product.getId());
                throw new SellException(ResultEnum.ERROR.getCode(),"商品不存在");
            }
        }else {
            //新增操作
            if (productMapper.insert(product) < 1){
                log.error("[新增商品]，商品不存在，productId={}",product.getId());
                throw new SellException(ResultEnum.ERROR.getCode(),"商品不存在");
            }
        }
    }

    @Override
    public void setSaleStatus(int productId, int productStatus) {
        Product product = new Product();
        product.setId(productId);
        product.setStatus(productStatus);
        if (productMapper.updateById(product) < 1){
            log.error("[更新商品状态]，商品不存在，productId={}",product.getId());
            throw new SellException(ResultEnum.ERROR.getCode(),"商品不存在");
        }
    }

    @Override
    public ProductDetailVO getProductDetail(Integer productId) {
        Product product = productMapper.getById(productId);
        if (product == null){
            log.error("[获取商品详情]获取失败，商品不存在，productId={}",productId);
            throw new SellException(ResultEnum.ERROR.getCode(),"商品不存在");
        }
        return product2DetailVO(product);
    }

    @Override
    public PageInfo getProductList(int pageNum, int pageSize) {
        //在该语句后面的第一个查询会自动在查询sql结尾加上limit分页，若后面的查询需要分页，需再次调用该方法
        PageHelper.startPage(pageNum,pageSize);
        List<Product> products = productMapper.getAll();
        return new PageInfo<>(product2ListVO(products));
    }

    @Override
    public PageInfo getProductByNameAndId(String productName, Integer productId, int pageNum, int pageSize) {
        if (productName != null){
            productName = "%".concat(productName).concat("%");
        }
        PageHelper.startPage(pageNum,pageSize);
        List<Product> products = productMapper.getAllByProductIdAndName(productId,productName);
        return new PageInfo<>(product2ListVO(products));
    }

    @Override
    public PageInfo getOnSaleByKeyAndCategoryId(String key, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy) {
        if (!StringUtils.isBlank(key)){
            key = "%".concat(key).concat("%");
        }
        //查询该类目及所有子类目的ID
        Set<Integer> categoryIds = categoryService.getDeepChildrenByCategoryId(categoryId);
        PageHelper.startPage(pageNum,pageSize);
        if (Const.PRODUCTS_ORDER_BY.contains(orderBy)){
            //添加排序规则
            //该方法传入一个字符串，规则为：关键字 升序/降序。如：price desc即为价格降序
            String[] orderByArray = orderBy.split("_");
            PageHelper.orderBy(orderByArray[0].concat(" ").concat(orderByArray[1]));
        }else {
            log.error("[查询商品列表]orderBy参数错误，orderBy={}",orderBy);
            throw new SellException(ResultEnum.ERROR.getCode(),"orderBy参数错误");
        }
        List<ProductListVO> productListVOs = product2ListVO(productMapper.getOnSaleByProductNameAndCategoryId(key,categoryIds));
        return new PageInfo<>(productListVOs);
    }
}
