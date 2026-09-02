package com.gpnu.learning.service;

import com.gpnu.learning.model.vo.ClassMasteryPointVO;
import com.gpnu.learning.model.vo.FrequentWrongQuestionVO;
import com.gpnu.learning.model.vo.MasteryOverviewVO;
import com.gpnu.learning.model.vo.WrongQuestionDetailVO;
import com.gpnu.learning.model.vo.WrongStatsVO;

import java.util.List;

/**
 * 教师端学情查询服务（按 class_id 聚合）。
 * 对外契约型服务，采用接口 + 实现分离。
 */
public interface ITeacherLearningService {

    List<ClassMasteryPointVO> getClassMasteryOverview(Long teacherId, Long classId);

    List<MasteryOverviewVO> getStudentMastery(Long teacherId, Long classId, Long studentId);

    List<FrequentWrongQuestionVO> getFrequentWrongs(Long teacherId, Long classId, Integer limit);

    WrongStatsVO getClassWrongPointDistribution(Long teacherId, Long classId);

    WrongQuestionDetailVO getQuestionWrongDetail(Long teacherId, Long classId, Long questionId);

    /**
     * 不做教师授课校验的题目错题详情查询，供对内 Feign（InnerLearningService）复用。
     * 调用方需自行保证已做权限校验或处于受信任的内部调用场景。
     */
    WrongQuestionDetailVO getQuestionWrongDetailWithoutAuth(Long classId, Long questionId);
}