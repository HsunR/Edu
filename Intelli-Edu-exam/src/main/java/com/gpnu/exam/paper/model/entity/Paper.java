package com.gpnu.exam.paper.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.gpnu.exam.paper.model.enums.PaperStatus;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@TableName(value = "ex_paper", autoResultMap = true)
@Data
public class Paper implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long paperId;

    private String paperName;

    private Long courseId;

    private Long teacherId;

    private BigDecimal totalScore;

    /**
     * 分节标题 [{"index":1,"title":"选择题"},...]
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Map<String, Object>> sections;

    private PaperStatus status;

    @Version
    private Integer version;

    @TableLogic
    private Integer isDeleted;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
