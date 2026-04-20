package com.gpnu.course.model.vo.category;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CategoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long categoryId;

    private String name;

    private Long   parentId;

    private Integer orderIndex;

    private List<CategoryVO> children;

}
