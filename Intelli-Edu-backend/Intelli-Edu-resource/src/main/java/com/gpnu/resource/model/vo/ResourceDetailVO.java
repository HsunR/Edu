package com.gpnu.resource.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class ResourceDetailVO extends ResourceVO {
    private static final long serialVersionUID = 1L;

    @Schema(description = "上传者id")
    private Long uploaderId;


    private VideoMetaVO videoMeta;
}

