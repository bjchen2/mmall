package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import com.mmall.utils.ResultUtil;
import com.mmall.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 后台管理员用户有关接口
 * Created By Cx On 2018/8/28 22:14
 */
@RestController
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    UserService userService;

    /**
     * 后台管理员登录接口
     * data包含 username，password
     */
    @PostMapping("/login")
    public ResultVO login(@RequestBody Map<String,String>data, HttpSession session){
        User user = userService.login(data.get("username"),data.get("password"));
        if(user.getRole() != Const.Role.ROLE_ADMIN){
            //说明登录的不是管理员
            return ResultUtil.error("不是管理员,无法登录");
        }
        session.setAttribute(Const.CURRENT_USER,user);
        return ResultUtil.success();
    }

}
