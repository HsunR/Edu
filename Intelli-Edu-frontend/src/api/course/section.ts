import request from '@/api/request'
import type { SectionVO, SectionDetailVO, SectionCreateRequest, SectionUpdateRequest, SectionResourceAddRequest, OrderItem } from './types'

export function addSection(chapterId: string, data: SectionCreateRequest) {
  return request.post<SectionVO>(`/api/course/chapters/${chapterId}/sections`, data)
}

export function updateSection(sectionId: string, data: SectionUpdateRequest) {
  return request.put<SectionVO>(`/api/course/sections/${sectionId}`, data)
}

export function deleteSection(sectionId: string) {
  return request.delete(`/api/course/sections/${sectionId}`)
}

export function getSectionDetail(sectionId: string) {
  return request.get<SectionDetailVO>(`/api/course/sections/${sectionId}/detail`)
}

export function reorderSections(chapterId: string, items: OrderItem[]) {
  return request.put(`/api/course/chapters/${chapterId}/sections/order`, items)
}

export function addSectionResource(sectionId: string, data: SectionResourceAddRequest) {
  return request.post(`/api/course/sections/${sectionId}/resources`, data)
}

export function removeSectionResource(sectionId: string, id: string) {
  return request.delete(`/api/course/sections/${sectionId}/resources/${id}`)
}

export function reorderSectionResources(sectionId: string, items: OrderItem[]) {
  return request.put(`/api/course/sections/${sectionId}/resources/order`, items)
}
