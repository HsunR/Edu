package com.gpnu.api.dto.knowledge;

import lombok.Data;

import java.io.Serializable;

@Data
public class PointSimpleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long pointId;
    private String pointName;
    private Long parentId;
    private Integer level;
}
