package com.gpnu.exam.question.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpnu.exam.question.model.dto.QuestionCreateRequest;
import com.gpnu.exam.question.model.dto.QuestionQueryRequest;
import com.gpnu.exam.question.model.dto.QuestionUpdateRequest;
import com.gpnu.exam.question.model.entity.Question;
import com.gpnu.exam.question.model.vo.QuestionVO;

import java.util.List;

public interface IQuestionService extends IService<Question> {

    QuestionVO createQuestion(Long teacherId, Long bankId, QuestionCreateRequest request);

    QuestionVO updateQuestion(Long teacherId, Long questionId, QuestionUpdateRequest request);

    void deleteQuestion(Long teacherId, Long questionId);

    QuestionVO getQuestion(Long questionId);

    Page<QuestionVO> listQuestions(QuestionQueryRequest request);

    /**
     * 批量查询题目（含选项），用于试卷快照
     */
    List<QuestionVO> listByIds(List<Long> questionIds);

    /**
     * 获取题目所属课程ID
     */
    Long getQuestionCourseId(Long questionId);
}
