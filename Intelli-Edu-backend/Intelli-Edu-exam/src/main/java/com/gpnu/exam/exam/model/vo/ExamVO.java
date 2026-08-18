package com.gpnu.exam.exam.model.vo;

import com.gpnu.exam.exam.model.enums.ExamStatus;
import com.gpnu.exam.exam.model.enums.ExamType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@Schema(description = "考试VO")
public class ExamVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long examId;
    private String examName;
    private Long paperId;
    private String paperName;
    private Long classId;
    private Long courseId;
    private Long teacherId;
    private ExamType examType;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Integer durationMinutes;
    private Boolean allowLateSubmit;
    private ExamStatus status;
    private OffsetDateTime createdAt;
}
