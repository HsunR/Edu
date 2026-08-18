package com.gpnu.knowledge.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gpnu.knowledge.model.entity.KnowledgeSection;
import com.gpnu.knowledge.model.vo.KnowledgePointVO;

import java.util.List;

public interface IKnowledgeSectionService extends IService<KnowledgeSection> {

    void bindSections(Long teacherId, Long pointId, List<Long> sectionIds);

    void unbindSection(Long teacherId, Long pointId, Long sectionId);

    List<KnowledgePointVO> getPointsBySection(Long sectionId);

    List<Long> getSectionIdsByPoint(Long pointId);

    void clearSectionRelations(Long sectionId);
}
