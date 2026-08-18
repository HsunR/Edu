package com.gpnu.exam.exam.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gpnu.exam.exam.model.enums.ExamStatus;
import com.gpnu.exam.exam.model.enums.ExamType;
import lombok.Data;
import java.io.Serializable;
import java.time.OffsetDateTime;

@TableName("ex_exam")
@Data
public class Exam implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long examId;

    private String examName;

    private Long paperId;

    private Long classId;

    private Long courseId;

    private Long teacherId;

    private ExamType examType;

    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    /**
     * 答题时长（分钟），null=不限时
     */
    private Integer durationMinutes;

    private Boolean allowLateSubmit;

    private ExamStatus status;

    @Version
    private Integer version;

    @TableLogic
    private Integer isDeleted;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
