import request from '@/utils/request'
import type { SectionVO, SectionDetailVO, SectionCreateRequest, SectionUpdateRequest, SectionResourceAddRequest, OrderItem } from './types'

export function addSection(chapterId: number, data: SectionCreateRequest) {
  return request.post<any, SectionVO>(`/api/course/chapters/${chapterId}/sections`, data)
}

export function updateSection(sectionId: number, data: SectionUpdateRequest) {
  return request.put<any, SectionVO>(`/api/course/sections/${sectionId}`, data)
}

export function deleteSection(sectionId: number) {
  return request.delete(`/api/course/sections/${sectionId}`)
}

export function getSectionDetail(sectionId: number) {
  return request.get<any, SectionDetailVO>(`/api/course/sections/${sectionId}/detail`)
}

export function reorderSections(chapterId: number, items: OrderItem[]) {
  return request.put(`/api/course/chapters/${chapterId}/sections/order`, items)
}

export function addSectionResource(sectionId: number, data: SectionResourceAddRequest) {
  return request.post(`/api/course/sections/${sectionId}/resources`, data)
}

export function removeSectionResource(sectionId: number, id: number) {
  return request.delete(`/api/course/sections/${sectionId}/resources/${id}`)
}

export function reorderSectionResources(sectionId: number, items: OrderItem[]) {
  return request.put(`/api/course/sections/${sectionId}/resources/order`, items)
}
