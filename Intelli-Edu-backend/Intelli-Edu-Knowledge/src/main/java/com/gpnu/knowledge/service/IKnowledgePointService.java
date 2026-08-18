package com.gpnu.knowledge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gpnu.api.dto.knowledge.KnowledgeTreeDTO;
import com.gpnu.api.dto.knowledge.PointSimpleDTO;
import com.gpnu.knowledge.model.dto.PointCreateRequest;
import com.gpnu.knowledge.model.dto.PointUpdateRequest;
import com.gpnu.knowledge.model.entity.KnowledgePoint;
import com.gpnu.knowledge.model.vo.KnowledgePointVO;
import com.gpnu.knowledge.model.vo.KnowledgeTreeVO;
import com.gpnu.knowledge.model.vo.PointSimpleVO;

import java.util.List;
import java.util.Map;

public interface IKnowledgePointService extends IService<KnowledgePoint> {

    KnowledgePointVO createPoint(Long teacherId, PointCreateRequest request);

    KnowledgePointVO updatePoint(Long teacherId, Long pointId, PointUpdateRequest request);

    void deletePoint(Long teacherId, Long pointId);

    List<KnowledgeTreeVO> getPointTree(Long courseId);

    List<KnowledgeTreeDTO> getPointTreeDTO(Long courseId);

    List<PointSimpleVO> getPointBatch(List<Long> pointIds);

    List<PointSimpleDTO> getPointBatchDTO(List<Long> pointIds);

    List<Long> getQuestionIdsByPoint(Long pointId);

    Map<Long, List<PointSimpleVO>> getPointsByQuestions(List<Long> questionIds);

    Map<Long, List<PointSimpleDTO>> getPointsByQuestionsDTO(List<Long> questionIds);
}
