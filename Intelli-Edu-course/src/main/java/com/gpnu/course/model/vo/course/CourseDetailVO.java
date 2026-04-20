package com.gpnu.course.model.vo.course;

import com.gpnu.course.model.vo.chapter.ChapterVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CourseDetailVO extends  CourseVO implements Serializable {
    private static final long serialVersionUID = 1L;


    List<ChapterVO> chapters;
}
