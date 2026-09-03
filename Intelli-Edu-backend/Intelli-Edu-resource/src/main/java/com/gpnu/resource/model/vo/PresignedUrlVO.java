package com.gpnu.resource.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class PresignedUrlVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "预创建的资源记录 ID",example = "12345")
    private Long resourceId;

    @Schema(description = "预签名上传 URL", example = "http://localhost:9000/intelli-edu-resources/object-key?signature=xxx")
    private String uploadUrl;

    @Schema(description = "对象存储键", example = "video/2026/09/03/object-key.mp4")
    private String storageKey;

    @Schema(description = "上传完成后的访问地址", example = "http://localhost:9000/intelli-edu-resources/object-key")
    private String accessUrl;

    @Schema(description = "预签名 URL 的过期时间（秒）",example = "3600")
    private Long expiresIn;             // 预签名有效期（秒）
}
