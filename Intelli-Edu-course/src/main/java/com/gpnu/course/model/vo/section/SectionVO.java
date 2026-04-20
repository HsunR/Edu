package com.gpnu.course.model.vo.section;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SectionVO  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long sectionId;

    private Long chapterId;

    private String title;

    private Integer orderIndex;

    private Integer isFree;

    //仅在目录树内嵌时填充
    private List<SectionResourceVO> resources;


}
