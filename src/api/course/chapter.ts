import request from '@/api/request'
import type { CategoryVO, ChapterVO, ChapterCreateRequest, ChapterUpdateRequest, OrderItem } from './types'

export function getCategoryTree() {
  return request.get<CategoryVO[]>('/api/course/categories/')
}

export function addChapter(courseId: number, data: ChapterCreateRequest) {
  return request.post<ChapterVO>(`/api/course/courses/${courseId}/chapters`, data)
}

export function updateChapter(chapterId: number, data: ChapterUpdateRequest) {
  return request.put<ChapterVO>(`/api/course/chapters/${chapterId}`, data)
}

export function deleteChapter(chapterId: number) {
  return request.delete(`/api/course/chapters/${chapterId}`)
}

export function reorderChapters(courseId: number, items: OrderItem[]) {
  return request.put(`/api/course/courses/${courseId}/chapters/order`, items)
}
