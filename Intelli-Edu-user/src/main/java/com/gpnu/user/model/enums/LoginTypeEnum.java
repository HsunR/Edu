package com.gpnu.user.model.enums;


import lombok.Getter;

@Getter
public enum LoginTypeEnum {

    MOBILE_CODE(1, "手机验证码登录", "mobileCodeLoginStrategy"),
    EMAIL_CODE(2, "邮箱验证码登录", "emailCodeLoginStrategy"),
    WECHAT_OPENID(3, "微信OpenID登录", "wechatOpenIdLoginStrategy"),
    USERNAME_PASSWORD(4, "用户名密码登录", "usernamePasswordLoginStrategy");

    /**
     * 登录类型码，对应LoginRequest中的loginType
     */
    private final int code;

    /**
     * 登录类型描述
     */
    private final String description;

    /**
     * 对应策略Bean的名称 (可选，用于LoginService通过名称查找Bean)
     * 注意：这里的值应该与具体策略实现类上的 @Component 注解值（如果没有指定，则为类名首字母小写）一致
     */
    private final String strategyBeanName;

    LoginTypeEnum(int code, String description, String strategyBeanName) {
        this.code = code;
        this.description = description;
        this.strategyBeanName = strategyBeanName;
    }

    /**
     * 根据类型码获取对应的枚举
     * @param code 类型码
     * @return 对应的LoginTypeEnum，如果不存在则返回null
     */
    public static LoginTypeEnum getByCode(int code) {
        for (LoginTypeEnum type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据策略Bean名称获取对应的枚举
     * @param beanName 策略Bean名称
     * @return 对应的LoginTypeEnum，如果不存在则返回null
     */
    public static LoginTypeEnum getByStrategyBeanName(String beanName) {
        for (LoginTypeEnum type : values()) {
            if (type.getStrategyBeanName().equals(beanName)) {
                return type;
            }
        }
        return null;
    }
}
