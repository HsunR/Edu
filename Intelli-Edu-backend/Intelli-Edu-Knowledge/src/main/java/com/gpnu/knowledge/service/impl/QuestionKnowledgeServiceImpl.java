package com.gpnu.knowledge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.api.client.exam.ExamFeignClient;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.knowledge.mapper.KnowledgePointMapper;
import com.gpnu.knowledge.mapper.QuestionKnowledgeMapper;
import com.gpnu.knowledge.model.entity.KnowledgePoint;
import com.gpnu.knowledge.model.entity.QuestionKnowledge;
import com.gpnu.knowledge.model.vo.KnowledgePointVO;
import com.gpnu.knowledge.service.IQuestionKnowledgeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class QuestionKnowledgeServiceImpl extends ServiceImpl<QuestionKnowledgeMapper, QuestionKnowledge>
        implements IQuestionKnowledgeService {

    @Resource
    private KnowledgePointMapper knowledgePointMapper;

    @Resource
    private ExamFeignClient examFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindQuestions(Long teacherId, Long pointId, List<Long> questionIds) {
        // 校验知识点存在且为二级知识点
        KnowledgePoint point = knowledgePointMapper.selectById(pointId);
        ThrowUtils.throwIf(point == null, ErrorCode.NOT_FOUND_ERROR, "知识点不存在");
        ThrowUtils.throwIf(point.getParentId() == null,
                ErrorCode.PARAMS_ERROR, "一级知识点不能关联题目，请选择二级知识点");

        for (Long questionId : questionIds) {
            // Feign 校验题目存在且属于同一课程
            Long questionCourseId = examFeignClient.getQuestionCourseId(questionId);
            ThrowUtils.throwIf(questionCourseId == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");
            ThrowUtils.throwIf(!questionCourseId.equals(point.getCourseId()),
                    ErrorCode.PARAMS_ERROR, "题目不属于该课程");

            // 检查是否已关联
            boolean exists = exists(new LambdaQueryWrapper<QuestionKnowledge>()
                    .eq(QuestionKnowledge::getPointId, pointId)
                    .eq(QuestionKnowledge::getQuestionId, questionId));
            if (exists) {
                continue;
            }

            QuestionKnowledge qk = new QuestionKnowledge();
            qk.setPointId(pointId);
            qk.setQuestionId(questionId);
            qk.setCourseId(point.getCourseId());
            save(qk);
        }
        log.info("Questions bound to point, pointId={}, questionIds={}", pointId, questionIds);
    }

    @Override
    public void unbindQuestion(Long teacherId, Long pointId, Long questionId) {
        KnowledgePoint point = knowledgePointMapper.selectById(pointId);
        ThrowUtils.throwIf(point == null, ErrorCode.NOT_FOUND_ERROR, "知识点不存在");

        int deleted = getBaseMapper().delete(new LambdaQueryWrapper<QuestionKnowledge>()
                .eq(QuestionKnowledge::getPointId, pointId)
                .eq(QuestionKnowledge::getQuestionId, questionId));
        ThrowUtils.throwIf(deleted == 0, ErrorCode.NOT_FOUND_ERROR, "关联关系不存在");
        log.info("Question unbound from point, pointId={}, questionId={}", pointId, questionId);
    }

    @Override
    public List<KnowledgePointVO> getPointsByQuestion(Long questionId) {
        List<QuestionKnowledge> records = list(new LambdaQueryWrapper<QuestionKnowledge>()
                .eq(QuestionKnowledge::getQuestionId, questionId));
        List<Long> pointIds = records.stream().map(QuestionKnowledge::getPointId).toList();
        if (pointIds.isEmpty()) {
            return List.of();
        }
        List<KnowledgePoint> points = knowledgePointMapper.selectList(
                new LambdaQueryWrapper<KnowledgePoint>()
                        .in(KnowledgePoint::getPointId, pointIds));
        return points.stream().map(p -> {
            KnowledgePointVO vo = new KnowledgePointVO();
            BeanUtil.copyProperties(p, vo);
            return vo;
        }).toList();
    }

    @Override
    public void clearQuestionRelations(Long questionId) {
        // TODO: 等消息队列建设好后，切换为 MQ 事件驱动 (exam.question.deleted)
        getBaseMapper().delete(new LambdaQueryWrapper<QuestionKnowledge>()
                .eq(QuestionKnowledge::getQuestionId, questionId));
        log.info("Cleared all knowledge relations for questionId={}", questionId);
    }
}
