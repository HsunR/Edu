package com.gpnu.api.dto.section;

import lombok.Data;

import java.io.Serializable;

@Data
public class SectionSimpleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long sectionId;
    private Long courseId;
    private String title;
}
