package com.gpnu.user.model.dto.ususer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

@Data
public class SendLoginCodeRequest implements Serializable {

    @NotNull(message = "注册类型不能为空")
    private Integer loginType;

    // 对于手机注册，mobile不能为空且格式正确
    @NotBlank(message = "手机号不能为空", groups = SendLoginCodeRequest.MobileGroup.class)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确", groups = SendLoginCodeRequest.MobileGroup.class)
    private String mobile;

    // 对于邮箱注册，email不能为空且格式正确
    @NotBlank(message = "邮箱不能为空", groups = SendLoginCodeRequest.EmailGroup.class)
    @Email(message = "邮箱格式不正确", groups = SendLoginCodeRequest.EmailGroup.class)
    private String email;

    // JSR 303 validation groups
    public interface MobileGroup {}
    public interface EmailGroup {}
}
