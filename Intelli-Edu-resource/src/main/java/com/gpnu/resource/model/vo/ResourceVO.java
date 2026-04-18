package com.gpnu.resource.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
public class ResourceVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long resourceId;
    private String resourceName;
    private Integer resourceType;
    private String fileFormat;
    private Long fileSize;
    private String accessUrl;
    private Integer uploadStatus;
    private OffsetDateTime createdAt;
}