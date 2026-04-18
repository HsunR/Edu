package com.gpnu.resource.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "上传确认请求")
public class UploadConfirmRequest implements Serializable {

    @NotNull(message = "资源ID不能为空")
    @Schema(description = "申请预签名返回的 resourceId",example = "123456789")
    private Long resourceId;            // 申请预签名时返回的 resourceId

    @NotBlank(message = "访问地址不能为空")
    @Schema(description = "COS 上传完成后的访问地址",example = "https://bucket-name.cos.region.myqcloud.com/object-key")
    private String accessUrl;           // COS 上传完成后的访问地址

    @Schema(description = "实际文件大小，单位字节",example = "1048576")
    private Long fileSize;              // 实际文件大小
}