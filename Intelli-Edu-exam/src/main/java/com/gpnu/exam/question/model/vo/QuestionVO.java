package com.gpnu.exam.question.model.vo;

import com.gpnu.exam.question.model.enums.Difficulty;
import com.gpnu.exam.question.model.enums.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Schema(description = "题目VO")
public class QuestionVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long questionId;
    private Long bankId;
    private QuestionType questionType;
    private String stem;
    private String analysis;
    private String answer;
    private BigDecimal score;
    private Difficulty difficulty;
    private List<QuestionOptionVO> options;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
