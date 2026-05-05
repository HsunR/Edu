import request from '@/api/request'
import type { PageResult } from '@/types/api'
import type { QuestionBankVO, QuestionBankCreateRequest, QuestionBankUpdateRequest, QuestionBankQueryRequest, QuestionVO, QuestionCreateRequest, QuestionUpdateRequest, QuestionQueryRequest, PaperVO, PaperDetailVO, PaperCreateRequest, PaperUpdateRequest, PaperQueryRequest, PaperQuestionAddRequest, PaperQuestionOrderRequest, ExamVO, ExamCreateRequest, ExamUpdateRequest, ExamQueryRequest, ExamStatsVO, AnswerSheetVO, AnswerSheetDetailVO, AnswerSaveRequest, GradeRequest } from './types'

export function getQuestionBankList(params: QuestionBankQueryRequest) {
  return request.get<PageResult<QuestionBankVO>>('/api/exam/question-banks', { params })
}

export function createQuestionBank(data: QuestionBankCreateRequest) {
  return request.post<QuestionBankVO>('/api/exam/question-banks', data)
}

export function updateQuestionBank(bankId: string, data: QuestionBankUpdateRequest) {
  return request.put<QuestionBankVO>(`/api/exam/question-banks/${bankId}`, data)
}

export function deleteQuestionBank(bankId: string) {
  return request.delete(`/api/exam/question-banks/${bankId}`)
}

export function getQuestionList(params: QuestionQueryRequest) {
  return request.get<PageResult<QuestionVO>>('/api/exam/questions', { params })
}

export function getQuestionDetail(questionId: string) {
  return request.get<QuestionVO>(`/api/exam/questions/${questionId}`)
}

export function createQuestion(bankId: string, data: QuestionCreateRequest) {
  return request.post<QuestionVO>(`/api/exam/questions/banks/${bankId}`, data)
}

export function updateQuestion(questionId: string, data: QuestionUpdateRequest) {
  return request.put<QuestionVO>(`/api/exam/questions/${questionId}`, data)
}

export function deleteQuestion(questionId: string) {
  return request.delete(`/api/exam/questions/${questionId}`)
}

export function getPaperList(params: PaperQueryRequest) {
  return request.get<PageResult<PaperVO>>('/api/exam/papers', { params })
}

export function getPaperDetail(paperId: string) {
  return request.get<PaperDetailVO>(`/api/exam/papers/${paperId}`)
}

export function createPaper(data: PaperCreateRequest) {
  return request.post<PaperVO>('/api/exam/papers', data)
}

export function updatePaper(paperId: string, data: PaperUpdateRequest) {
  return request.put<PaperVO>(`/api/exam/papers/${paperId}`, data)
}

export function deletePaper(paperId: string) {
  return request.delete(`/api/exam/papers/${paperId}`)
}

export function publishPaper(paperId: string) {
  return request.put(`/api/exam/papers/${paperId}/publish`)
}

export function addPaperQuestions(paperId: string, data: PaperQuestionAddRequest) {
  return request.post(`/api/exam/papers/${paperId}/questions`, data)
}

export function removePaperQuestion(paperId: string, questionId: string) {
  return request.delete(`/api/exam/papers/${paperId}/questions/${questionId}`)
}

export function reorderPaperQuestions(paperId: string, data: PaperQuestionOrderRequest) {
  return request.put(`/api/exam/papers/${paperId}/questions/order`, data)
}

export function getExamList(params: ExamQueryRequest) {
  return request.get<PageResult<ExamVO>>('/api/exam/exams', { params })
}

export function createExam(data: ExamCreateRequest) {
  return request.post<ExamVO>('/api/exam/exams', data)
}

export function updateExam(examId: string, data: ExamUpdateRequest) {
  return request.put<ExamVO>(`/api/exam/exams/${examId}`, data)
}

export function deleteExam(examId: string) {
  return request.delete(`/api/exam/exams/${examId}`)
}

export function getExamSheets(examId: string) {
  return request.get<AnswerSheetVO[]>(`/api/exam/exams/${examId}/sheets`)
}

export function getExamStats(examId: string) {
  return request.get<ExamStatsVO>(`/api/exam/exams/${examId}/stats`)
}

export function enterExam(examId: string) {
  return request.post<AnswerSheetVO>(`/api/exam/answers/exams/${examId}/enter`)
}

export function getMySheet(examId: string) {
  return request.get<AnswerSheetDetailVO>(`/api/exam/answers/exams/${examId}/my-sheet`)
}

export function saveAnswer(sheetId: string, questionId: string, data: AnswerSaveRequest) {
  return request.put(`/api/exam/answers/sheets/${sheetId}/questions/${questionId}`, data)
}

export function submitExam(sheetId: string) {
  return request.post(`/api/exam/answers/sheets/${sheetId}/submit`)
}

export function getSheetDetail(sheetId: string) {
  return request.get<AnswerSheetDetailVO>(`/api/exam/exams/sheets/${sheetId}/detail`)
}

export function gradeRecord(recordId: string, data: GradeRequest) {
  return request.put(`/api/exam/exams/records/${recordId}/grade`, data)
}

export function finishGrading(sheetId: string) {
  return request.post(`/api/exam/exams/sheets/${sheetId}/finish-grading`)
}
