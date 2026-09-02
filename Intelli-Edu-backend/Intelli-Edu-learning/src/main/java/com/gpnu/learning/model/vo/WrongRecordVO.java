package com.gpnu.learning.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "错题记录")
public class WrongRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "错题记录ID")
    private Long wrongId;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "来源考试ID")
    private Long examId;

    @Schema(description = "题型：0单选 1多选 2判断 3填空 4简答")
    private Integer questionType;

    @Schema(description = "该题满分")
    private BigDecimal fullScore;

    @Schema(description = "该题得分")
    private BigDecimal earnedScore;

    @Schema(description = "AI错误类型")
    private String wrongType;

    @Schema(description = "是否已解决：0否 1是")
    private Integer isResolved;

    @Schema(description = "标记解决时间")
    private OffsetDateTime resolvedAt;

    @Schema(description = "累计答错次数")
    private Integer wrongCount;

    @Schema(description = "最近答错时间")
    private OffsetDateTime lastWrongAt;

    @Schema(description = "关联知识点")
    private List<WrongPointBriefVO> points = new ArrayList<>();
}
