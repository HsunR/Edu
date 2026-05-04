import request from '@/utils/request'
import type { PageResult } from '@/types/api'
import type { ClassVO, ClassCreateRequest, ClassUpdateRequest, JoinClassRequest, ClassMemberVO } from './types'

export function createClass(data: ClassCreateRequest) {
  return request.post<any, ClassVO>('/api/course/classes', data)
}

export function updateClass(classId: number, data: ClassUpdateRequest) {
  return request.put<any, ClassVO>(`/api/course/classes/${classId}`, data)
}

export function getClassMembers(classId: number, params?: { current?: number; pageSize?: number }) {
  return request.get<any, PageResult<ClassMemberVO>>(`/api/course/classes/${classId}/members`, { params })
}

export function removeMember(classId: number, memberId: number) {
  return request.delete(`/api/course/classes/${classId}/members/${memberId}`)
}

export function quitClass(classId: number) {
  return request.post(`/api/course/classes/${classId}/quit`)
}

export function joinClass(data: JoinClassRequest) {
  return request.post<any, ClassVO>('/api/course/classes/join', data)
}

export function getMyClasses() {
  return request.get<any, ClassVO[]>('/api/course/classes/my')
}
