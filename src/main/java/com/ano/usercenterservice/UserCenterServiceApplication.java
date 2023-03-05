package com.ano.usercenterservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author wangjiao
 */
@SpringBootApplication
@MapperScan("com.ano.usercenterservice.mapper")
public class UserCenterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserCenterServiceApplication.class, args);
    }

}
