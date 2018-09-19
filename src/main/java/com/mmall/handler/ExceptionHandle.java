package com.mmall.handler;

import com.mmall.exception.SellException;
import com.mmall.utils.ResultUtil;
import com.mmall.vo.ResultVO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常捕获类
 * Created By Cx On 2018/8/25 0:13
 */
@ControllerAdvice
public class ExceptionHandle {


    @ExceptionHandler(SellException.class)
    @ResponseBody
    public ResultVO handleSellException(SellException e){
        return ResultUtil.error(e.getCode(),e.getMessage());
    }
}
