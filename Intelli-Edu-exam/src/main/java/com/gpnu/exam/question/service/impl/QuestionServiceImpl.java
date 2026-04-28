package com.gpnu.exam.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.exam.question.mapper.QuestionMapper;
import com.gpnu.exam.question.mapper.QuestionOptionMapper;
import com.gpnu.exam.question.model.dto.QuestionCreateRequest;
import com.gpnu.exam.question.model.dto.QuestionOptionDTO;
import com.gpnu.exam.question.model.dto.QuestionQueryRequest;
import com.gpnu.exam.question.model.dto.QuestionUpdateRequest;
import com.gpnu.exam.question.model.entity.Question;
import com.gpnu.exam.question.model.entity.QuestionBank;
import com.gpnu.exam.question.model.entity.QuestionOption;
import com.gpnu.exam.question.model.enums.QuestionType;
import com.gpnu.exam.question.model.vo.QuestionOptionVO;
import com.gpnu.exam.question.model.vo.QuestionVO;
import com.gpnu.exam.question.service.IQuestionBankService;
import com.gpnu.exam.question.service.IQuestionService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements IQuestionService {

    @Resource
    private QuestionOptionMapper optionMapper;

    @Resource
    private IQuestionBankService questionBankService;

    @Override
    @Transactional
    public QuestionVO createQuestion(Long teacherId, Long bankId, QuestionCreateRequest request) {
        // 校验题库存在且属于当前教师
        QuestionBank bank = questionBankService.getById(bankId);
        ThrowUtils.throwIf(bank == null, ErrorCode.NOT_FOUND_ERROR, "题库不存在");
        ThrowUtils.throwIf(!bank.getTeacherId().equals(teacherId), ErrorCode.NO_AUTH_ERROR, "无权操作该题库");

        // 创建题目
        Question question = new Question();
        question.setBankId(bankId);
        question.setQuestionType(QuestionType.values()[request.getQuestionType()]);
        question.setStem(request.getStem());
        question.setAnalysis(request.getAnalysis());
        question.setAnswer(request.getAnswer());
        question.setScore(request.getScore());
        if (request.getDifficulty() != null) {
            question.setDifficulty(com.gpnu.exam.question.model.enums.Difficulty.values()[request.getDifficulty() - 1]);
        }
        save(question);

        // 创建选项
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            saveOptions(question.getQuestionId(), request.getOptions());
        }

        // 更新题库计数
        questionBankService.update(new LambdaUpdateWrapper<QuestionBank>()
                .eq(QuestionBank::getBankId, bankId)
                .setSql("question_count = question_count + 1"));

        return getQuestion(question.getQuestionId());
    }

    @Override
    @Transactional
    public QuestionVO updateQuestion(Long teacherId, Long questionId, QuestionUpdateRequest request) {
        Question question = getById(questionId);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");

        // 校验权限（通过题库校验教师）
        QuestionBank bank = questionBankService.getById(question.getBankId());
        ThrowUtils.throwIf(!bank.getTeacherId().equals(teacherId), ErrorCode.NO_AUTH_ERROR, "无权操作");

        // 更新题目字段
        if (StringUtils.hasText(request.getStem())) {
            question.setStem(request.getStem());
        }
        if (request.getAnalysis() != null) {
            question.setAnalysis(request.getAnalysis());
        }
        if (request.getAnswer() != null) {
            question.setAnswer(request.getAnswer());
        }
        if (request.getScore() != null) {
            question.setScore(request.getScore());
        }
        if (request.getDifficulty() != null) {
            question.setDifficulty(com.gpnu.exam.question.model.enums.Difficulty.values()[request.getDifficulty() - 1]);
        }
        updateById(question);

        // 全量替换选项
        if (request.getOptions() != null) {
            // 软删除旧选项
            optionMapper.delete(new LambdaQueryWrapper<QuestionOption>()
                    .eq(QuestionOption::getQuestionId, questionId));
            // 插入新选项
            if (!request.getOptions().isEmpty()) {
                saveOptions(questionId, request.getOptions());
            }
        }

        return getQuestion(questionId);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long teacherId, Long questionId) {
        Question question = getById(questionId);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");

        QuestionBank bank = questionBankService.getById(question.getBankId());
        ThrowUtils.throwIf(!bank.getTeacherId().equals(teacherId), ErrorCode.NO_AUTH_ERROR, "无权操作");

        // 软删除题目和选项
        removeById(questionId);
        optionMapper.delete(new LambdaQueryWrapper<QuestionOption>()
                .eq(QuestionOption::getQuestionId, questionId));

        // 更新题库计数
        questionBankService.update(new LambdaUpdateWrapper<QuestionBank>()
                .eq(QuestionBank::getBankId, question.getBankId())
                .setSql("question_count = GREATEST(question_count - 1, 0)"));
    }

    @Override
    public QuestionVO getQuestion(Long questionId) {
        Question question = getById(questionId);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");

        List<QuestionOption> options = optionMapper.selectList(
                new LambdaQueryWrapper<QuestionOption>()
                        .eq(QuestionOption::getQuestionId, questionId)
                        .orderByAsc(QuestionOption::getOrderIndex));

        return toVO(question, options);
    }

    @Override
    public Page<QuestionVO> listQuestions(QuestionQueryRequest request) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(request.getBankId() != null, Question::getBankId, request.getBankId());
        wrapper.eq(request.getQuestionType() != null, Question::getQuestionType, request.getQuestionType());
        wrapper.eq(request.getDifficulty() != null, Question::getDifficulty, request.getDifficulty());
        wrapper.like(StringUtils.hasText(request.getKeyword()), Question::getStem, request.getKeyword());
        wrapper.orderByDesc(Question::getCreatedAt);

        Page<Question> page = page(new Page<>(request.getCurrent(), request.getPageSize()), wrapper);

        // 批量查询选项
        List<Long> questionIds = page.getRecords().stream().map(Question::getQuestionId).toList();
        Map<Long, List<QuestionOption>> optionMap = getOptionsMap(questionIds);

        Page<QuestionVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream()
                .map(q -> toVO(q, optionMap.getOrDefault(q.getQuestionId(), List.of())))
                .toList());
        return voPage;
    }

    @Override
    public List<QuestionVO> listByIds(List<Long> questionIds) {
        if (questionIds == null || questionIds.isEmpty()) {
            return List.of();
        }
        List<Question> questions = listByIds(new HashSet<>(questionIds));
        Map<Long, List<QuestionOption>> optionMap = getOptionsMap(questionIds);
        return questions.stream()
                .map(q -> toVO(q, optionMap.getOrDefault(q.getQuestionId(), List.of())))
                .toList();
    }

    // ==================== 私有方法 ====================

    private void saveOptions(Long questionId, List<QuestionOptionDTO> optionDTOs) {
        for (int i = 0; i < optionDTOs.size(); i++) {
            QuestionOptionDTO dto = optionDTOs.get(i);
            QuestionOption option = new QuestionOption();
            option.setQuestionId(questionId);
            option.setLabel(dto.getLabel());
            option.setContent(dto.getContent());
            option.setIsCorrect(dto.getIsCorrect() != null ? dto.getIsCorrect() : false);
            option.setOrderIndex(dto.getOrderIndex() != null ? dto.getOrderIndex() : i);
            optionMapper.insert(option);
        }
    }

    private Map<Long, List<QuestionOption>> getOptionsMap(List<Long> questionIds) {
        if (questionIds.isEmpty()) {
            return Map.of();
        }
        List<QuestionOption> allOptions = optionMapper.selectList(
                new LambdaQueryWrapper<QuestionOption>()
                        .in(QuestionOption::getQuestionId, questionIds)
                        .orderByAsc(QuestionOption::getOrderIndex));
        return allOptions.stream().collect(Collectors.groupingBy(QuestionOption::getQuestionId));
    }

    private QuestionVO toVO(Question question, List<QuestionOption> options) {
        QuestionVO vo = new QuestionVO();
        vo.setQuestionId(question.getQuestionId());
        vo.setBankId(question.getBankId());
        vo.setQuestionType(question.getQuestionType().getCode());
        vo.setStem(question.getStem());
        vo.setAnalysis(question.getAnalysis());
        vo.setAnswer(question.getAnswer());
        vo.setScore(question.getScore());
        vo.setDifficulty(question.getDifficulty().getCode());
        vo.setCreatedAt(question.getCreatedAt());
        vo.setUpdatedAt(question.getUpdatedAt());
        vo.setOptions(options.stream().map(this::toOptionVO).toList());
        return vo;
    }

    private QuestionOptionVO toOptionVO(QuestionOption option) {
        QuestionOptionVO vo = new QuestionOptionVO();
        BeanUtils.copyProperties(option, vo);
        return vo;
    }
}
