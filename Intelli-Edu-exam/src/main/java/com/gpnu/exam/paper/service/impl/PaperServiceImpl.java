package com.gpnu.exam.paper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.exam.paper.mapper.PaperMapper;
import com.gpnu.exam.paper.mapper.PaperQuestionMapper;
import com.gpnu.exam.paper.model.dto.*;
import com.gpnu.exam.paper.model.entity.Paper;
import com.gpnu.exam.paper.model.entity.PaperQuestion;
import com.gpnu.exam.paper.model.enums.PaperStatus;
import com.gpnu.exam.paper.model.vo.PaperDetailVO;
import com.gpnu.exam.paper.model.vo.PaperQuestionVO;
import com.gpnu.exam.paper.model.vo.PaperVO;
import com.gpnu.exam.paper.service.IPaperService;
import com.gpnu.exam.question.model.vo.QuestionOptionVO;
import com.gpnu.exam.question.model.vo.QuestionVO;
import com.gpnu.exam.question.service.IQuestionService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper>
        implements IPaperService {

    @Resource
    private PaperQuestionMapper paperQuestionMapper;

    @Resource
    private IQuestionService questionService;

    @Override
    public PaperVO createPaper(Long teacherId, PaperCreateRequest request) {
        Paper paper = new Paper();
        paper.setPaperName(request.getPaperName());
        paper.setCourseId(request.getCourseId());
        paper.setTeacherId(teacherId);
        paper.setTotalScore(BigDecimal.ZERO);
        paper.setSections(request.getSections());
        paper.setStatus(PaperStatus.DRAFT);
        save(paper);
        return toVO(paper);
    }

    @Override
    public PaperVO updatePaper(Long teacherId, Long paperId, PaperUpdateRequest request) {
        Paper paper = getAndValidate(teacherId, paperId, true);

        if (StringUtils.hasText(request.getPaperName())) {
            paper.setPaperName(request.getPaperName());
        }
        if (request.getSections() != null) {
            paper.setSections(request.getSections());
        }
        updateById(paper);
        return toVO(paper);
    }

    @Override
    public void deletePaper(Long teacherId, Long paperId) {
        Paper paper = getAndValidate(teacherId, paperId, true);
        removeById(paperId);
        // 软删除关联的题目
        paperQuestionMapper.delete(new LambdaQueryWrapper<PaperQuestion>()
                .eq(PaperQuestion::getPaperId, paperId));
    }

    @Override
    @Transactional
    public void addQuestions(Long teacherId, Long paperId, PaperQuestionAddRequest request) {
        Paper paper = getAndValidate(teacherId, paperId, true);

        // 查询当前最大order_index
        int maxOrder = getMaxOrderIndex(paperId);

        for (PaperQuestionAddRequest.QuestionItem item : request.getQuestions()) {
            // 校验题目存在
            QuestionVO question = questionService.getQuestion(item.getQuestionId());
            ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR,
                    "题目不存在: " + item.getQuestionId());

            PaperQuestion pq = new PaperQuestion();
            pq.setPaperId(paperId);
            pq.setQuestionId(item.getQuestionId());
            pq.setScore(item.getScore());
            pq.setSectionIndex(item.getSectionIndex() != null ? item.getSectionIndex() : 1);
            pq.setOrderIndex(++maxOrder);
            paperQuestionMapper.insert(pq);
        }

        // 重新计算总分
        recalculateTotalScore(paper);
    }

    @Override
    @Transactional
    public void removeQuestion(Long teacherId, Long paperId, Long questionId) {
        Paper paper = getAndValidate(teacherId, paperId, true);

        paperQuestionMapper.delete(new LambdaQueryWrapper<PaperQuestion>()
                .eq(PaperQuestion::getPaperId, paperId)
                .eq(PaperQuestion::getQuestionId, questionId));

        recalculateTotalScore(paper);
    }

    @Override
    @Transactional
    public void reorderQuestions(Long teacherId, Long paperId, PaperQuestionOrderRequest request) {
        getAndValidate(teacherId, paperId, true);

        for (PaperQuestionOrderRequest.OrderItem item : request.getItems()) {
            PaperQuestion pq = paperQuestionMapper.selectById(item.getId());
            if (pq != null && pq.getPaperId().equals(paperId)) {
                pq.setOrderIndex(item.getOrderIndex());
                if (item.getSectionIndex() != null) {
                    pq.setSectionIndex(item.getSectionIndex());
                }
                paperQuestionMapper.updateById(pq);
            }
        }
    }

    @Override
    @Transactional
    public void publishPaper(Long teacherId, Long paperId) {
        Paper paper = getAndValidate(teacherId, paperId, true);

        // 查询所有关联题目
        List<PaperQuestion> pqs = paperQuestionMapper.selectList(
                new LambdaQueryWrapper<PaperQuestion>()
                        .eq(PaperQuestion::getPaperId, paperId)
                        .orderByAsc(PaperQuestion::getOrderIndex));
        ThrowUtils.throwIf(pqs.isEmpty(), ErrorCode.OPERATION_ERROR, "试卷中没有题目，无法发布");

        // 批量查询题目完整信息
        List<Long> questionIds = pqs.stream().map(PaperQuestion::getQuestionId).toList();
        List<QuestionVO> questions = questionService.listByIds(questionIds);
        Map<Long, QuestionVO> questionMap = questions.stream()
                .collect(Collectors.toMap(QuestionVO::getQuestionId, Function.identity()));

        // 冻结快照
        for (PaperQuestion pq : pqs) {
            QuestionVO q = questionMap.get(pq.getQuestionId());
            if (q != null) {
                pq.setQuestionSnapshot(buildSnapshot(q));
                paperQuestionMapper.updateById(pq);
            }
        }

        // 更新状态
        paper.setStatus(PaperStatus.PUBLISHED);
        updateById(paper);
    }

    @Override
    public Page<PaperVO> listPapers(Long teacherId, PaperQueryRequest request) {
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Paper::getTeacherId, teacherId);
        wrapper.eq(request.getCourseId() != null, Paper::getCourseId, request.getCourseId());
        wrapper.eq(request.getStatus() != null, Paper::getStatus, request.getStatus());
        wrapper.like(StringUtils.hasText(request.getKeyword()), Paper::getPaperName, request.getKeyword());
        wrapper.orderByDesc(Paper::getCreatedAt);

        Page<Paper> page = page(new Page<>(request.getCurrent(), request.getPageSize()), wrapper);
        Page<PaperVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        // 附带题目数量
        voPage.setRecords(page.getRecords().stream().map(p -> {
            PaperVO vo = toVO(p);
            Long count = paperQuestionMapper.selectCount(
                    new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId, p.getPaperId()));
            vo.setQuestionCount(count.intValue());
            return vo;
        }).toList());
        return voPage;
    }

    @Override
    public PaperDetailVO getPaperDetail(Long paperId) {
        Paper paper = getById(paperId);
        ThrowUtils.throwIf(paper == null, ErrorCode.NOT_FOUND_ERROR, "试卷不存在");

        List<PaperQuestion> pqs = paperQuestionMapper.selectList(
                new LambdaQueryWrapper<PaperQuestion>()
                        .eq(PaperQuestion::getPaperId, paperId)
                        .orderByAsc(PaperQuestion::getSectionIndex)
                        .orderByAsc(PaperQuestion::getOrderIndex));

        // 如果是草稿，查实时题目数据；如果已发布，用快照
        boolean isDraft = paper.getStatus() == PaperStatus.DRAFT;
        Map<Long, QuestionVO> questionMap = Map.of();
        if (isDraft && !pqs.isEmpty()) {
            List<Long> qIds = pqs.stream().map(PaperQuestion::getQuestionId).toList();
            questionMap = questionService.listByIds(qIds).stream()
                    .collect(Collectors.toMap(QuestionVO::getQuestionId, Function.identity()));
        }

        List<PaperQuestionVO> pqVOs = new ArrayList<>();
        for (PaperQuestion pq : pqs) {
            PaperQuestionVO pqVO = new PaperQuestionVO();
            pqVO.setId(pq.getId());
            pqVO.setPaperId(pq.getPaperId());
            pqVO.setQuestionId(pq.getQuestionId());
            pqVO.setOrderIndex(pq.getOrderIndex());
            pqVO.setScore(pq.getScore());
            pqVO.setSectionIndex(pq.getSectionIndex());
            pqVO.setQuestionSnapshot(pq.getQuestionSnapshot());

            if (isDraft) {
                pqVO.setQuestion(questionMap.get(pq.getQuestionId()));
            }
            pqVOs.add(pqVO);
        }

        PaperDetailVO detail = new PaperDetailVO();
        detail.setPaperId(paper.getPaperId());
        detail.setPaperName(paper.getPaperName());
        detail.setCourseId(paper.getCourseId());
        detail.setTeacherId(paper.getTeacherId());
        detail.setTotalScore(paper.getTotalScore());
        detail.setSections(paper.getSections());
        detail.setStatus(paper.getStatus().getCode());
        detail.setQuestions(pqVOs);
        detail.setCreatedAt(paper.getCreatedAt());
        detail.setUpdatedAt(paper.getUpdatedAt());
        return detail;
    }

    // ==================== 私有方法 ====================

    private Paper getAndValidate(Long teacherId, Long paperId, boolean requireDraft) {
        Paper paper = getById(paperId);
        ThrowUtils.throwIf(paper == null, ErrorCode.NOT_FOUND_ERROR, "试卷不存在");
        ThrowUtils.throwIf(!paper.getTeacherId().equals(teacherId), ErrorCode.NO_AUTH_ERROR, "无权操作该试卷");
        if (requireDraft) {
            ThrowUtils.throwIf(paper.getStatus() != PaperStatus.DRAFT, ErrorCode.OPERATION_ERROR, "已发布的试卷不可修改");
        }
        return paper;
    }

    private int getMaxOrderIndex(Long paperId) {
        List<PaperQuestion> list = paperQuestionMapper.selectList(
                new LambdaQueryWrapper<PaperQuestion>()
                        .eq(PaperQuestion::getPaperId, paperId)
                        .orderByDesc(PaperQuestion::getOrderIndex)
                        .last("LIMIT 1"));
        return list.isEmpty() ? 0 : list.get(0).getOrderIndex();
    }

    private void recalculateTotalScore(Paper paper) {
        List<PaperQuestion> pqs = paperQuestionMapper.selectList(
                new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId, paper.getPaperId()));
        BigDecimal total = pqs.stream()
                .map(PaperQuestion::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        paper.setTotalScore(total);
        updateById(paper);
    }

    private Map<String, Object> buildSnapshot(QuestionVO q) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("stem", q.getStem());
        snapshot.put("question_type", q.getQuestionType());
        snapshot.put("answer", q.getAnswer());
        snapshot.put("analysis", q.getAnalysis());
        if (q.getOptions() != null && !q.getOptions().isEmpty()) {
            List<Map<String, Object>> optList = q.getOptions().stream().map(o -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("label", o.getLabel());
                m.put("content", o.getContent());
                m.put("is_correct", o.getIsCorrect());
                return m;
            }).toList();
            snapshot.put("options", optList);
        }
        return snapshot;
    }

    private PaperVO toVO(Paper paper) {
        PaperVO vo = new PaperVO();
        vo.setPaperId(paper.getPaperId());
        vo.setPaperName(paper.getPaperName());
        vo.setCourseId(paper.getCourseId());
        vo.setTeacherId(paper.getTeacherId());
        vo.setTotalScore(paper.getTotalScore());
        vo.setSections(paper.getSections());
        vo.setStatus(paper.getStatus().getCode());
        vo.setCreatedAt(paper.getCreatedAt());
        vo.setUpdatedAt(paper.getUpdatedAt());
        return vo;
    }
}
