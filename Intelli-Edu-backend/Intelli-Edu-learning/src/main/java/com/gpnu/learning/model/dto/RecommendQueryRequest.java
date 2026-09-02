package com.gpnu.learning.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "个性化推荐查询参数")
public class RecommendQueryRequest implements Serializable {

    @NotNull(message = "classId 不能为空")
    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "课程ID（可选，用于校验与摘要文案）")
    private Long courseId;

    @Schema(description = "推荐场景：REVIEW_WEAK / REVIEW_WRONG / DAILY_PLAN / TEACHER_INTERVENTION")
    private String scene = "REVIEW_WEAK";

    @Min(1)
    @Max(50)
    @Schema(description = "返回条数上限")
    private Integer limit;
}
