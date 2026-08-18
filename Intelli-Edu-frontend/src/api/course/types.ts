import type { ClassStatus, CourseStatus, SectionResourceType, YesNo } from '@/types/enums'
import type { PageRequest } from '@/types/api'

export interface CategoryVO {
  categoryId: string
  name: string
  parentId: string
  orderIndex: number
  children: CategoryVO[]
}

export interface CourseVO {
  courseId: string
  courseName: string
  coverUrl: string
  description: string
  teacherId: string
  teacherName: string
  teacherAvatar: string
  categoryId: string
  categoryName: string
  status: CourseStatus
  isPublic: YesNo
  createdAt: string
}

export interface CourseDetailVO extends CourseVO {
  chapters: ChapterVO[]
}

export interface CourseCreateRequest {
  courseName: string
  description?: string
  coverUrl?: string
  categoryId?: string
  isPublic?: YesNo
}

export interface CourseUpdateRequest {
  courseName?: string
  description?: string
  coverUrl?: string
  categoryId?: string
  isPublic?: YesNo
}

export interface CourseQueryRequest extends PageRequest {
  categoryId?: string
  courseName?: string
  status?: CourseStatus
}

export interface ChapterVO {
  chapterId: string
  courseId: string
  title: string
  orderIndex: number
  sections: SectionVO[]
}

export interface ChapterCreateRequest {
  title: string
}

export interface ChapterUpdateRequest {
  title?: string
}

export interface SectionVO {
  sectionId: string
  chapterId: string
  title: string
  orderIndex: number
  isFree: YesNo
  resources: SectionResourceVO[]
}

export interface SectionDetailVO extends SectionVO {
  resourceDetails: ResourceSimpleDTO[]
}

export interface SectionCreateRequest {
  title: string
  isFree?: YesNo
}

export interface SectionUpdateRequest {
  title?: string
  isFree?: YesNo
}

export interface SectionResourceVO {
  id: string
  sectionId: string
  resourceId: string
  resourceType: SectionResourceType
  orderIndex: number
  resourceName: string
  accessUrl: string
}

export interface ResourceSimpleDTO {
  resourceId: string
  resourceName: string
  resourceType: number
  fileFormat: string
  accessUrl: string
  fileSize: number
}

export interface SectionResourceAddRequest {
  resourceId: string
  resourceType: SectionResourceType
}

export interface ClassVO {
  classId: string
  courseId: string
  courseName: string
  className: string
  teacherId: string
  teacherName: string
  inviteCode: string
  maxStudents: number
  currentStudents: number
  startDate: string
  endDate: string
  status: ClassStatus
  createdAt: string
}

export interface ClassCreateRequest {
  courseId: string
  className: string
  maxStudents?: number
  /** ISO 8601 with timezone, e.g. "2024-09-01T00:00:00+08:00" */
  startDate?: string
  /** ISO 8601 with timezone, e.g. "2025-06-30T23:59:59+08:00" */
  endDate?: string
}

export interface ClassUpdateRequest {
  className: string
  maxStudents?: number
  /** ISO 8601 with timezone, e.g. "2024-09-01T00:00:00+08:00" */
  startDate?: string
  /** ISO 8601 with timezone, e.g. "2025-06-30T23:59:59+08:00" */
  endDate?: string
  status?: ClassStatus
}

export interface JoinClassRequest {
  inviteCode: string
}

export interface ClassMemberVO {
  id: string
  studentId: string
  studentName: string
  avatarUrl: string
  status: number
  joinedAt: string
}

export interface OrderItem {
  id: string
  orderIndex: number
}
