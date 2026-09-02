package com.gpnu.learning.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@TableName("lp_wrong_point")
@Data
public class LpWrongPoint implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long wrongId;

    private Long studentId;

    private Long classId;

    private Long courseId;

    private Long pointId;

    @TableLogic
    private Integer isDeleted;

    private OffsetDateTime createdAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
