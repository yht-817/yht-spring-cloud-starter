package com.yht.oauth2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author 鱼仔
 * @date 2021/9/7 5:09 下午
 * 概要
 */
@SpringBootTest
public class Oauth2SpringCloudApplicationTest {
    @Autowired
    public PasswordEncoder passwordEncoder;

    @Test
    public void passworf() {
        String encode = passwordEncoder.encode("123456");
        System.out.println(encode);
    }
}
