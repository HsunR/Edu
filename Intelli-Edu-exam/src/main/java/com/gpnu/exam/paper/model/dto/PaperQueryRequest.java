package com.gpnu.exam.paper.model.dto;

import com.gpnu.common.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "试卷查询请求")
public class PaperQueryRequest extends PageRequest {

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "状态：0=草稿 1=已发布")
    private Integer status;

    @Schema(description = "关键词")
    private String keyword;
}
