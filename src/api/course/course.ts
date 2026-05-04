import request from '@/api/request'
import type { PageResult } from '@/types/api'
import type { CourseVO, CourseDetailVO, CourseCreateRequest, CourseUpdateRequest, CourseQueryRequest, ClassVO } from './types'

export function getCourseList(params: CourseQueryRequest) {
  return request.get<PageResult<CourseVO>>('/api/course/courses', { params })
}

export function getCourseDetail(courseId: number) {
  return request.get<CourseDetailVO>(`/api/course/courses/${courseId}`)
}

export function createCourse(data: CourseCreateRequest) {
  return request.post<CourseVO>('/api/course/courses', data)
}

export function updateCourse(courseId: number, data: CourseUpdateRequest) {
  return request.put<CourseVO>(`/api/course/courses/${courseId}`, data)
}

export function deleteCourse(courseId: number) {
  return request.delete(`/api/course/courses/${courseId}`)
}

export function publishCourse(courseId: number) {
  return request.put(`/api/course/courses/${courseId}/publish`)
}

export function archiveCourse(courseId: number) {
  return request.put(`/api/course/courses/${courseId}/archive`)
}

export function getTeachingCourses(params: CourseQueryRequest) {
  return request.get<PageResult<CourseVO>>('/api/course/courses/teaching', { params })
}

export function getCourseClasses(courseId: number) {
  return request.get<ClassVO[]>(`/api/course/courses/${courseId}/classes`)
}

export function getCourseBrief(courseId: number) {
  return request.get<CourseVO>(`/api/course/courses/${courseId}/brief`)
}

export function getBatchCourseBrief(data: number[]) {
  return request.post<CourseVO[]>('/api/course/courses/batch', data)
}

export function checkMemberInClass(classId: number) {
  return request.get<boolean>(`/api/course/classes/${classId}/check-member`)
}
