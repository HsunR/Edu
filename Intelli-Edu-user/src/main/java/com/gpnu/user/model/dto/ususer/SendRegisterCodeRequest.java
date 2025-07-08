package com.gpnu.user.model.dto.ususer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


import java.io.Serializable;

/**
 * 发送注册验证码请求DTO
 */
@Data
public class SendRegisterCodeRequest implements Serializable {
    @NotNull(message = "注册类型不能为空")
    private Integer registerType;

    // 对于手机注册，mobile不能为空且格式正确
    @NotNull(message = "手机号不能为空", groups = SendRegisterCodeRequest.MobileGroup.class)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确", groups = SendRegisterCodeRequest.MobileGroup.class)
    private String mobile;

    // 对于邮箱注册，email不能为空且格式正确
    @NotNull(message = "邮箱不能为空", groups =SendRegisterCodeRequest. EmailGroup.class)
    @Email(message = "邮箱格式不正确", groups =SendRegisterCodeRequest. EmailGroup.class)
    private String email;

    // JSR 303 validation groups
    public interface MobileGroup {}
    public interface EmailGroup {}
}