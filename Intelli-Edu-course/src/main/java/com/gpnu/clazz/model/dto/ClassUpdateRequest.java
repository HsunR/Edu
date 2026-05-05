package com.gpnu.clazz.model.dto;

import com.gpnu.clazz.model.enums.ClassStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Schema(description = "更新班级信息请求对象")
public class ClassUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;




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

    @Schema(description = "班级状态：0=招生中 1=进行中 2=已结束",example = "0")
    private ClassStatus status;

}
