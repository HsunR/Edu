package com.gpnu.user.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码编码器
 */
@Component
public class PasswordEncoder {

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    /** 加密（注册/修改密码时调用） */
    public String encode(String rawPassword) {
        return bcrypt.encode(rawPassword);
    }

    /** 比对（登录时调用） */
    public boolean matches(String rawPassword, String encodedPassword) {
        return bcrypt.matches(rawPassword, encodedPassword);
    }
}