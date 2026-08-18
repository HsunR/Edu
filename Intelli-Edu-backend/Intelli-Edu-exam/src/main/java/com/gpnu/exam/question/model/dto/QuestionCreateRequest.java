package com.gpnu.exam.question.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "题目创建请求")
public class QuestionCreateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "题目类型不能为空")
    @Schema(description = "题目类型：0=单选 1=多选 2=判断 3=填空 4=简答")
    private Integer questionType;

    @NotBlank(message = "题干不能为空")
    @Schema(description = "题干")
    private String stem;

    @Schema(description = "解析")
    private String analysis;

    @Schema(description = "标准答案")
    private String answer;

    @NotNull(message = "分值不能为空")
    @Schema(description = "默认分值", example = "5.0")
    private BigDecimal score;

    @Schema(description = "难度：1-5", example = "3")
    private Integer difficulty;

    @Valid
    @Schema(description = "选项列表（单选/多选/判断题必填）")
    private List<QuestionOptionDTO> options;
}
