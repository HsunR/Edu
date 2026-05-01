package com.gpnu.user.model.dto.ususer;

import com.gpnu.user.model.enums.SexType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateRequest implements Serializable {

    @Size(min = 2, max = 20, message = "姓名长度2-20")
    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "0-未知，1-男,2-女", example="0")
    private SexType sex;

    @Schema(description = "学校", example = "广东技术师范大学")
    private String school;

    @Schema(description = "个性签名", example = "热爱学习，热爱生活")
    private String personalSignature;
}