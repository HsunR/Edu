package com.gpnu.resource.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class VideoConfirmRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "资源ID不能为空")
    @Schema(description = "申请预签名返回的 resourceId", example = "12345")
    private Long resourceId;

    @NotBlank(message = "VOD session key 不能为空")
    @Schema(description = "申请上传返回的 sessionKey", example = "abcde12345")
    private String vodSessionKey;       // ApplyUpload 返回的 sessionKey
}