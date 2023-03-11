package com.ano.usercenterservice.controller;

import com.ano.usercenterservice.model.domain.User;
import com.ano.usercenterservice.model.request.UserLoginRequest;
import com.ano.usercenterservice.model.request.UserRegisterRequest;
import com.ano.usercenterservice.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ano.usercenterservice.constant.UserConstant.ADMIN_ROLE;
import static com.ano.usercenterservice.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author wangjiao
 * @date 2023年02月27日 08:46:30
 * @RestController restful风格，默认返回json类型
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)) {
            return null;
        }
        return userService.userRegister(userAccount, userPassword, checkPassword);
    }

    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount,userPassword)) {
            return null;
        }
        return userService.userLogin(userAccount, userPassword,request);
    }

    /**
     * 用户注销
     * @param request
     */
    @PostMapping("/outLogin")
    public void userOutLogin(HttpServletRequest request) {
        request.getSession().setAttribute(USER_LOGIN_STATE,null);
    }

    /**
     * 查询当前登录用户
     * @param request
     * @return
     */
    @GetMapping("/current")
    public User getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            return null;
        }
        //对于信息变化的频繁程度可能需要从数据库再查询一次
        long userId = currentUser.getId();
        /* TODO 后续需要校验是否合法 是否被封号？ */
        User user = userService.getById(userId);
        return userService.getSafetyUser(user);
    }

    /**
     * 根据username查询用户
     * @param username
     * @param request
     * @return
     */
    @GetMapping("/query")
    public List<User> queryUserListByName(String username, HttpServletRequest request) {
        // 仅管理员可查看
        if (!isAdmin(request)) {
            return new ArrayList<>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)) {
            queryWrapper.like("user_name",username);
        }
        List<User> userList = userService.list(queryWrapper);
        return userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
    }

    /**
     * 根据id删除用户
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public boolean deleteUserById(@RequestBody long id, HttpServletRequest request) {
        // 仅管理员可删除
        if (!isAdmin(request)) {
            return false;
        }
        if (id <= 0) {
            return false;
        }
        return userService.removeById(id);
    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if(user == null || user.getUserRole() != ADMIN_ROLE) {
            return false;
        }
        return true;
    }

}
