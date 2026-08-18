package com.gpnu.exam.exam.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@Schema(description = "考试创建请求")
public class ExamCreateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "考试名称不能为空")
    @Size(max = 200)
    @Schema(description = "考试名称")
    private String examName;

    @NotNull(message = "试卷ID不能为空")
    @Schema(description = "试卷ID")
    private Long paperId;

    @NotNull(message = "班级ID不能为空")
    @Schema(description = "班级ID")
    private Long classId;

    @NotNull(message = "考试类型不能为空")
    @Schema(description = "类型：0=考试 1=练习 2=作业")
    private Integer examType;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开放窗口开始时间")
    private OffsetDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "开放窗口结束时间")
    private OffsetDateTime endTime;

    @Schema(description = "答题时长（分钟），null=不限时")
    private Integer durationMinutes;

    @Schema(description = "是否允许迟交", example = "false")
    private Boolean allowLateSubmit;
}
