import request from '@/utils/request'
import type { KnowledgePointVO, KnowledgeTreeVO, PointCreateRequest, PointUpdateRequest, QuestionBindRequest, SectionBindRequest } from './types'

export function createPoint(data: PointCreateRequest) {
  return request.post<any, KnowledgePointVO>('/api/knowledge/points', data)
}

export function updatePoint(pointId: number, data: PointUpdateRequest) {
  return request.put<any, KnowledgePointVO>(`/api/knowledge/points/${pointId}`, data)
}

export function deletePoint(pointId: number) {
  return request.delete(`/api/knowledge/points/${pointId}`)
}

export function getKnowledgeTree(courseId: number) {
  return request.get<any, KnowledgeTreeVO[]>('/api/knowledge/points/tree', { params: { courseId } })
}

export function getPointQuestions(pointId: number) {
  return request.get<any, number[]>(`/api/knowledge/points/${pointId}/questions`)
}

export function bindQuestions(pointId: number, data: QuestionBindRequest) {
  return request.post(`/api/knowledge/points/${pointId}/questions`, data)
}

export function unbindQuestion(pointId: number, questionId: number) {
  return request.delete(`/api/knowledge/points/${pointId}/questions/${questionId}`)
}

export function getPointSections(pointId: number) {
  return request.get<any, number[]>(`/api/knowledge/points/${pointId}/sections`)
}

export function bindSections(pointId: number, data: SectionBindRequest) {
  return request.post(`/api/knowledge/points/${pointId}/sections`, data)
}

export function unbindSection(pointId: number, sectionId: number) {
  return request.delete(`/api/knowledge/points/${pointId}/sections/${sectionId}`)
}

export function getQuestionPoints(questionId: number) {
  return request.get<any, KnowledgePointVO[]>(`/api/knowledge/questions/${questionId}/points`)
}

export function getSectionPoints(sectionId: number) {
  return request.get<any, KnowledgePointVO[]>(`/api/knowledge/sections/${sectionId}/points`)
}
