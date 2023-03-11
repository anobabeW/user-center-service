package com.ano.usercenterservice.service.impl;

import com.ano.usercenterservice.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ano.usercenterservice.model.domain.User;
import com.ano.usercenterservice.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ano.usercenterservice.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author wangjiao
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2023-02-26 11:35:46
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    /** 用户账户最小长度 */
    public static final int MIN_ACCOUNT_LENGTH = 4;

    /** 用户密码最小长度 */
    public static final int MIN_PASSWORD_LENGTH = 8;

    /** 盐值：混淆密码 */
    private static final String SALT = "ano";

    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验 TODO: 自定义异常
        //1)非空
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)) {
            return -1;
        }
        //2)长度
        if (userAccount.length() < MIN_ACCOUNT_LENGTH) {
            return -1;
        }
        if (userPassword.length() < MIN_PASSWORD_LENGTH || checkPassword.length() < MIN_PASSWORD_LENGTH) {
            return -1;
        }
        //3)校验特殊字符
        String validPatter = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPatter).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        //4)密码和再次输入密码是否相同
        if (!StringUtils.equals(userPassword,checkPassword)) {
            return -1;
        }
        //5)账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account",userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count > 0) {
            return -1;
        }

        // 2. 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex(String.format(SALT,userPassword).getBytes());

        //3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if(!saveResult) {
            return -1;
        }

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验 TODO: return优化
        if (StringUtils.isAnyBlank(userAccount,userPassword)) {
            return null;
        }
        if (userAccount.length() < MIN_ACCOUNT_LENGTH) {
            return null;
        }
        if (userPassword.length() < MIN_PASSWORD_LENGTH) {
            return null;
        }
        String validPatter = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPatter).matcher(userAccount);
        if(matcher.find()) {
            return null;
        }
        // 2. 校验密码，与库表中的密文密码比对
        String encryptPassword = DigestUtils.md5DigestAsHex(String.format(SALT,userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account",userAccount);
        queryWrapper.eq("user_password",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }
        // 3. 用户信息脱敏
        User safetyUser = getSafetyUser(user);

        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 用户信息脱敏
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if(originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUserName(originUser.getUserName());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUserRole(originUser.getUserRole());
        return safetyUser;
    }
}




