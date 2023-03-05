package com.ano.usercenterservice.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wangjiao
 * @date 2023年03月01日 00:07:04
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 5154003569115094536L;

    /** 用户账号 */
    private String userAccount;

    /** 用户密码 */
    private String userPassword;
}
