package com.gpnu.resource.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class VideoMetaVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer duration;
    private String coverUrl;
    private String definition;
    private Integer transcodeStatus;
}