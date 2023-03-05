package com.ano.usercenterservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ano.usercenterservice.model.domain.User;

import javax.servlet.http.HttpServletRequest;

/**
* @author wangjiao
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2023-02-26 11:35:46
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 再次输入密码
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的登录用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户信息脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);
}
