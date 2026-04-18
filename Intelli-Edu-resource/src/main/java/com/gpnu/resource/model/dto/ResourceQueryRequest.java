package com.gpnu.resource.model.dto;

import com.gpnu.common.common.PageRequest;
import com.gpnu.resource.model.enums.UploadStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "资源查询请求对象")
public class ResourceQueryRequest extends PageRequest implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 资源名称（模糊搜索）
     */
    @Schema(description = "资源名称（模糊搜索）",example = "avatar.png")
    private String resourceName;

    /**
     * 资源大类：1=视频 2=文档 3=图片
     */
    @Schema(description = "资源大类：1=视频 2=文档 3=图片",example = "1")
    private Integer resourceType;

    /**
     * 文件格式：pdf/mp4/docx 等（精确匹配）
     */
    @Schema(description = "文件格式：pdf/mp4/docx 等（精确匹配）",example = "pdf")
    private String fileFormat;

    /**
     * 上传状态：0=待确认 1=成功 2=失败
     */
    @Schema(description = "上传状态：0=待确认 1=成功 2=失败",example = "1")
    private UploadStatus uploadStatus;

    /**
     * 创建时间范围 - 开始
     */
    @Schema(description = "创建时间范围 - 开始",example = "2024-01-01T00:00:00")
    private LocalDateTime createdFrom;

    /**
     * 创建时间范围 - 结束
     */
    @Schema(description = "创建时间范围 - 结束",example = "2024-12-31T23:59:59")
    private LocalDateTime createdTo;


}