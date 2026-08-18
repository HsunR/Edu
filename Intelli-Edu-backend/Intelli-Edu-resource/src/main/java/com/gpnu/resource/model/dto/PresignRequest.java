package com.gpnu.resource.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "预签名请求对象")
public class PresignRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "文件名不能为空")
    @Schema(description = "原始文件名（含后缀）", example = "example.jpg")
    private String fileName;            // 原始文件名（含后缀）

    @Schema(description = "文件大小",example = "102400")
    @NotNull
    private Long fileSize;              // 文件大小（字节）
}