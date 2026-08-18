package com.gpnu.knowledge.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "知识点更新请求")
public class PointUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "知识点名称")
    private String pointName;

    @Schema(description = "知识点描述")
    private String description;

    @Schema(description = "排序序号")
    private Integer orderIndex;
}
