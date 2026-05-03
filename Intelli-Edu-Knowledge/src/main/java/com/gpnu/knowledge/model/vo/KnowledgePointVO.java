package com.gpnu.knowledge.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@Schema(description = "知识点详情")
public class KnowledgePointVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "知识点ID")
    private Long pointId;

    @Schema(description = "知识点名称")
    private String pointName;

    @Schema(description = "所属课程ID")
    private Long courseId;

    @Schema(description = "父知识点ID，null表示一级知识点")
    private Long parentId;

    @Schema(description = "知识点描述")
    private String description;

    @Schema(description = "排序序号")
    private Integer orderIndex;

    @Schema(description = "创建时间")
    private OffsetDateTime createdAt;

    @Schema(description = "更新时间")
    private OffsetDateTime updatedAt;
}
