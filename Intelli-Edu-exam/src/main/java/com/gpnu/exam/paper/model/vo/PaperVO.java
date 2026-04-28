package com.gpnu.exam.paper.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "试卷VO")
public class PaperVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long paperId;
    private String paperName;
    private Long courseId;
    private Long teacherId;
    private BigDecimal totalScore;
    private List<Map<String, Object>> sections;
    private Integer status;
    private Integer questionCount;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
