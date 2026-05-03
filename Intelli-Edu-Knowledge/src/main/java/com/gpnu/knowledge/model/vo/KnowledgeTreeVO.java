package com.gpnu.knowledge.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "知识点树节点")
public class KnowledgeTreeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "知识点ID")
    private Long pointId;

    @Schema(description = "知识点名称")
    private String pointName;

    @Schema(description = "所属课程ID")
    private Long courseId;

    @Schema(description = "父知识点ID")
    private Long parentId;

    @Schema(description = "知识点描述")
    private String description;

    @Schema(description = "排序序号")
    private Integer orderIndex;

    @Schema(description = "子知识点列表")
    private List<KnowledgeTreeVO> children;
}
