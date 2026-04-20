package com.gpnu.clazz.model.vo;

import lombok.Data;

import java.io.Serializable;
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

    private OffsetDateTime startDate;

    private OffsetDateTime endDate;

    private Integer status;

    private OffsetDateTime createdAt;

}
