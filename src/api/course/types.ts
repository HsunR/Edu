import type { ClassStatus, CourseStatus, SectionResourceType, YesNo } from '@/types/enums'
import type { PageRequest } from '@/types/api'

export interface CategoryVO {
  categoryId: number
  name: string
  parentId: number
  orderIndex: number
  children: CategoryVO[]
}

export interface CourseVO {
  courseId: number
  courseName: string
  coverUrl: string
  description: string
  teacherId: number
  teacherName: string
  teacherAvatar: string
  categoryId: number
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
  categoryId?: number
  isPublic?: YesNo
}

export interface CourseUpdateRequest {
  courseName?: string
  description?: string
  coverUrl?: string
  categoryId?: number
  isPublic?: YesNo
}

export interface CourseQueryRequest extends PageRequest {
  categoryId?: number
  courseName?: string
  status?: CourseStatus
}

export interface ChapterVO {
  chapterId: number
  courseId: number
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
  sectionId: number
  chapterId: number
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
  id: number
  sectionId: number
  resourceId: number
  resourceType: SectionResourceType
  orderIndex: number
  resourceName: string
  accessUrl: string
}

export interface ResourceSimpleDTO {
  resourceId: number
  resourceName: string
  resourceType: number
  fileFormat: string
  accessUrl: string
  fileSize: number
}

export interface SectionResourceAddRequest {
  resourceId: number
  resourceType: SectionResourceType
}

export interface ClassVO {
  classId: number
  courseId: number
  courseName: string
  className: string
  teacherId: number
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
  courseId: number
  className: string
  maxStudents?: number
  startDate?: string
  endDate?: string
}

export interface ClassUpdateRequest {
  className: string
  maxStudents?: number
  startDate?: string
  endDate?: string
  status?: ClassStatus
}

export interface JoinClassRequest {
  inviteCode: string
}

export interface ClassMemberVO {
  id: number
  studentId: number
  studentName: string
  avatarUrl: string
  status: number
  joinedAt: string
}

export interface OrderItem {
  id: number
  orderIndex: number
}
