package com.gpnu.exam.paper.model.vo;

import com.gpnu.exam.question.model.vo.QuestionVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Schema(description = "试卷题目VO")
public class PaperQuestionVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long paperId;
    private Long questionId;
    private Integer orderIndex;
    private BigDecimal score;
    private Integer sectionIndex;

    /** 草稿阶段为实时数据，发布后为快照 */
    private QuestionVO question;

    /** 原始快照数据（发布后非null） */
    private Map<String, Object> questionSnapshot;
}
