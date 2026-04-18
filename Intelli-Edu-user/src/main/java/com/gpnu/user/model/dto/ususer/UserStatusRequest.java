package com.gpnu.user.model.dto.ususer;

import com.gpnu.user.model.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "修改用户状态请求DTO")
public class UserStatusRequest implements Serializable {

    @NotNull(message = "状态不能为空")
    @Schema(description = "用户状态，1=正常 0=禁用", required = true,example = "1")
    private UserStatus status;             // 1=正常 0=禁用
}