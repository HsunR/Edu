package com.gpnu.exam.question.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "选项VO")
public class QuestionOptionVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long optionId;
    private String label;
    private String content;
    private Boolean isCorrect;
    private Integer orderIndex;
}
