package com.gpnu.user.model.dto.ususer;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.io.Serializable;

/**
 * 用户注册请求DTO
 */
@Data

public class RegisterRequest implements Serializable {
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 20, message = "用户姓名长度必须在2到20之间")
    private String name;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,20}$",
             message = "密码必须包含字母、数字和特殊字符且长度在6到20之间")
    private String password;

    @NotNull(message = "注册类型不能为空")
    private Integer registerType;

    // 根据registerType，mobile或email至少有一个需要被校验

    @NotNull(message = "手机号不能为空", groups = MobileGroup.class)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确", groups = MobileGroup.class)
    private String mobile;

    @NotNull(message = "邮箱不能为空", groups = EmailGroup.class)
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确", groups = EmailGroup.class)
    private String email;

    @NotBlank(message = "验证码不能为空")
    private String code;

    public interface EmailGroup {}
    public interface MobileGroup {}




}