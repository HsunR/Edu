package com.gpnu.learning.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@TableName("lp_sheet_graded_log")
@Data
public class LpSheetGradedLog implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long logId;

    private Long sheetId;

    private Integer submitCount;

    private Long studentId;

    private Long classId;

    private Long courseId;

    /** JSON 格式的 SheetContributionSnapshot */
    private String contributionJson;

    @TableLogic
    private Integer isDeleted;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
