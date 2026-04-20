package com.gpnu.course.model.dto.course;

import com.gpnu.common.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "课程查询请求对象，包含分页信息和过滤条件")
public class CourseQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "课程分类，根据课程分类Id过滤")
    private Long categoryId;

    @Schema(description = "课程名称，根据课程名称模糊匹配过滤")
    private String courseName;

    @Schema(description = "课程状态，根据课程状态过滤，0：草稿，1：已发布，2：已归档")
    private Integer status;
}
