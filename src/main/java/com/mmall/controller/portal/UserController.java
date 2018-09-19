package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import com.mmall.utils.ResultUtil;
import com.mmall.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 门户端用户有关
 * Created By Cx On 2018/8/25 0:32
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 用户登录接口
     * data包含username 和 password 字段
     */
    @PostMapping("/login")
    public ResultVO login(@RequestBody Map<String,String> data, HttpSession session){
        User user = userService.login(data.get("username"),data.get("password"));
        //设置session
        session.setAttribute(Const.CURRENT_USER,user);
        return ResultUtil.success(user);
    }

    /**
     * 用户登出接口
     */
    @PostMapping("/logout")
    public ResultVO logout(HttpSession session){
        //设置session
        session.removeAttribute(Const.CURRENT_USER);
        return ResultUtil.success();
    }

    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    public ResultVO register(@RequestBody User user){
        userService.register(user);
        return ResultUtil.success();
    }

    /**
     * 检验type的value是否存在
     * 如：检验username - admin 是否存在
     * data 包含 type 和 value 字段
     */
    @PostMapping("/check_exist")
    public ResultVO checkExist(@RequestBody Map<String,String>data){
        String type = data.get("type"),value = data.get("value");
        if (userService.checkExist(type,value)){
            return ResultUtil.success(type.concat("已存在"));
        }
        return ResultUtil.error(type.concat("不存在"));
    }

    /**
     * 获取用户信息
     */
    @PostMapping("/get_info")
    public ResultVO getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            log.error("[获取用户信息]获取失败，用户未登录");
            return ResultUtil.error("用户未登录");
        }
        return ResultUtil.success(user);
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/update_info")
    public ResultVO updateInfo(@RequestBody User user,HttpSession session){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            log.error("[更新用户信息]更新失败，用户未登录");
            return ResultUtil.error("用户未登录");
        }
        user.setId(currentUser.getId());
        userService.updateInfo(user);
        //将更新字段赋值并存入session
        if (user.getEmail() != null) currentUser.setEmail(user.getEmail());
        if (user.getPhone() != null) currentUser.setPhone(user.getPhone());
        if (user.getQuestion() != null) currentUser.setQuestion(user.getQuestion());
        if (user.getAnswer() != null) currentUser.setAnswer(user.getAnswer());
        session.setAttribute(Const.CURRENT_USER,currentUser);
        return ResultUtil.success();
    }

    /**
     * 获取用户验证问题
     * data 包含 username 字段
     */
    @PostMapping("/get_question")
    public ResultVO getQuestion(@RequestBody Map<String,String> data){
        return ResultUtil.success(userService.getQuestionByUsername(data.get("username")));
    }

    /**
     * 验证用户密保问题和答案是否正确
     * data包含 username question answer字段
     * 返回token用于更改密码
     */
    @PostMapping(value = "/check_answer")
    public ResultVO checkAnswer(@RequestBody Map<String,String> data){
        return ResultUtil.success(userService.checkAnswer(data.get("username"),data.get("question"),data.get("answer")));
    }

    /**
     * 忘记密码时，重置密码接口
     * data包含username，newPassword，token
     * token由checkAnswer获取
     */
    @PostMapping("/reset_password/token")
    public ResultVO resetPasswordByToken(@RequestBody Map<String,String> data){
        userService.resetPasswordByToken(data.get("username"),data.get("newPassword"),data.get("token"));
        return ResultUtil.success();
    }

    /**
     * 登录时，重置密码接口
     * data包含oldPassword，newPassword
     * token由checkAnswer获取
     */
    @PostMapping("/reset_password/old_password")
    public ResultVO resetPasswordByOldPass(@RequestBody Map<String,String> data,HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            log.error("[修改密码]修改失败，用户未登录");
            return ResultUtil.error("用户未登录");
        }
        user.setPassword(data.get("oldPassword"));
        userService.resetPasswordByOldPass(user,data.get("newPassword"));
        return ResultUtil.success();
    }


}
