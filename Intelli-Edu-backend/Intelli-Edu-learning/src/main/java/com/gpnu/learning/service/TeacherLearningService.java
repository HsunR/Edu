package com.gpnu.learning.service;

import com.gpnu.api.client.exam.ExamFeignClient;
import com.gpnu.api.client.user.UserFeignClient;
import com.gpnu.api.dto.exam.QuestionSimpleDTO;
import com.gpnu.api.dto.knowledge.PointSimpleDTO;
import com.gpnu.api.dto.user.UserSimpleDTO;
import com.gpnu.learning.config.LearningProperties;
import com.gpnu.learning.mapper.LpMasteryMapper;
import com.gpnu.learning.mapper.LpWrongRecordMapper;
import com.gpnu.learning.model.vo.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 教师端学情查询（F4）：与学生端共用 lp_mastery / lp_wrong_record，按 class_id 聚合。
 */
@Service
public class TeacherLearningService {

    @Resource
    private ClassAccessService classAccessService;

    @Resource
    private LpMasteryMapper lpMasteryMapper;

    @Resource
    private LpWrongRecordMapper lpWrongRecordMapper;

    @Resource
    private MasteryQueryService masteryQueryService;

    @Resource
    private LearningEnrichmentService learningEnrichmentService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private LearningProperties learningProperties;

    @Resource
    private ExamFeignClient examFeignClient;

    public List<ClassMasteryPointVO> getClassMasteryOverview(Long teacherId, Long classId) {
        classAccessService.validateClassTeacher(teacherId, classId);
        List<ClassMasteryPointVO> list = lpMasteryMapper.statClassMastery(
                classId, learningProperties.getWeakMasteryThreshold());
        enrichPointNames(list);
        return list;
    }

    public List<MasteryOverviewVO> getStudentMastery(Long teacherId, Long classId, Long studentId) {
        classAccessService.validateClassTeacher(teacherId, classId);
        // 复用学生视角查询逻辑，教师指定 studentId
        return masteryQueryService.listOverview(studentId, classId);
    }

    public List<FrequentWrongQuestionVO> getFrequentWrongs(Long teacherId, Long classId, Integer limit) {
        classAccessService.validateClassTeacher(teacherId, classId);
        int top = limit != null && limit > 0 ? limit : 20;
        List<FrequentWrongQuestionVO> list = lpWrongRecordMapper.statFrequentQuestions(classId, top);
        enrichQuestionInfo(list);
        return list;
    }

    public WrongStatsVO getClassWrongPointDistribution(Long teacherId, Long classId) {
        classAccessService.validateClassTeacher(teacherId, classId);
        List<WrongPointStatVO> byPoint = lpWrongRecordMapper.statClassWrongByPoint(classId);
        List<Long> pointIds = byPoint.stream().map(WrongPointStatVO::getPointId).filter(Objects::nonNull).toList();
        Map<Long, PointSimpleDTO> pointMap = learningEnrichmentService.loadPointMap(pointIds);
        byPoint.forEach(item -> {
            PointSimpleDTO p = pointMap.get(item.getPointId());
            if (p != null) {
                item.setPointName(p.getPointName());
            }
        });
        WrongStatsVO stats = new WrongStatsVO();
        stats.setByKnowledgePoint(byPoint);
        return stats;
    }

    public WrongQuestionDetailVO getQuestionWrongDetail(Long teacherId, Long classId, Long questionId) {
        classAccessService.validateClassTeacher(teacherId, classId);
        return getQuestionWrongDetailWithoutAuth(classId, questionId);
    }

    public WrongQuestionDetailVO getQuestionWrongDetailWithoutAuth(Long classId, Long questionId) {
        WrongQuestionDetailVO detail = new WrongQuestionDetailVO();
        detail.setClassId(classId);
        detail.setQuestionId(questionId);

        // 补题目信息
        List<QuestionSimpleDTO> questions = examFeignClient.getQuestionBatch(
                Collections.singletonList(questionId));
        if (!CollectionUtils.isEmpty(questions)) {
            QuestionSimpleDTO q = questions.get(0);
            detail.setQuestionType(q.getQuestionType());
            detail.setDifficulty(q.getDifficulty());
            detail.setStem(q.getStem());
        }

        List<WrongStudentBriefVO> students = lpWrongRecordMapper.listWrongStudentsByQuestion(classId, questionId);
        enrichStudentNames(students);
        detail.setStudents(students);

        detail.setWrongTypeDistribution(lpWrongRecordMapper.statWrongTypeByQuestion(classId, questionId));
        return detail;
    }

    private void enrichPointNames(List<ClassMasteryPointVO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<Long> pointIds = list.stream().map(ClassMasteryPointVO::getPointId).toList();
        Map<Long, PointSimpleDTO> pointMap = learningEnrichmentService.loadPointMap(pointIds);
        list.forEach(item -> {
            PointSimpleDTO p = pointMap.get(item.getPointId());
            if (p != null) {
                item.setPointName(p.getPointName());
            }
        });
    }

    private void enrichStudentNames(List<WrongStudentBriefVO> students) {
        if (CollectionUtils.isEmpty(students)) {
            return;
        }
        List<Long> userIds = students.stream().map(WrongStudentBriefVO::getStudentId).filter(Objects::nonNull).toList();
        List<UserSimpleDTO> users = userFeignClient.getUserSimpleBatch(userIds);
        if (CollectionUtils.isEmpty(users)) {
            return;
        }
        Map<Long, UserSimpleDTO> userMap = users.stream()
                .filter(u -> u.getUserId() != null)
                .collect(Collectors.toMap(UserSimpleDTO::getUserId, Function.identity(), (a, b) -> a));
        students.forEach(s -> {
            UserSimpleDTO u = userMap.get(s.getStudentId());
            if (u != null) {
                s.setStudentName(u.getName());
                s.setAvatarUrl(u.getAvatarUrl());
            }
        });
    }

    private void enrichQuestionInfo(List<FrequentWrongQuestionVO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<Long> questionIds = list.stream().map(FrequentWrongQuestionVO::getQuestionId)
                .filter(Objects::nonNull).distinct().toList();
        List<QuestionSimpleDTO> questions = examFeignClient.getQuestionBatch(questionIds);
        if (CollectionUtils.isEmpty(questions)) {
            return;
        }
        Map<Long, QuestionSimpleDTO> questionMap = questions.stream()
                .filter(q -> q.getQuestionId() != null)
                .collect(Collectors.toMap(QuestionSimpleDTO::getQuestionId, Function.identity(), (a, b) -> a));
        list.forEach(item -> {
            QuestionSimpleDTO q = questionMap.get(item.getQuestionId());
            if (q != null) {
                item.setDifficulty(q.getDifficulty());
                item.setStem(q.getStem());
            }
        });
    }
}
