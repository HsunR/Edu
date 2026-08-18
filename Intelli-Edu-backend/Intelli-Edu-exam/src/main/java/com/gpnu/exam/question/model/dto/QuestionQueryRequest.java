package com.gpnu.exam.question.model.dto;

import com.gpnu.common.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "题目查询请求")
public class QuestionQueryRequest extends PageRequest {

    @Schema(description = "题库ID")
    private Long bankId;

    @Schema(description = "题目类型：0=单选 1=多选 2=判断 3=填空 4=简答")
    private Integer questionType;

    @Schema(description = "难度：1-5")
    private Integer difficulty;

    @Schema(description = "关键词搜索（题干）")
    private String keyword;
}
