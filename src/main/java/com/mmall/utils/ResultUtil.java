package com.mmall.utils;

import com.mmall.enums.ResultEnum;
import com.mmall.vo.ResultVO;

/**
 * Created By Cx On 2018/8/24 23:35
 */
public class ResultUtil {

    public static ResultVO success(){
        return success(null);
    }

    public static ResultVO success(Object data){
        return new ResultVO(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),data);
    }

    public static ResultVO error(Integer errorCode,String msg){
        return new ResultVO(errorCode,msg,null);
    }

    public static ResultVO error(){
        return error(ResultEnum.ERROR.getCode(),ResultEnum.ERROR.getMsg());
    }

    public static ResultVO error(String errorMsg){
        return error(ResultEnum.ERROR.getCode(),errorMsg);
    }

}
