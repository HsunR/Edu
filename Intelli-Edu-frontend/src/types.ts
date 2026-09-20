export type UserRole = 'Student' | 'Teacher' | 'Admin'
/** 后端使用 19 位雪花 ID。网络层会将其无损解析为字符串。 */
export type EntityId = string | number

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
  userId: EntityId
  name: string
  userType: UserRole
  /** 后端查询返回中文枚举，更新接口接收数字 code。 */
  sex?: number | '未知' | '男' | '女'
  avatarUrl?: string
  personalSignature?: string
  school?: string
  email?: string
  mobile?: string
  status?: number | '正常' | '禁止'
  studentProfile?: StudentProfile
  teacherProfile?: TeacherProfile
}
export interface LoginResult {
  userId: EntityId
  userType: UserRole
  accessToken: string
  refreshToken: string
}

export interface SectionResource {
  id: EntityId
  resourceId: EntityId
  resourceType: string
  orderIndex: number
}
export interface ResourceSummary {
  resourceId: EntityId
  resourceName: string
  resourceType: number
  fileFormat: string
  accessUrl?: string
  fileSize: number
}
export interface Section {
  sectionId: EntityId
  chapterId: EntityId
  title: string
  orderIndex: number
  isFree: number
  resources?: SectionResource[]
  resourceDetails?: ResourceSummary[]
}
export interface Chapter {
  chapterId: EntityId
  courseId: EntityId
  title: string
  orderIndex: number
  sections?: Section[]
}
export interface Course {
  courseId: EntityId
  courseName: string
  coverUrl?: string
  description?: string
  teacherId: EntityId
  teacherName?: string
  teacherAvatar?: string
  categoryId?: EntityId
  categoryName?: string
  status: number
  isPublic: number
  createdAt: string
  chapters?: Chapter[]
}
export interface Category {
  categoryId: EntityId
  categoryName: string
  parentId?: EntityId
  children?: Category[]
}
export interface CourseClass {
  classId: EntityId
  courseId: EntityId
  courseName: string
  className: string
  teacherId: EntityId
  teacherName: string
  inviteCode?: string
  maxStudents: number
  currentStudents: number
  startDate?: string
  endDate?: string
  status: number
  createdAt: string
}
export interface ClassMember {
  id: EntityId
  studentId: EntityId
  studentName: string
  avatarUrl?: string
  status: number
  joinedAt: string
}
export interface Exam {
  examId: EntityId
  examName: string
  paperId: EntityId
  paperName?: string
  classId: EntityId
  courseId: EntityId
  teacherId: EntityId
  examType: number
  startTime: string
  endTime: string
  durationMinutes: number
  allowLateSubmit: boolean
  status: number
  createdAt: string
}
export interface AnswerRecord {
  recordId?: EntityId
  questionId: EntityId
  answerContent?: string
  score?: number
  isCorrect?: boolean
  gradingStatus?: number
  graderId?: number
  comment?: string
  questionType?: number
  stem?: string
  questionScore?: number
  correctAnswer?: string
}
export interface AnswerSheet {
  sheetId: EntityId
  examId: EntityId
  examName?: string
  studentId: EntityId
  studentName?: string
  status: number
  totalScore: number
  objectiveScore: number
  subjectiveScore: number
  submitCount: number
  startAnswerTime: string
  submitTime?: string
  deadline?: string
  records?: AnswerRecord[]
}
export interface ResourceItem {
  resourceId: EntityId
  resourceName: string
  resourceType: number
  fileFormat: string
  fileSize: number
  accessUrl?: string
  uploadStatus: number
  createdAt: string
}
export interface PresignedUpload {
  resourceId: EntityId
  uploadUrl: string
  storageKey: string
  accessUrl: string
  expiresIn: number
}
export interface MasteryPoint {
  pointId: EntityId
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
  points?: Array<{ pointId: EntityId; pointName: string; masteryLevel?: number }>
  sections?: unknown[]
  questions?: unknown[]
}
export interface GraphPoint {
  pointId: EntityId
  pointName: string
  parentId?: EntityId
  description?: string
  orderIndex: number
  masteryLevel?: number
  isWeak: boolean
  sectionIds: EntityId[]
  childPoints: GraphPoint[]
}
export interface GraphOverview {
  courseId: EntityId
  classId: EntityId
  weakThreshold: number
  points: GraphPoint[]
}
export interface WrongRecord {
  wrongId: EntityId
  classId: EntityId
  courseId: EntityId
  questionId: EntityId
  examId: EntityId
  questionType: number
  fullScore: number
  earnedScore: number
  wrongType: string
  isResolved: number
  wrongCount: number
  lastWrongAt: string
  points: Array<{ pointId: EntityId; pointName: string }>
}
