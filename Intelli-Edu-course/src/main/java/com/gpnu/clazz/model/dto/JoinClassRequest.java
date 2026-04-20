package com.gpnu.clazz.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "加入班级请求对象")
public class JoinClassRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "邀请码", example = "AS3C3A31")
    @NotBlank
    @Size(min = 8, max = 8, message = "邀请码必须为8位")
    private String inviteCode;

}
