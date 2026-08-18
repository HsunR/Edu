package com.gpnu.resource.model.vo;

import com.gpnu.resource.model.enums.ResourceType;
import com.gpnu.resource.model.enums.UploadStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
public class ResourceVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long resourceId;
    private String resourceName;
    private ResourceType resourceType;
    private String fileFormat;
    private Long fileSize;
    private String accessUrl;
    private UploadStatus uploadStatus;
    private OffsetDateTime createdAt;
}