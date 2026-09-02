package com.gpnu.learning.service.recommend;

import lombok.Builder;
import lombok.Data;

/**
 * 薄弱知识点识别结果项（公式 1：Weak(p)）。
 */
@Data
@Builder
public class WeakPointItem {

    private Long pointId;
    private String pointName;
    /** 原始掌握度 0-100，无记录可为 null */
    private Integer masteryLevel;
    /** 薄弱度 0-1，越大越薄弱 */
    private double weakness;
}
