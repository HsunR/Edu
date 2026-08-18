package com.gpnu.user.constants;

public class CaptchaConstants {

    // 登录验证码前缀 (用于手机/邮箱验证码登录，与注册区分)
    public static final String LOGIN_CODE_PREFIX_MOBILE = "login:code:mobile:";
    public static final String LOGIN_CODE_PREFIX_EMAIL = "login:code:email:";

    public static final String REGISTER_CODE_PREFIX_MOBILE = "register:code:mobile:";
    public static final String REGISTER_CODE_PREFIX_EMAIL = "register:code:email:";
    public static final long CODE_EXPIRATION_SECONDS = 5 * 60; // 验证码有效期5分钟 (秒)
    public static final long CODE_SEND_INTERVAL_SECONDS = 60; // 验证码发送间隔60秒
    public static final int CAPTCHA_LENGTH = 6; // 验证码长度
}
