package com.gpnu.learning.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程图谱节点：知识点 + 个人掌握度（F2）。
 */
@Data
@Schema(description = "图谱知识点节点")
public class GraphPointVO implements Serializable {

    private Long pointId;
    private String pointName;
    private Long parentId;
    private String description;
    private Integer orderIndex;

    @Schema(description = "掌握度 0-100；无记录时为 null 表示未练习")
    private Integer masteryLevel;

    @Schema(description = "是否薄弱点（低于阈值）")
    private Boolean isWeak;

    @Schema(description = "关联章节 ID 列表")
    private List<Long> sectionIds = new ArrayList<>();

    @Schema(description = "子知识点（二级）")
    private List<GraphPointVO> childPoints = new ArrayList<>();
}
