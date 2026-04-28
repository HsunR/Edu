package com.gpnu.exam.question.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@Schema(description = "题库VO")
public class QuestionBankVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long bankId;
    private String bankName;
    private Long courseId;
    private Long teacherId;
    private String description;
    private Integer questionCount;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
