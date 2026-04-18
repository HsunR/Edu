package com.gpnu.user.model.dto.ususer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

@Data
public class PasswordUpdateRequest implements Serializable {

    @NotBlank(message = "旧密码不能为空")
    @Schema(description = "旧密码，不能为空", example = "OldP@ssw0rd")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+...]).{6,20}$",
             message = "密码必须包含字母、数字和特殊字符且长度在6到20之间")
    @Schema(description = "新密码，必须包含字母、数字和特殊字符且长度在6到20之间", example = "NewP@ssw0rd")
    private String newPassword;
}