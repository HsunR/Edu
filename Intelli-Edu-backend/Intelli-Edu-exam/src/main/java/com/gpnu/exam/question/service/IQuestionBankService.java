package com.gpnu.exam.question.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpnu.exam.question.model.dto.QuestionBankCreateRequest;
import com.gpnu.exam.question.model.dto.QuestionBankQueryRequest;
import com.gpnu.exam.question.model.dto.QuestionBankUpdateRequest;
import com.gpnu.exam.question.model.entity.QuestionBank;
import com.gpnu.exam.question.model.vo.QuestionBankVO;

public interface IQuestionBankService extends IService<QuestionBank> {

    QuestionBankVO createBank(Long teacherId, QuestionBankCreateRequest request);

    QuestionBankVO updateBank(Long teacherId, Long bankId, QuestionBankUpdateRequest request);

    void deleteBank(Long teacherId, Long bankId);

    Page<QuestionBankVO> listBanks(Long teacherId, QuestionBankQueryRequest request);
}
