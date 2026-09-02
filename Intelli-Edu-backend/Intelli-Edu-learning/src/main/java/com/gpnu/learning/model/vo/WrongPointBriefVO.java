package com.gpnu.learning.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "错题关联知识点摘要")
public class WrongPointBriefVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "知识点ID")
    private Long pointId;

    @Schema(description = "知识点名称")
    private String pointName;
}
