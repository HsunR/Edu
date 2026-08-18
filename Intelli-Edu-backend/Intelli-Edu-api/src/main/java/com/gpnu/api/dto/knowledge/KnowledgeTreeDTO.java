package com.gpnu.api.dto.knowledge;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class KnowledgeTreeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long pointId;
    private String pointName;
    private Long courseId;
    private Long parentId;
    private String description;
    private Integer orderIndex;
    private List<KnowledgeTreeDTO> children;
}
