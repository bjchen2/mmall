package com.mmall.aspect;

import com.mmall.common.Const;
import com.mmall.enums.ResultEnum;
import com.mmall.exception.SellException;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 管理员权限检测
 * Created By Cx On 2018/9/1 23:34
 */
@Aspect
@Order(1)
@Slf4j
@Component
public class AdminAuthorizeAspect {

    @Autowired
    UserService userService;

    //切点范围
    @Pointcut("execution(public * com.mmall.controller.backend..*.*(..)) &&" +
            "!execution(public * com.mmall.controller.backend.UserManageController.*(..))")
    public void verifyAdmin(){}

    @Pointcut("execution(public * com.mmall.controller.portal..*.*(..)) &&" +
            "!execution(public * com.mmall.controller.portal.UserController.*(..)) &&" +
            "!execution(public * com.mmall.controller.portal.OrderController.alipayCallback(..)) ")
    public void verifyUser(){}

    @Before("verifyAdmin()")
    public void doVerifyAdmin(){
        User user = doVerifyLogin();
        //校验一下是否是管理员
        if(!userService.checkAdminRole(user.getId())){
            log.error("用户非管理员");
            throw new SellException(ResultEnum.ERROR.getCode(),"无权限操作,需要管理员权限");
        }
    }

    @Before("verifyUser()")
    public User doVerifyLogin(){
        //获取HttpHttpServletRequest
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //获取session
        HttpSession session = request.getSession();
        //获取session中的用户信息
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            log.error("用户未登录");
            throw new SellException(ResultEnum.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        return user;
    }
}
