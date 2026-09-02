package com.gpnu.learning.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "错题统计查询")
public class WrongStatsQueryRequest {

    @Schema(description = "班级ID（与 courseId 至少传一个）")
    private Long classId;

    @Schema(description = "课程ID（与 classId 至少传一个）")
    private Long courseId;
}
