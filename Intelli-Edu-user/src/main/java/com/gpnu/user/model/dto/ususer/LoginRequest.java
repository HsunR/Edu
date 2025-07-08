package com.gpnu.user.model.dto.ususer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求DTO
 */
@Data
public class LoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "登录类型不能为空")
    private Integer loginType;

    @NotBlank(message = "用户名不能为空", groups = UsernamePasswordGroup.class)
    private String username;

    @NotBlank(message = "密码不能为空", groups = UsernamePasswordGroup.class)
    private String password;

    @NotBlank(message = "手机号不能为空", groups = MobileCodeGroup.class)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确", groups = MobileCodeGroup.class)
    private String mobile;

    @NotBlank(message = "邮箱不能为空", groups = EmailCodeGroup.class)
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确", groups = EmailCodeGroup.class)
    private String email;

    @NotBlank(message = "验证码不能为空", groups = {MobileCodeGroup.class, EmailCodeGroup.class})
    private String code; // 验证码

    @NotBlank(message = "OpenID不能为空", groups = OpenIdGroup.class)
    private String openId; // 微信/QQ OpenID

    // JSR 303 validation groups
    public interface UsernamePasswordGroup {}
    public interface MobileCodeGroup {}
    public interface EmailCodeGroup {}
    public interface OpenIdGroup {}
}