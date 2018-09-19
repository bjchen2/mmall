package com.mmall.controller.backend;

import com.mmall.enums.ResultEnum;
import com.mmall.exception.SellException;
import com.mmall.pojo.Product;
import com.mmall.service.FileService;
import com.mmall.service.ProductService;
import com.mmall.utils.PropertiesUtil;
import com.mmall.utils.ResultUtil;
import com.mmall.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By Cx On 2018/9/1 23:22
 */
@RestController
@RequestMapping("/manage/product")
@Slf4j
public class ProductManageController {

    @Autowired
    private ProductService productService;
    @Autowired
    private FileService fileService;

    /**
     * 商品增加 或 更新 接口
     * 判断条件为：productId是否为空，为空则为增加
     * 约定：subImages 的多张图之间用 逗号 隔开，所以 mainImage 即 subImages 第一个逗号前的字符串
     *
     */
    @PostMapping("/save")
    public ResultVO productSave(@RequestBody Product product){
        productService.saveOrUpdateProduct(product);
        return ResultUtil.success();
    }

    /**
     * 设置商品上下架状态
     * data包含 productId(int)，status(int)
     */
    @PostMapping("/set_sale_status")
    public ResultVO setSaleStatus(@RequestBody Map<String,Object>data){
        try{
            productService.setSaleStatus((int)data.get("productId"),(int)data.get("status"));
            return ResultUtil.success();
        }catch (NullPointerException e){
            log.error("【商品上下架】参数错误");
            throw new SellException(ResultEnum.ERROR.getCode(),"【商品上下架】参数错误");
        }
    }

    /**
     * 获取商品信息
     * data包含 productId(int)
     */
    @PostMapping("/detail")
    public ResultVO getDetail(@RequestBody Map<String,Object>data){
        return ResultUtil.success(productService.getProductDetail((Integer)data.get("productId")));
    }

    /**
     * 获取商品列表
     * data包含 pageNum(int)，pageSize（int）
     * 默认第1页，每页10行
     */
    @PostMapping("/list")
    public ResultVO getList(@RequestBody Map<String,Object> data){
        int pageNum = data.get("pageNum")==null?1: (Integer) data.get("pageNum");
        int pageSize = data.get("pageSize")==null?10: (Integer) data.get("pageSize");
        return ResultUtil.success(productService.getProductList(pageNum,pageSize));
    }

    /**
     * 通过关键字（模糊查询）和ID（可以只传一个条件）
     * 分页返回符合条件的商品
     * data包含 productName,productId(int),pageNum(int)，pageSize（int）
     * 默认第1页，每页10行
     */
    @PostMapping("/search")
    public ResultVO getProductByNameAndId(@RequestBody Map<String,Object> data){
        int pageNum = data.get("pageNum")==null?1: (Integer) data.get("pageNum");
        int pageSize = data.get("pageSize")==null?10: (Integer) data.get("pageSize");
        return ResultUtil.success(productService.getProductByNameAndId((String)data.get("productName"), (Integer) data.get("productId"),pageNum,pageSize));
    }

    /**
     * 上传文件
     * 需要配置SpringMVC配置文件（dispatcher-servlet）
     * 前端表单格式需要是multipart/form-data
     * TODO 未测试，线上服务器通过nginx配置，转发到对应网址，详情可见2-15到2-18 20分钟处
     */
    @PostMapping("/upload")
    public ResultVO upload(@RequestBody MultipartFile file, HttpServletRequest request){
        //获取servlet所在路径下（即tomcat工作路径webapp下的upload文件夹）的upload文件夹的路径
        String path = request.getSession().getServletContext().getRealPath("upload");
        System.out.println(path);
        //将文件先暂时存在工作路径的upload文件夹下（即tomcat工作目录的upload文件夹下）
        String targetFileName = fileService.upload(file,path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
        Map<String,String> m = new HashMap<>();
        m.put("url",url);
        return ResultUtil.success(m);
    }

    /**
     * 富文本上传
     * 富文本中对于返回值有自己的要求,这里假设前端使用simditor，所以按照simditor的要求进行返回
     * {
     *   "success": true/false,
     *   "msg": "error message", # optional
     *   "file_path": "[real file path]"
     * }
     * TODO 未测试，线上服务器通过nginx配置，转发到对应网址，详情可见2-15到2-18 20分钟处
     */
    @PostMapping("/rich_text_img_upload")
    public Map<String,Object> richTextImgUpload(@RequestBody MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        //将返回的HTTP消息头修改，一般按前端要求修改。
        response.setHeader("Access-Control-Allow-Headers","X-File-Name");
        //获取servlet所在路径下（即tomcat工作路径webapp下的upload文件夹）的upload文件夹的路径
        String path = request.getSession().getServletContext().getRealPath("upload");
        //将文件先暂时存在工作路径的upload文件夹下（即tomcat工作目录的upload文件夹下）
        String targetFileName = fileService.upload(file,path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
        Map<String,Object> m = new HashMap<>();
        m.put("success",true);
        m.put("file_path",url);
        m.put("msg","SUCCESS");
        return m;
    }

}
