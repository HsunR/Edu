package com.gpnu.clazz.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "创建班级请求对象")
public class ClassCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;


    @NotNull
    @Schema(description = "课程id", example = "123")
    private  Long  courseId;

    @NotNull
    @Size(max = 50)
    @Schema(description = "班级名称",example = "2024级软件工程1班")
    private String className;


    @Schema(description = "班级学生人数上限",example = "100")
    private Integer maxStudents;


    @Schema(description = "开课日期",example = "2024-09-01")
    private LocalDate startDate;

    @Schema(description = "结课日期",example = "2025-06-30")
    private LocalDate endDate;
}
