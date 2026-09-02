package com.gpnu.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gpnu.learning.model.entity.LpWrongRecord;
import com.gpnu.learning.model.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LpWrongRecordMapper extends BaseMapper<LpWrongRecord> {

    List<WrongTypeStatVO> statByQuestionType(@Param("studentId") Long studentId,
                                             @Param("classId") Long classId,
                                             @Param("courseId") Long courseId);

    List<WrongPointStatVO> statByKnowledgePoint(@Param("studentId") Long studentId,
                                                @Param("classId") Long classId,
                                                @Param("courseId") Long courseId);

    /** 教师端：班级高频错题（按 question_id 聚合） */
    List<FrequentWrongQuestionVO> statFrequentQuestions(@Param("classId") Long classId,
                                                        @Param("limit") int limit);

    /** 教师端：班级错题知识点分布（studentId 传 null） */
    List<WrongPointStatVO> statClassWrongByPoint(@Param("classId") Long classId);

    /** 教师端：某题答错学生列表 */
    List<WrongStudentBriefVO> listWrongStudentsByQuestion(@Param("classId") Long classId,
                                                          @Param("questionId") Long questionId);

    /** 教师端：某题错因类型分布 */
    List<WrongTypeDistVO> statWrongTypeByQuestion(@Param("classId") Long classId,
                                                  @Param("questionId") Long questionId);
}
