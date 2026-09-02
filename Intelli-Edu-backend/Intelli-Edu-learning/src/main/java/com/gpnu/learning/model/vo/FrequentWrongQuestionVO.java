package com.gpnu.learning.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "班级高频错题")
public class FrequentWrongQuestionVO implements Serializable {

    private Long questionId;
    private Integer questionType;
    @Schema(description = "难度 1-5")
    private Integer difficulty;
    @Schema(description = "题干摘要")
    private String stem;
    private Integer wrongStudentCount;
    private Long totalWrongTimes;
}
