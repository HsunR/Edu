package com.gpnu.course.model.vo.course;

import com.gpnu.course.model.enums.CourseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@Schema(description = "课程信息VO")
public class CourseVO implements Serializable {

    private static final long serialVersionUID = 1L;


    Long    courseId;
    String  courseName;
    String  coverUrl;
    String  description;
    Long    teacherId;
    String  teacherName;
    String  teacherAvatar;
    Long    categoryId;
    String  categoryName;
    CourseStatus status;
    Integer isPublic;
    OffsetDateTime createdAt;
}
