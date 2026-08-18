package com.gpnu.resource.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class VodPresignedUrlVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long resourceId;
    private String vodSessionKey;
    private List<String> mediaUploadUrls;
    private String coverUploadUrl;
    private Long expiresIn;
}