package com.gpnu.exam.paper.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@TableName(value = "ex_paper_question", autoResultMap = true)
@Data
public class PaperQuestion implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long paperId;

    private Long questionId;

    private Integer orderIndex;

    private BigDecimal score;

    private Integer sectionIndex;

    /**
     * 题目快照（试卷发布时冻结）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> questionSnapshot;

    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
