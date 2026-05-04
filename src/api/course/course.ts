import request from '@/utils/request'
import type { PageResult } from '@/types/api'
import type { CourseVO, CourseDetailVO, CourseCreateRequest, CourseUpdateRequest, CourseQueryRequest, ClassVO } from './types'

export function getCourseList(params: CourseQueryRequest) {
  return request.get<any, PageResult<CourseVO>>('/api/course/courses', { params })
}

export function getCourseDetail(courseId: number) {
  return request.get<any, CourseDetailVO>(`/api/course/courses/${courseId}`)
}

export function createCourse(data: CourseCreateRequest) {
  return request.post<any, CourseVO>('/api/course/courses', data)
}

export function updateCourse(courseId: number, data: CourseUpdateRequest) {
  return request.put<any, CourseVO>(`/api/course/courses/${courseId}`, data)
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
  return request.get<any, PageResult<CourseVO>>('/api/course/courses/teaching', { params })
}

export function getCourseClasses(courseId: number) {
  return request.get<any, ClassVO[]>(`/api/course/courses/${courseId}/classes`)
}
