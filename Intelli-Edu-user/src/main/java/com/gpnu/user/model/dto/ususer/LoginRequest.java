package com.gpnu.user.model.dto.ususer;

import com.gpnu.user.model.enums.LoginType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求DTO
 */
@Data
@Schema(description = "用户登录请求DTO")
public class LoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "登录类型不能为空")
    @Schema(description = "登录类型,4-用户名密码登录, 1-手机号登录, 2-邮箱登录, 3-微信登录", example = "4")
    private LoginType loginType;

    @NotBlank(message = "用户名不能为空", groups = UsernamePasswordGroup.class)
    @Schema(description = "用户名,仅在用户名密码登录时必填", example = "张三")
    private String username;

    @NotBlank(message = "密码不能为空", groups = UsernamePasswordGroup.class)
    @Schema(description = "密码,仅在用户名密码登录时必填", example = "123456")
    private String password;

    @NotBlank(message = "手机号不能为空", groups = MobileCodeGroup.class)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确", groups = MobileCodeGroup.class)
    @Schema(description = "手机号,仅在手机号登录时必填", example = "13800010004")
    private String mobile;

    @NotBlank(message = "邮箱不能为空", groups = EmailCodeGroup.class)
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确", groups = EmailCodeGroup.class)
    @Schema(description = "邮箱,仅在邮箱登录时必填", example = "1234567@qq.com")
    private String email;

    @NotBlank(message = "验证码不能为空", groups = {MobileCodeGroup.class, EmailCodeGroup.class})
    @Schema(description = "验证码,仅在手机号登录和邮箱登录时必填", example = "645632")
    private String code; // 验证码

    @NotBlank(message = "OpenID不能为空", groups = OpenIdGroup.class)
    @Schema(description = "微信/QQ OpenID,仅在微信登录时必填", example = "oTg1234567890abcdefg")
    private String openId; // 微信/QQ OpenID

    // JSR 303 validation groups
    public interface UsernamePasswordGroup {}
    public interface MobileCodeGroup {}
    public interface EmailCodeGroup {}
    public interface OpenIdGroup {}
}