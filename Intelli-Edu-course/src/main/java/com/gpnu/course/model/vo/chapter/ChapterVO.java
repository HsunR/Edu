package com.gpnu.course.model.vo.chapter;

import com.gpnu.course.model.vo.section.SectionVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ChapterVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long chapterId;

    private Long courseId;

    private String title;

    private Integer orderIndex;

    //该字段仅仅在 CourseDetailVO 使用
    private List<SectionVO> sections;
}
