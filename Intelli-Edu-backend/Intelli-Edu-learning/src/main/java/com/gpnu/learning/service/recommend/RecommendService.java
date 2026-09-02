package com.gpnu.learning.service.recommend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gpnu.api.client.course.CourseFeignClient;
import com.gpnu.api.client.exam.ExamFeignClient;
import com.gpnu.api.client.knowledge.KnowledgeFeignClient;
import com.gpnu.api.dto.exam.QuestionSimpleDTO;
import com.gpnu.api.dto.knowledge.PointSimpleDTO;
import com.gpnu.api.dto.section.SectionSimpleDTO;
import com.gpnu.learning.config.LearningProperties;
import com.gpnu.learning.config.RecommendProperties;
import com.gpnu.learning.mapper.LpMasteryMapper;
import com.gpnu.learning.mapper.LpWrongRecordMapper;
import com.gpnu.learning.model.entity.LpMastery;
import com.gpnu.learning.model.entity.LpWrongRecord;
import com.gpnu.learning.model.enums.RecommendScene;
import com.gpnu.learning.model.vo.*;
import com.gpnu.learning.service.ClassAccessService;
import com.gpnu.learning.service.LearningQuerySupport;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 个性化推荐编排服务（F5）：WMCLR 算法 + Feign 富化。
 * <p>
 * 职责边界：
 * <ul>
 *   <li>本类决定「推什么」（知识点/题目/章节）</li>
 *   <li>AI 模块仅消费结果生成话术，不参与排序</li>
 * </ul>
 */
@Service
public class RecommendService {

    @Resource
    private ClassAccessService classAccessService;

    @Resource
    private WeakPointDetector weakPointDetector;

    @Resource
    private QuestionRanker questionRanker;

    @Resource
    private LearningQuerySupport learningQuerySupport;

    @Resource
    private LpWrongRecordMapper lpWrongRecordMapper;

    @Resource
    private LpMasteryMapper lpMasteryMapper;

    @Resource
    private KnowledgeFeignClient knowledgeFeignClient;

    @Resource
    private ExamFeignClient examFeignClient;

    @Resource
    private CourseFeignClient courseFeignClient;

    @Resource
    private LearningProperties learningProperties;

    @Resource
    private RecommendProperties recommendProperties;

    /**
     * 学生端推荐：基于个人 lp_mastery + lp_wrong_record。
     */
    public RecommendResultVO recommendForStudent(Long studentId, Long classId, Long courseId,
                                                 RecommendScene scene, Integer limit) {
        classAccessService.validateStudentMember(studentId, classId);
        int topLimit = resolveLimit(limit);

        List<WeakPointItem> weakPoints = weakPointDetector.detectWeakPoints(studentId, classId, 10);
        Map<Long, Double> weaknessByPoint = buildWeaknessMap(studentId, classId);
        Map<Long, LpWrongRecord> wrongByQuestion = loadWrongMap(studentId, classId);

        RecommendResultVO result = new RecommendResultVO();
        result.setScene(scene.getCode());

        // 1. 推荐知识点（薄弱点 Top）
        int pointLimit = Math.min(5, topLimit);
        for (int i = 0; i < Math.min(pointLimit, weakPoints.size()); i++) {
            WeakPointItem item = weakPoints.get(i);
            RecommendPointVO vo = new RecommendPointVO();
            vo.setPointId(item.getPointId());
            vo.setPointName(item.getPointName());
            vo.setMasteryLevel(item.getMasteryLevel());
            vo.setReason(String.format(Locale.ROOT, "薄弱度%.2f，掌握度%d分",
                    item.getWeakness(), item.getMasteryLevel() != null ? item.getMasteryLevel() : 0));
            result.getPoints().add(vo);
        }

        // 2. 内容召回 + WMCLR 精排
        List<Long> candidateIds = collectCandidateQuestionIds(scene, weakPoints, wrongByQuestion);
        List<QuestionRanker.RankedQuestion> ranked = rankQuestions(
                studentId, candidateIds, weaknessByPoint, wrongByQuestion);

        int questionLimit = scene == RecommendScene.DAILY_PLAN ? Math.min(3, topLimit) : topLimit;
        for (int i = 0; i < Math.min(questionLimit, ranked.size()); i++) {
            QuestionRanker.RankedQuestion rq = ranked.get(i);
            RecommendQuestionVO qvo = new RecommendQuestionVO();
            qvo.setQuestionId(rq.questionId());
            if (rq.question() != null) {
                qvo.setQuestionType(rq.question().getQuestionType());
                qvo.setStem(rq.question().getStem());
            }
            qvo.setScore(rq.score());
            qvo.setReason(rq.reason());
            result.getQuestions().add(qvo);
            questionRanker.markRecommended(studentId, rq.questionId());
        }

        // 3. 推荐关联章节（薄弱点 → kn_knowledge_section → 节标题）
        result.getSections().addAll(buildSectionRecommends(weakPoints, Math.min(3, topLimit)));

        result.setSummary(buildStudentSummary(weakPoints, result));
        return result;
    }

    /**
     * 教师端教学干预推荐：基于班级聚合 lp_mastery / lp_wrong_record（共用同表，不同 SQL）。
     */
    public RecommendResultVO recommendForTeacher(Long teacherId, Long classId, Integer limit) {
        classAccessService.validateClassTeacher(teacherId, classId);
        int topLimit = resolveLimit(limit);

        RecommendResultVO result = new RecommendResultVO();
        result.setScene(RecommendScene.TEACHER_INTERVENTION.getCode());

        // 班级薄弱知识点：AVG(mastery_level) 最低者优先
        List<ClassMasteryPointVO> classPoints = lpMasteryMapper.statClassMastery(
                classId, learningProperties.getWeakMasteryThreshold());
        List<Long> pointIds = classPoints.stream().map(ClassMasteryPointVO::getPointId).toList();
        Map<Long, PointSimpleDTO> pointMap = loadPointMap(pointIds);

        int pointLimit = Math.min(5, topLimit);
        for (int i = 0; i < Math.min(pointLimit, classPoints.size()); i++) {
            ClassMasteryPointVO cp = classPoints.get(i);
            PointSimpleDTO point = pointMap.get(cp.getPointId());
            RecommendPointVO vo = new RecommendPointVO();
            vo.setPointId(cp.getPointId());
            vo.setPointName(point != null ? point.getPointName() : null);
            if (cp.getAvgMasteryLevel() != null) {
                vo.setMasteryLevel(cp.getAvgMasteryLevel().intValue());
            }
            vo.setReason(String.format(Locale.ROOT, "全班平均掌握度%s，%d人薄弱",
                    cp.getAvgMasteryLevel(), cp.getWeakStudentCount() != null ? cp.getWeakStudentCount() : 0));
            result.getPoints().add(vo);
        }

        // 班级高频错题
        List<FrequentWrongQuestionVO> frequent = lpWrongRecordMapper.statFrequentQuestions(classId, topLimit);
        for (FrequentWrongQuestionVO fw : frequent) {
            RecommendQuestionVO qvo = new RecommendQuestionVO();
            qvo.setQuestionId(fw.getQuestionId());
            qvo.setQuestionType(fw.getQuestionType());
            qvo.setReason(String.format(Locale.ROOT, "%d名学生错过，累计错%d次",
                    fw.getWrongStudentCount(), fw.getTotalWrongTimes()));
            result.getQuestions().add(qvo);
        }

        // 班薄弱点关联章节
        List<WeakPointItem> weakItems = classPoints.stream().limit(3).map(cp -> {
            PointSimpleDTO p = pointMap.get(cp.getPointId());
            return WeakPointItem.builder()
                    .pointId(cp.getPointId())
                    .pointName(p != null ? p.getPointName() : null)
                    .masteryLevel(cp.getAvgMasteryLevel() != null ? cp.getAvgMasteryLevel().intValue() : 0)
                    .weakness(1 - (cp.getAvgMasteryLevel() != null ? cp.getAvgMasteryLevel().doubleValue() / 100 : 0))
                    .build();
        }).toList();
        result.getSections().addAll(buildSectionRecommends(weakItems, 3));

        result.setSummary(buildTeacherSummary(classPoints, frequent));
        return result;
    }

    private int resolveLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return recommendProperties.getDefaultLimit();
        }
        return limit;
    }

    /** 构建 pointId → weakness，供 WMCLR 使用 */
    private Map<Long, Double> buildWeaknessMap(Long studentId, Long classId) {
        int cMin = Math.max(1, learningProperties.getWeakConfidenceMinAnswers());
        List<LpMastery> masteries = learningQuerySupport.listByStudentAndClass(studentId, classId);
        Map<Long, Double> map = new HashMap<>();
        for (LpMastery m : masteries) {
            int mp = m.getMasteryLevel() != null ? m.getMasteryLevel() : 0;
            int cp = m.getAnswerCount() != null ? m.getAnswerCount() : 0;
            map.put(m.getPointId(), weakPointDetector.calcWeakness(mp, cp, cMin));
        }
        return map;
    }

    private Map<Long, LpWrongRecord> loadWrongMap(Long studentId, Long classId) {
        List<LpWrongRecord> list = lpWrongRecordMapper.selectList(new LambdaQueryWrapper<LpWrongRecord>()
                .eq(LpWrongRecord::getStudentId, studentId)
                .eq(LpWrongRecord::getClassId, classId));
        return list.stream().collect(Collectors.toMap(LpWrongRecord::getQuestionId, Function.identity(), (a, b) -> a));
    }

    /**
     * 基于场景从 kn_question_knowledge 召回候选题 ID（内容召回，公式 2～3）。
     */
    private List<Long> collectCandidateQuestionIds(RecommendScene scene,
                                                   List<WeakPointItem> weakPoints,
                                                   Map<Long, LpWrongRecord> wrongByQuestion) {
        Set<Long> ids = new LinkedHashSet<>();

        if (scene == RecommendScene.REVIEW_WRONG || scene == RecommendScene.DAILY_PLAN) {
            wrongByQuestion.values().stream()
                    .filter(w -> !Integer.valueOf(1).equals(w.getIsResolved()))
                    .map(LpWrongRecord::getQuestionId)
                    .forEach(ids::add);
        }

        if (scene == RecommendScene.REVIEW_WEAK || scene == RecommendScene.DAILY_PLAN || ids.isEmpty()) {
            for (WeakPointItem item : weakPoints) {
                List<Long> qIds = knowledgeFeignClient.getQuestionIdsByPoint(item.getPointId());
                if (!CollectionUtils.isEmpty(qIds)) {
                    ids.addAll(qIds);
                }
            }
        }

        return new ArrayList<>(ids);
    }

    private List<QuestionRanker.RankedQuestion> rankQuestions(Long studentId,
                                                              List<Long> candidateIds,
                                                              Map<Long, Double> weaknessByPoint,
                                                              Map<Long, LpWrongRecord> wrongByQuestion) {
        if (candidateIds.isEmpty()) {
            return List.of();
        }
        List<QuestionSimpleDTO> questions = examFeignClient.getQuestionBatch(candidateIds);
        Map<Long, QuestionSimpleDTO> questionMap = questions.stream()
                .filter(q -> q.getQuestionId() != null)
                .collect(Collectors.toMap(QuestionSimpleDTO::getQuestionId, Function.identity(), (a, b) -> a));

        Map<Long, List<PointSimpleDTO>> questionPoints = knowledgeFeignClient.getPointsByQuestions(candidateIds);
        if (questionPoints == null) {
            questionPoints = Map.of();
        }

        return questionRanker.rank(studentId, candidateIds, questionMap, questionPoints,
                weaknessByPoint, wrongByQuestion);
    }

    private List<RecommendSectionVO> buildSectionRecommends(List<WeakPointItem> weakPoints, int limit) {
        List<RecommendSectionVO> sections = new ArrayList<>();
        for (WeakPointItem item : weakPoints) {
            if (sections.size() >= limit) {
                break;
            }
            List<Long> sectionIds = knowledgeFeignClient.getSectionIdsByPoint(item.getPointId());
            if (CollectionUtils.isEmpty(sectionIds)) {
                continue;
            }
            Long sectionId = sectionIds.get(0);
            SectionSimpleDTO section = courseFeignClient.getSectionSimple(sectionId);
            RecommendSectionVO svo = new RecommendSectionVO();
            svo.setSectionId(sectionId);
            svo.setPointId(item.getPointId());
            svo.setPointName(item.getPointName());
            if (section != null) {
                svo.setSectionTitle(section.getTitle());
            }
            svo.setReason("薄弱知识点「" + item.getPointName() + "」关联章节，建议优先复习");
            sections.add(svo);
        }
        return sections;
    }

    private Map<Long, PointSimpleDTO> loadPointMap(List<Long> pointIds) {
        if (CollectionUtils.isEmpty(pointIds)) {
            return Map.of();
        }
        List<PointSimpleDTO> list = knowledgeFeignClient.getPointBatch(pointIds);
        if (CollectionUtils.isEmpty(list)) {
            return Map.of();
        }
        return list.stream()
                .filter(p -> p.getPointId() != null)
                .collect(Collectors.toMap(PointSimpleDTO::getPointId, Function.identity(), (a, b) -> a));
    }

    private String buildStudentSummary(List<WeakPointItem> weakPoints, RecommendResultVO result) {
        if (weakPoints.isEmpty()) {
            return "暂无薄弱知识点，继续保持！";
        }
        String first = weakPoints.get(0).getPointName() != null ? weakPoints.get(0).getPointName() : "知识点";
        return String.format(Locale.ROOT, "你有 %d 个薄弱知识点，推荐 %d 道题、%d 个章节；建议优先复习「%s」",
                weakPoints.size(), result.getQuestions().size(), result.getSections().size(), first);
    }

    private String buildTeacherSummary(List<ClassMasteryPointVO> classPoints, List<FrequentWrongQuestionVO> frequent) {
        if (classPoints.isEmpty() && frequent.isEmpty()) {
            return "班级学情良好，暂无突出薄弱点。";
        }
        return String.format(Locale.ROOT, "班级共 %d 个知识点有掌握度记录，高频错题 %d 道，建议结合推荐章节讲评。",
                classPoints.size(), frequent.size());
    }
}
