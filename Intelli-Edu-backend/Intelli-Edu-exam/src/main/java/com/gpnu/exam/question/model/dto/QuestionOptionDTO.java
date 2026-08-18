package com.gpnu.exam.question.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "题目选项DTO")
public class QuestionOptionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "选项标号不能为空")
    @Schema(description = "选项标号", example = "A")
    private String label;

    @NotBlank(message = "选项内容不能为空")
    @Schema(description = "选项内容")
    private String content;

    @Schema(description = "是否正确答案", example = "false")
    private Boolean isCorrect;

    @Schema(description = "排序", example = "0")
    private Integer orderIndex;
}
