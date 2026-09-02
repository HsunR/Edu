package com.gpnu.learning.service.recommend;

import com.gpnu.api.dto.knowledge.PointSimpleDTO;
import com.gpnu.learning.config.LearningProperties;
import com.gpnu.learning.model.entity.LpMastery;
import com.gpnu.learning.service.LearningQuerySupport;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 薄弱知识点识别（文档公式 1）。
 * <pre>
 * Weak(p) = (1 - Mp/100) × min(1, Cp/Cmin)
 * </pre>
 */
@Component
public class WeakPointDetector {

    @Resource
    private LearningQuerySupport learningQuerySupport;

    @Resource
    private LearningProperties learningProperties;

    /**
     * 识别学生在某班下的薄弱知识点，按 weakness 降序。
     *
     * @param topK 最多返回条数；<=0 表示不限制
     */
    public List<WeakPointItem> detectWeakPoints(Long studentId, Long classId, int topK) {
        List<LpMastery> masteries = learningQuerySupport.listByStudentAndClass(studentId, classId);
        int cMin = Math.max(1, learningProperties.getWeakConfidenceMinAnswers());

        List<Long> pointIds = masteries.stream().map(LpMastery::getPointId).toList();
        Map<Long, PointSimpleDTO> pointMap = learningQuerySupport.loadPointMap(pointIds);

        List<WeakPointItem> items = masteries.stream()
                .map(m -> toWeakItem(m, cMin, pointMap))
                .sorted(Comparator.comparingDouble(WeakPointItem::getWeakness).reversed())
                .collect(Collectors.toList());

        // 仅保留「低于阈值」或 weakness 明显 > 0 的点
        int threshold = learningProperties.getWeakMasteryThreshold();
        items = items.stream()
                .filter(item -> item.getMasteryLevel() == null
                        || item.getMasteryLevel() < threshold
                        || item.getWeakness() >= (1 - threshold / 100.0) * 0.5)
                .collect(Collectors.toList());

        if (topK > 0 && items.size() > topK) {
            return items.subList(0, topK);
        }
        return items;
    }

    /**
     * 计算单个知识点的薄弱度。
     */
    public double calcWeakness(int masteryLevel, int answerCount, int cMin) {
        double gap = 1.0 - masteryLevel / 100.0;
        double confidence = Math.min(1.0, answerCount / (double) cMin);
        return gap * confidence;
    }

    private WeakPointItem toWeakItem(LpMastery mastery, int cMin, Map<Long, PointSimpleDTO> pointMap) {
        int mp = mastery.getMasteryLevel() != null ? mastery.getMasteryLevel() : 0;
        int cp = mastery.getAnswerCount() != null ? mastery.getAnswerCount() : 0;
        double weakness = calcWeakness(mp, cp, cMin);

        PointSimpleDTO point = pointMap.get(mastery.getPointId());
        return WeakPointItem.builder()
                .pointId(mastery.getPointId())
                .pointName(point != null ? point.getPointName() : null)
                .masteryLevel(mp)
                .weakness(weakness)
                .build();
    }
}