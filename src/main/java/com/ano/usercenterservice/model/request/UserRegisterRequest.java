package com.ano.usercenterservice.model.request;


import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 * @author wangjiao
 * @date 2023年02月28日 08:38:41
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -587117808955291497L;

    /** 用户账号 */
    private String userAccount;

    /** 用户密码 */
    private String userPassword;

    /** 校验密码 */
    private String checkPassword;

}
