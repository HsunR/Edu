package com.gpnu.api.dto.course;

import lombok.Data;

import java.io.Serializable;

@Data
public class CourseSimpleDTO  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long courseId;
    private String courseName;
    private String courseCover;
    private Long   teacherId;
    private Integer status;

}
