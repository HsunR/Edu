package com.gpnu.knowledge.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "知识点创建请求")
public class PointCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "知识点名称不能为空")
    @Schema(description = "知识点名称", example = "封装")
    private String pointName;

    @NotNull(message = "课程ID不能为空")
    @Schema(description = "所属课程ID", example = "1001")
    private Long courseId;

    @Schema(description = "父知识点ID，null表示一级知识点", example = "100")
    private Long parentId;

    @Schema(description = "知识点描述", example = "面向对象的三大特性之一")
    private String description;

    @Schema(description = "排序序号", example = "0")
    private Integer orderIndex;
}
