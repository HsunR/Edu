package com.gpnu.exam.paper.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "试卷详情VO（含题目列表）")
public class PaperDetailVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long paperId;
    private String paperName;
    private Long courseId;
    private Long teacherId;
    private BigDecimal totalScore;
    private List<Map<String, Object>> sections;
    private Integer status;
    private List<PaperQuestionVO> questions;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
