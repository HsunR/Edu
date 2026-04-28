package com.gpnu.exam.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpnu.api.dto.exam.ExamSimpleDTO;
import com.gpnu.exam.exam.model.dto.ExamCreateRequest;
import com.gpnu.exam.exam.model.dto.ExamQueryRequest;
import com.gpnu.exam.exam.model.dto.ExamUpdateRequest;
import com.gpnu.exam.exam.model.entity.Exam;
import com.gpnu.exam.exam.model.vo.AnswerSheetVO;
import com.gpnu.exam.exam.model.vo.ExamStatsVO;
import com.gpnu.exam.exam.model.vo.ExamVO;

import java.util.List;

public interface IExamService extends IService<Exam> {

    ExamVO createExam(Long teacherId, ExamCreateRequest request);

    ExamVO updateExam(Long teacherId, Long examId, ExamUpdateRequest request);

    void deleteExam(Long teacherId, Long examId);

    Page<ExamVO> listExams(ExamQueryRequest request);

    ExamStatsVO getExamStats(Long teacherId, Long examId);

    List<AnswerSheetVO> listExamSheets(Long teacherId, Long examId);

    // ----- Feign -----

    ExamSimpleDTO getExamSimple(Long examId);
}
