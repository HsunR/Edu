package com.gpnu.learning.service;

import com.gpnu.api.client.knowledge.KnowledgeFeignClient;
import com.gpnu.api.dto.knowledge.KnowledgeTreeDTO;
import com.gpnu.learning.config.LearningProperties;
import com.gpnu.learning.model.entity.LpMastery;
import com.gpnu.learning.model.vo.GraphOverviewVO;
import com.gpnu.learning.model.vo.GraphPointVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程图谱聚合（F2）：Knowledge 知识点树 + Learning 掌握度叠加。
 * <p>
 * 前端三 Tab（大纲/思维导图/图谱）共用本接口返回的树形数据。
 */
@Service
public class GraphOverviewService {

    @Resource
    private KnowledgeFeignClient knowledgeFeignClient;

    @Resource
    private LearningQuerySupport learningQuerySupport;

    @Resource
    private ClassAccessService classAccessService;

    @Resource
    private LearningProperties learningProperties;

    public GraphOverviewVO getStudentGraphOverview(Long studentId, Long classId, Long courseId) {
        classAccessService.validateStudentMember(studentId, classId);

        GraphOverviewVO overview = new GraphOverviewVO();
        overview.setClassId(classId);
        overview.setCourseId(courseId);
        overview.setWeakThreshold(learningProperties.getWeakMasteryThreshold());

        // pointId → masteryLevel（仅当前学生在当前班的数据）
        Map<Long, Integer> masteryMap = new HashMap<>();
        List<LpMastery> masteries = learningQuerySupport.listByStudentAndClass(studentId, classId);
        for (LpMastery m : masteries) {
            masteryMap.put(m.getPointId(), m.getMasteryLevel());
        }

        List<KnowledgeTreeDTO> tree = knowledgeFeignClient.getPointTree(courseId);
        if (CollectionUtils.isEmpty(tree)) {
            return overview;
        }

        int threshold = learningProperties.getWeakMasteryThreshold();
        for (KnowledgeTreeDTO root : tree) {
            overview.getPoints().add(toGraphPoint(root, masteryMap, threshold));
        }
        return overview;
    }

    private GraphPointVO toGraphPoint(KnowledgeTreeDTO node, Map<Long, Integer> masteryMap, int threshold) {
        GraphPointVO vo = new GraphPointVO();
        vo.setPointId(node.getPointId());
        vo.setPointName(node.getPointName());
        vo.setParentId(node.getParentId());
        vo.setDescription(node.getDescription());
        vo.setOrderIndex(node.getOrderIndex());

        Integer level = masteryMap.get(node.getPointId());
        vo.setMasteryLevel(level);
        vo.setIsWeak(level != null && level < threshold);

        // 关联章节 ID，供前端跳转章节学习
        List<Long> sectionIds = knowledgeFeignClient.getSectionIdsByPoint(node.getPointId());
        if (!CollectionUtils.isEmpty(sectionIds)) {
            vo.setSectionIds(sectionIds);
        }

        if (!CollectionUtils.isEmpty(node.getChildren())) {
            for (KnowledgeTreeDTO child : node.getChildren()) {
                vo.getChildPoints().add(toGraphPoint(child, masteryMap, threshold));
            }
        }
        return vo;
    }
}