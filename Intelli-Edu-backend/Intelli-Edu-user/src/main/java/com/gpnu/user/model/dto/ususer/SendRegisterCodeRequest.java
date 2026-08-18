package com.gpnu.user.model.dto.ususer;

import com.gpnu.user.model.enums.RegisterType;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "注册类型，1-手机号注册，2-邮箱注册,3-微信注册", example = "2")
    private RegisterType registerType;

    // 对于手机注册，mobile不能为空且格式正确
    @NotNull(message = "手机号不能为空", groups = SendRegisterCodeRequest.MobileGroup.class)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确", groups = SendRegisterCodeRequest.MobileGroup.class)
    @Schema(description = "手机号，registerType为MOBILE_CODE时必填", example = "13800010004")
    private String mobile;

    // 对于邮箱注册，email不能为空且格式正确
    @NotNull(message = "邮箱不能为空", groups =SendRegisterCodeRequest. EmailGroup.class)
    @Email(message = "邮箱格式不正确", groups =SendRegisterCodeRequest. EmailGroup.class)
    @Schema(description = "邮箱，registerType为EMAIL_CODE时必填", example = "12345@qq.com")
    private String email;

    // JSR 303 validation groups
    public interface MobileGroup {}
    public interface EmailGroup {}
}