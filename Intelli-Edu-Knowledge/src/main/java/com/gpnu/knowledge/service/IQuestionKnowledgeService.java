package com.gpnu.knowledge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gpnu.knowledge.model.entity.QuestionKnowledge;
import com.gpnu.knowledge.model.vo.KnowledgePointVO;

import java.util.List;

public interface IQuestionKnowledgeService extends IService<QuestionKnowledge> {

    void bindQuestions(Long teacherId, Long pointId, List<Long> questionIds);

    void unbindQuestion(Long teacherId, Long pointId, Long questionId);

    List<KnowledgePointVO> getPointsByQuestion(Long questionId);

    void clearQuestionRelations(Long questionId);
}
