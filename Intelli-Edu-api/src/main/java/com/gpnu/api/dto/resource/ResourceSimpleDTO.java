package com.gpnu.api.dto.resource;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResourceSimpleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long resourceId;
    private String resourceName;
    private Integer resourceType;   // 1=视频 2=文档 3=图片
    private String fileFormat;      // pdf/mp4/docx
    private String accessUrl;
    private Long fileSize;
}