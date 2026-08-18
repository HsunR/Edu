package com.gpnu.exam.paper.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "试卷添加题目请求")
public class PaperQuestionAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "题目列表不能为空")
    @Schema(description = "要添加的题目列表")
    private List<QuestionItem> questions;

    @Data
    @Schema(description = "单个题目项")
    public static class QuestionItem {
        @NotNull
        @Schema(description = "题目ID")
        private Long questionId;

        @NotNull
        @Schema(description = "在本卷中的分值")
        private BigDecimal score;

        @Schema(description = "归属节号", example = "1")
        private Integer sectionIndex;
    }
}
