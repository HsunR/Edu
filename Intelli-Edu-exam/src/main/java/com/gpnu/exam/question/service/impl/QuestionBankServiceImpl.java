package com.gpnu.exam.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.exam.question.mapper.QuestionBankMapper;
import com.gpnu.exam.question.model.dto.QuestionBankCreateRequest;
import com.gpnu.exam.question.model.dto.QuestionBankQueryRequest;
import com.gpnu.exam.question.model.dto.QuestionBankUpdateRequest;
import com.gpnu.exam.question.model.entity.QuestionBank;
import com.gpnu.exam.question.model.vo.QuestionBankVO;
import com.gpnu.exam.question.service.IQuestionBankService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank>
        implements IQuestionBankService {

    @Override
    public QuestionBankVO createBank(Long teacherId, QuestionBankCreateRequest request) {
        QuestionBank bank = new QuestionBank();
        bank.setBankName(request.getBankName());
        bank.setCourseId(request.getCourseId());
        bank.setTeacherId(teacherId);
        bank.setDescription(request.getDescription());
        bank.setQuestionCount(0);
        save(bank);
        return toVO(bank);
    }

    @Override
    public QuestionBankVO updateBank(Long teacherId, Long bankId, QuestionBankUpdateRequest request) {
        QuestionBank bank = getById(bankId);
        ThrowUtils.throwIf(bank == null, ErrorCode.NOT_FOUND_ERROR, "题库不存在");
        ThrowUtils.throwIf(!bank.getTeacherId().equals(teacherId), ErrorCode.NO_AUTH_ERROR, "无权操作该题库");

        if (StringUtils.hasText(request.getBankName())) {
            bank.setBankName(request.getBankName());
        }
        if (request.getDescription() != null) {
            bank.setDescription(request.getDescription());
        }
        updateById(bank);
        return toVO(bank);
    }

    @Override
    public void deleteBank(Long teacherId, Long bankId) {
        QuestionBank bank = getById(bankId);
        ThrowUtils.throwIf(bank == null, ErrorCode.NOT_FOUND_ERROR, "题库不存在");
        ThrowUtils.throwIf(!bank.getTeacherId().equals(teacherId), ErrorCode.NO_AUTH_ERROR, "无权操作该题库");
        ThrowUtils.throwIf(bank.getQuestionCount() > 0, ErrorCode.OPERATION_ERROR, "题库中还有题目，不能删除");
        removeById(bankId);
    }

    @Override
    public Page<QuestionBankVO> listBanks(Long teacherId, QuestionBankQueryRequest request) {
        LambdaQueryWrapper<QuestionBank> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(request.getCourseId() != null, QuestionBank::getCourseId, request.getCourseId());
        wrapper.eq(QuestionBank::getTeacherId, teacherId);
        wrapper.like(StringUtils.hasText(request.getKeyword()), QuestionBank::getBankName, request.getKeyword());
        wrapper.orderByDesc(QuestionBank::getCreatedAt);

        Page<QuestionBank> page = page(new Page<>(request.getCurrent(), request.getPageSize()), wrapper);
        Page<QuestionBankVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(this::toVO).toList());
        return voPage;
    }

    private QuestionBankVO toVO(QuestionBank bank) {
        QuestionBankVO vo = new QuestionBankVO();
        BeanUtils.copyProperties(bank, vo);
        return vo;
    }
}
