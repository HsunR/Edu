package com.gpnu.exam.question.model.dto;

import com.gpnu.common.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "题库查询请求")
public class QuestionBankQueryRequest extends PageRequest {

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "关键词搜索")
    private String keyword;
}
