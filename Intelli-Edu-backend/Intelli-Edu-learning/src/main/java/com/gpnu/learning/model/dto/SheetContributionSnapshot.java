package com.gpnu.learning.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 单次答卷批改对 lp_ 表的贡献快照，用于重复提交时回滚。
 */
@Data
public class SheetContributionSnapshot {

    private List<MasteryContribution> masteries = new ArrayList<>();

    private List<WrongContribution> wrongs = new ArrayList<>();

    @Data
    public static class MasteryContribution {

        private Long pointId;

        private BigDecimal totalScoreDelta = BigDecimal.ZERO;

        private BigDecimal earnedScoreDelta = BigDecimal.ZERO;

        private int answerCountDelta;

        private int correctCountDelta;
    }

    @Data
    public static class WrongContribution {

        private Long questionId;

        private int wrongCountDelta;

        private Long wrongId;

        private List<Long> pointIds = new ArrayList<>();
    }
}
