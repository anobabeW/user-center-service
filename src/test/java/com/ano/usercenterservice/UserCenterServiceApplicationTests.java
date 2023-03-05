package com.ano.usercenterservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
class UserCenterServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testRegister() throws NoSuchAlgorithmException {
//        MessageDigest md5 = MessageDigest.getInstance("MD5");
//        byte[] bytes = md5.digest("abcd".getBytes(StandardCharsets.UTF_8));
//        String s = bytes.toString(); //[B@1a1cc163
        String s = DigestUtils.md5DigestAsHex("adbcd".getBytes());
        System.out.println(s);
    }

}
