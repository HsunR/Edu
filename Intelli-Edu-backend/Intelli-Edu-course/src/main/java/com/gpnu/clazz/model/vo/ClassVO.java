package com.gpnu.clazz.model.vo;

import com.gpnu.clazz.model.enums.ClassStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class ClassVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long classId;

    private Long courseId;

    private String courseName;

    private String className;

    private Long teacherId;

    private String teacherName;

    //仅教师可见，学生端不显示
    private String inviteCode;

    private Integer maxStudents;

    //当前学生人数，动态统计
    private Integer currentStudents;

    private LocalDate startDate;

    private LocalDate endDate;

    private ClassStatus status;

    private OffsetDateTime createdAt;

}
