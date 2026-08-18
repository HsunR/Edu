package com.gpnu.resource.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "上传确认请求")
public class UploadConfirmRequest implements Serializable {

    @NotNull(message = "资源ID不能为空")
    @Schema(description = "申请预签名返回的 resourceId",example = "123456789")
    private Long resourceId;            // 申请预签名时返回的 resourceId


}