export type UserRole = 'Student' | 'Teacher' | 'Admin'

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}
export interface StudentProfile {
  studentNo?: string
  grade?: string
  major?: string
  enrollmentYear?: number
}
export interface TeacherProfile {
  teacherNo?: string
  title?: string
  department?: string
  bio?: string
}
export interface User {
  userId: number
  name: string
  userType: UserRole
  sex?: number
  avatarUrl?: string
  personalSignature?: string
  school?: string
  email?: string
  mobile?: string
  status?: number
  studentProfile?: StudentProfile
  teacherProfile?: TeacherProfile
}
export interface LoginResult {
  userId: number
  userType: UserRole
  accessToken: string
  refreshToken: string
}

export interface SectionResource {
  id: number
  resourceId: number
  resourceType: string
  orderIndex: number
}
export interface Section {
  sectionId: number
  chapterId: number
  title: string
  orderIndex: number
  isFree: number
  resources?: SectionResource[]
}
export interface Chapter {
  chapterId: number
  courseId: number
  title: string
  orderIndex: number
  sections?: Section[]
}
export interface Course {
  courseId: number
  courseName: string
  coverUrl?: string
  description?: string
  teacherId: number
  teacherName?: string
  teacherAvatar?: string
  categoryId?: number
  categoryName?: string
  status: number
  isPublic: number
  createdAt: string
  chapters?: Chapter[]
}
export interface Category {
  categoryId: number
  categoryName: string
  parentId?: number
  children?: Category[]
}
export interface CourseClass {
  classId: number
  courseId: number
  courseName: string
  className: string
  teacherId: number
  teacherName: string
  inviteCode?: string
  maxStudents: number
  currentStudents: number
  startDate?: string
  endDate?: string
  status: number
  createdAt: string
}
export interface Exam {
  examId: number
  examName: string
  paperId: number
  paperName?: string
  classId: number
  courseId: number
  teacherId: number
  examType: number
  startTime: string
  endTime: string
  durationMinutes: number
  allowLateSubmit: boolean
  status: number
  createdAt: string
}
export interface ResourceItem {
  resourceId: number
  resourceName: string
  resourceType: number
  fileFormat: string
  fileSize: number
  accessUrl?: string
  uploadStatus: number
  createdAt: string
}
export interface PresignedUpload {
  resourceId: number
  uploadUrl: string
  storageKey: string
  accessUrl: string
  expiresIn: number
}
export interface MasteryPoint {
  pointId: number
  pointName: string
  masteryLevel?: number
  avgMasteryLevel?: number
  studentCount?: number
  weakStudentCount?: number
  totalScore?: number
  earnedScore?: number
  answerCount?: number
  correctCount?: number
}
export interface Recommendation {
  scene: string
  points?: Array<{ pointId: number; pointName: string; masteryLevel?: number }>
  sections?: unknown[]
  questions?: unknown[]
}
export interface GraphPoint {
  pointId: number
  pointName: string
  parentId?: number
  description?: string
  orderIndex: number
  masteryLevel?: number
  isWeak: boolean
  sectionIds: number[]
  childPoints: GraphPoint[]
}
export interface GraphOverview {
  courseId: number
  classId: number
  weakThreshold: number
  points: GraphPoint[]
}
export interface WrongRecord {
  wrongId: number
  classId: number
  courseId: number
  questionId: number
  examId: number
  questionType: number
  fullScore: number
  earnedScore: number
  wrongType: string
  isResolved: number
  wrongCount: number
  lastWrongAt: string
  points: Array<{ pointId: number; pointName: string }>
}
