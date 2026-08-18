package com.gpnu.exam.exam.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@Schema(description = "考试更新请求")
public class ExamUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Size(max = 200)
    @Schema(description = "考试名称")
    private String examName;

    @Schema(description = "开放窗口开始时间")
    private OffsetDateTime startTime;

    @Schema(description = "开放窗口结束时间")
    private OffsetDateTime endTime;

    @Schema(description = "答题时长（分钟）")
    private Integer durationMinutes;

    @Schema(description = "是否允许迟交")
    private Boolean allowLateSubmit;
}
