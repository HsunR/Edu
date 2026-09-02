package com.gpnu.learning.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@Schema(description = "某题答错学生摘要")
public class WrongStudentBriefVO implements Serializable {

    private Long studentId;
    private String studentName;
    @Schema(description = "头像URL")
    private String avatarUrl;
    private Integer wrongCount;
    private String wrongType;
    private Integer isResolved;
    private OffsetDateTime lastWrongAt;
}
