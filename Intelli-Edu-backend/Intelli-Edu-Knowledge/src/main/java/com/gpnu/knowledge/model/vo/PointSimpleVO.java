package com.gpnu.knowledge.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "知识点简要信息")
public class PointSimpleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "知识点ID")
    private Long pointId;

    @Schema(description = "知识点名称")
    private String pointName;

    @Schema(description = "父知识点ID")
    private Long parentId;

    @Schema(description = "层级：1=一级知识点, 2=二级知识点")
    private Integer level;
}
