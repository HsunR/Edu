package com.gpnu.exam.exam.model.dto;

import com.gpnu.common.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "考试查询请求")
public class ExamQueryRequest extends PageRequest {

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "考试类型：0=考试 1=练习 2=作业")
    private Integer examType;

    @Schema(description = "状态：0=未开始 1=进行中 2=已结束 3=已批阅完成")
    private Integer status;

    @Schema(description = "关键词")
    private String keyword;
}
