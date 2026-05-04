import request from '@/api/request'
import type { PageResult } from '@/types/api'
import type { QuestionBankVO, QuestionBankCreateRequest, QuestionBankUpdateRequest, QuestionBankQueryRequest, QuestionVO, QuestionCreateRequest, QuestionUpdateRequest, QuestionQueryRequest, PaperVO, PaperDetailVO, PaperCreateRequest, PaperUpdateRequest, PaperQueryRequest, PaperQuestionAddRequest, PaperQuestionOrderRequest, ExamVO, ExamCreateRequest, ExamUpdateRequest, ExamQueryRequest, ExamStatsVO, AnswerSheetVO, AnswerSheetDetailVO, AnswerSaveRequest, GradeRequest } from './types'

export function getQuestionBankList(params: QuestionBankQueryRequest) {
  return request.get<any, PageResult<QuestionBankVO>>('/api/exam/question-banks', { params })
}

export function createQuestionBank(data: QuestionBankCreateRequest) {
  return request.post<any, QuestionBankVO>('/api/exam/question-banks', data)
}

export function updateQuestionBank(bankId: number, data: QuestionBankUpdateRequest) {
  return request.put<any, QuestionBankVO>(`/api/exam/question-banks/${bankId}`, data)
}

export function deleteQuestionBank(bankId: number) {
  return request.delete(`/api/exam/question-banks/${bankId}`)
}

export function getQuestionList(params: QuestionQueryRequest) {
  return request.get<any, PageResult<QuestionVO>>('/api/exam/questions', { params })
}

export function getQuestionDetail(questionId: number) {
  return request.get<any, QuestionVO>(`/api/exam/questions/${questionId}`)
}

export function createQuestion(bankId: number, data: QuestionCreateRequest) {
  return request.post<any, QuestionVO>(`/api/exam/questions/banks/${bankId}`, data)
}

export function updateQuestion(questionId: number, data: QuestionUpdateRequest) {
  return request.put<any, QuestionVO>(`/api/exam/questions/${questionId}`, data)
}

export function deleteQuestion(questionId: number) {
  return request.delete(`/api/exam/questions/${questionId}`)
}

export function getPaperList(params: PaperQueryRequest) {
  return request.get<any, PageResult<PaperVO>>('/api/exam/papers', { params })
}

export function getPaperDetail(paperId: number) {
  return request.get<any, PaperDetailVO>(`/api/exam/papers/${paperId}`)
}

export function createPaper(data: PaperCreateRequest) {
  return request.post<any, PaperVO>('/api/exam/papers', data)
}

export function updatePaper(paperId: number, data: PaperUpdateRequest) {
  return request.put<any, PaperVO>(`/api/exam/papers/${paperId}`, data)
}

export function deletePaper(paperId: number) {
  return request.delete(`/api/exam/papers/${paperId}`)
}

export function publishPaper(paperId: number) {
  return request.put(`/api/exam/papers/${paperId}/publish`)
}

export function addPaperQuestions(paperId: number, data: PaperQuestionAddRequest) {
  return request.post(`/api/exam/papers/${paperId}/questions`, data)
}

export function removePaperQuestion(paperId: number, questionId: number) {
  return request.delete(`/api/exam/papers/${paperId}/questions/${questionId}`)
}

export function reorderPaperQuestions(paperId: number, data: PaperQuestionOrderRequest) {
  return request.put(`/api/exam/papers/${paperId}/questions/order`, data)
}

export function getExamList(params: ExamQueryRequest) {
  return request.get<any, PageResult<ExamVO>>('/api/exam/exams', { params })
}

export function createExam(data: ExamCreateRequest) {
  return request.post<any, ExamVO>('/api/exam/exams', data)
}

export function updateExam(examId: number, data: ExamUpdateRequest) {
  return request.put<any, ExamVO>(`/api/exam/exams/${examId}`, data)
}

export function deleteExam(examId: number) {
  return request.delete(`/api/exam/exams/${examId}`)
}

export function getExamSheets(examId: number) {
  return request.get<any, AnswerSheetVO[]>(`/api/exam/exams/${examId}/sheets`)
}

export function getExamStats(examId: number) {
  return request.get<any, ExamStatsVO>(`/api/exam/exams/${examId}/stats`)
}

export function enterExam(examId: number) {
  return request.post<any, AnswerSheetVO>(`/api/exam/answers/exams/${examId}/enter`)
}

export function getMySheet(examId: number) {
  return request.get<any, AnswerSheetDetailVO>(`/api/exam/answers/exams/${examId}/my-sheet`)
}

export function saveAnswer(sheetId: number, questionId: number, data: AnswerSaveRequest) {
  return request.put(`/api/exam/answers/sheets/${sheetId}/questions/${questionId}`, data)
}

export function submitExam(sheetId: number) {
  return request.post(`/api/exam/answers/sheets/${sheetId}/submit`)
}

export function getSheetDetail(sheetId: number) {
  return request.get<any, AnswerSheetDetailVO>(`/api/exam/exams/sheets/${sheetId}/detail`)
}

export function gradeRecord(recordId: number, data: GradeRequest) {
  return request.put(`/api/exam/exams/records/${recordId}/grade`, data)
}

export function finishGrading(sheetId: number) {
  return request.post(`/api/exam/exams/sheets/${sheetId}/finish-grading`)
}
