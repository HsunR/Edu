package com.gpnu.resource.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class PresignedUrlVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "预创建的资源记录 ID",example = "12345")
    private Long resourceId;

    @Schema(description = "预签名上传 URL",example = "https://bucket-name.cos.region.myqcloud.com/object-key?signature=xxx")
    private String uploadUrl;

    @Schema(description = "COS 存储键（前端上传时需要）",example = "object-key")
    private String storageKey;

    @Schema(description = "上传完成后的访问地址",example = "https://bucket-name.cos.region.myqcloud.com/object-key")
    private String accessUrl;

    @Schema(description = "预签名 URL 的过期时间（秒）",example = "3600")
    private Long expiresIn;             // 预签名有效期（秒）
}