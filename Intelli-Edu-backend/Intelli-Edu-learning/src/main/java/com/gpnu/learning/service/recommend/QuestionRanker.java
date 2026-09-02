package com.gpnu.learning.service.recommend;

import com.gpnu.api.dto.exam.QuestionSimpleDTO;
import com.gpnu.api.dto.knowledge.PointSimpleDTO;
import com.gpnu.common.service.RedisService;
import com.gpnu.learning.config.RecommendProperties;
import com.gpnu.learning.model.entity.LpWrongRecord;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * WMCLR 题目精排（文档公式 4～8）。
 * <pre>
 * Score(q) = w1*S_weak + w2*S_wrong + w3*S_diff + w4*S_new - w5*S_rep
 * </pre>
 */
@Component
public class QuestionRanker {

    private static final String REDIS_RECOMMEND_KEY = "learning:recommend:";

    @Resource
    private RecommendProperties recommendProperties;

    @Resource
    private RedisService redisService;

    @Resource
    private WeakPointDetector weakPointDetector;

    /**
     * 对候选题打分并降序排序。
     *
     * @param studentId      当前学生
     * @param questionIds    候选题 ID（来自知识点内容召回）
     * @param questionMap    题目元数据
     * @param questionPoints 题目关联知识点
     * @param weaknessByPoint 知识点薄弱度
     * @param wrongByQuestion 错题记录（可为空 map）
     */
    public List<RankedQuestion> rank(Long studentId,
                                     List<Long> questionIds,
                                     Map<Long, QuestionSimpleDTO> questionMap,
                                     Map<Long, List<PointSimpleDTO>> questionPoints,
                                     Map<Long, Double> weaknessByPoint,
                                     Map<Long, LpWrongRecord> wrongByQuestion) {
        if (questionIds == null || questionIds.isEmpty()) {
            return List.of();
        }

        List<RankedQuestion> ranked = new ArrayList<>();
        for (Long questionId : questionIds) {
            QuestionSimpleDTO question = questionMap.get(questionId);
            if (question == null) {
                continue;
            }
            List<PointSimpleDTO> points = questionPoints.getOrDefault(questionId, List.of());
            double sWeak = calcWeakAssoc(points, weaknessByPoint);
            LpWrongRecord wrong = wrongByQuestion.get(questionId);
            double sWrong = calcWrongUrgency(wrong);
            double sDiff = calcDifficultyMatch(question.getDifficulty(), points, weaknessByPoint);
            double sNew = calcNovelty(wrong);
            double sRep = calcRepeatPenalty(studentId, questionId);

            double score = recommendProperties.getWeightWeak() * sWeak
                    + recommendProperties.getWeightWrong() * sWrong
                    + recommendProperties.getWeightDiff() * sDiff
                    + recommendProperties.getWeightNew() * sNew
                    - recommendProperties.getWeightRepeat() * sRep;

            String reason = String.format(Locale.ROOT,
                    "薄弱%.2f×%.2f+错题%.2f×%.2f+难度%.2f×%.2f+新颖%.2f×%.2f",
                    sWeak, recommendProperties.getWeightWeak(),
                    sWrong, recommendProperties.getWeightWrong(),
                    sDiff, recommendProperties.getWeightDiff(),
                    sNew, recommendProperties.getWeightNew());

            ranked.add(new RankedQuestion(questionId, question, score, reason, sWeak, sWrong, sDiff, sNew, sRep));
        }

        ranked.sort(Comparator.comparingDouble(RankedQuestion::score).reversed());
        return ranked;
    }

    /** 记录本次推荐，用于 S_rep 重复惩罚 */
    public void markRecommended(Long studentId, Long questionId) {
        String key = REDIS_RECOMMEND_KEY + studentId + ":" + questionId;
        redisService.setCacheObject(key, "1", (long) recommendProperties.getRepeatPenaltyDays(), TimeUnit.DAYS);
    }

    /** S_weak：取题目关联知识点中最大薄弱度 */
    private double calcWeakAssoc(List<PointSimpleDTO> points, Map<Long, Double> weaknessByPoint) {
        double max = 0;
        for (PointSimpleDTO p : points) {
            if (p.getPointId() == null) {
                continue;
            }
            max = Math.max(max, weaknessByPoint.getOrDefault(p.getPointId(), 0.0));
        }
        return max;
    }

    /** S_wrong = min(1, Wq/5)；已解决则衰减 */
    private double calcWrongUrgency(LpWrongRecord wrong) {
        if (wrong == null) {
            return 0;
        }
        int count = wrong.getWrongCount() != null ? wrong.getWrongCount() : 0;
        double raw = Math.min(1.0, count / (double) recommendProperties.getWrongUrgencyCap());
        if (Integer.valueOf(1).equals(wrong.getIsResolved())) {
            return raw * recommendProperties.getResolvedWrongFactor();
        }
        return raw;
    }

    /** S_diff：目标难度 d_star 与题目难度 dq 的接近程度 */
    private double calcDifficultyMatch(Integer difficulty,
                                       List<PointSimpleDTO> points,
                                       Map<Long, Double> weaknessByPoint) {
        if (difficulty == null) {
            return 0.5;
        }
        double avgMastery = 50;
        if (!points.isEmpty()) {
            double sum = 0;
            int n = 0;
            for (PointSimpleDTO p : points) {
                if (p.getPointId() == null) {
                    continue;
                }
                double w = weaknessByPoint.getOrDefault(p.getPointId(), 0.5);
                sum += (1 - w) * 100;
                n++;
            }
            if (n > 0) {
                avgMastery = sum / n;
            }
        }
        int dStar = (int) Math.round(1 + 4 * (1 - avgMastery / 100.0));
        dStar = Math.max(1, Math.min(5, dStar));
        double diff = 1.0 - Math.abs(difficulty - dStar) / 4.0;
        return Math.max(0, Math.min(1, diff));
    }

    /** S_new：未错过且薄弱 → 1；在错题本 → 0.3/0.1 */
    private double calcNovelty(LpWrongRecord wrong) {
        if (wrong == null) {
            return 1.0;
        }
        if (Integer.valueOf(1).equals(wrong.getIsResolved())) {
            return 0.1;
        }
        return 0.3;
    }

    /** S_rep：近几天推荐过则 0.5 */
    private double calcRepeatPenalty(Long studentId, Long questionId) {
        String key = REDIS_RECOMMEND_KEY + studentId + ":" + questionId;
        return Boolean.TRUE.equals(redisService.hasKey(key)) ? 0.5 : 0;
    }

  public record RankedQuestion(Long questionId,
                                 QuestionSimpleDTO question,
                                 double score,
                                 String reason,
                                 double sWeak,
                                 double sWrong,
                                 double sDiff,
                                 double sNew,
                                 double sRep) {
    }
}
