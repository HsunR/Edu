package com.gpnu.exam.question.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "题目更新请求")
public class QuestionUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "题干")
    private String stem;

    @Schema(description = "解析")
    private String analysis;

    @Schema(description = "标准答案")
    private String answer;

    @Schema(description = "默认分值")
    private BigDecimal score;

    @Schema(description = "难度：1-5")
    private Integer difficulty;

    @Valid
    @Schema(description = "选项列表（传入时全量替换）")
    private List<QuestionOptionDTO> options;
}
