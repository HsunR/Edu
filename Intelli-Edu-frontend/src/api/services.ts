import axios from 'axios'
import { request } from './client'
import type {
  Category,
  Course,
  CourseClass,
  Exam,
  GraphOverview,
  LoginResult,
  MasteryPoint,
  PageResult,
  PresignedUpload,
  Recommendation,
  ResourceItem,
  User,
  WrongRecord,
} from '@/types'

export const authApi = {
  login: (data: {
    loginType: 2 | 4
    username?: string
    password?: string
    email?: string
    code?: string
  }) => request<LoginResult>({ url: '/api/user/auth/login', method: 'POST', data }),
  sendLoginCode: (data: { loginType: 2; email: string }) =>
    request<void>({ url: '/api/user/auth/login/send-code', method: 'POST', data }),
  register: (data: Record<string, unknown>) =>
    request<void>({ url: '/api/user/auth/register', method: 'POST', data }),
  sendRegisterCode: (data: { registerType: 2; email: string }) =>
    request<void>({ url: '/api/user/auth/register/send-code', method: 'POST', data }),
  logout: () => request<void>({ url: '/api/user/auth/logout', method: 'POST' }),
}

export const userApi = {
  me: () => request<User>({ url: '/api/user/users/me' }),
  update: (data: Partial<User>) =>
    request<void>({ url: '/api/user/users/me', method: 'PUT', data }),
  updateProfile: (data: Record<string, unknown>) =>
    request<void>({ url: '/api/user/users/me/profile', method: 'PUT', data }),
  updatePassword: (data: { oldPassword: string; newPassword: string }) =>
    request<void>({ url: '/api/user/users/me/password', method: 'PUT', data }),
  list: (params: Record<string, unknown>) =>
    request<PageResult<User>>({ url: '/api/user/users', params }),
  remove: (userId: number) => request<void>({ url: `/api/user/users/${userId}`, method: 'DELETE' }),
  assignTeacher: (data: Record<string, unknown>) =>
    request<void>({ url: `/api/user/users/${data.userId}/assign-teacher`, method: 'PUT', data }),
}

export const courseApi = {
  list: (params: Record<string, unknown> = {}) =>
    request<PageResult<Course>>({ url: '/api/course/courses', params }),
  detail: (courseId: number) => request<Course>({ url: `/api/course/courses/${courseId}` }),
  categories: () => request<Category[]>({ url: '/api/course/categories/' }),
  mine: () => request<CourseClass[]>({ url: '/api/course/classes/my' }),
  join: (inviteCode: string) =>
    request<CourseClass>({ url: '/api/course/classes/join', method: 'POST', data: { inviteCode } }),
  quit: (classId: number) =>
    request<void>({ url: `/api/course/classes/${classId}/quit`, method: 'POST' }),
  teaching: (params: Record<string, unknown> = {}) =>
    request<PageResult<Course>>({ url: '/api/course/courses/teaching', params }),
  classes: (courseId: number) =>
    request<CourseClass[]>({ url: `/api/course/courses/${courseId}/classes` }),
  create: (data: Record<string, unknown>) =>
    request<Course>({ url: '/api/course/courses', method: 'POST', data }),
  publish: (courseId: number) =>
    request<void>({ url: `/api/course/courses/${courseId}/publish`, method: 'PUT' }),
  archive: (courseId: number) =>
    request<void>({ url: `/api/course/courses/${courseId}/archive`, method: 'PUT' }),
}

export const examApi = {
  list: (params: Record<string, unknown> = {}) =>
    request<PageResult<Exam>>({ url: '/api/exam/exams', params }),
  enter: (examId: number) =>
    request<unknown>({ url: `/api/exam/answers/exams/${examId}/enter`, method: 'POST' }),
  mySheet: (examId: number) =>
    request<unknown>({ url: `/api/exam/answers/exams/${examId}/my-sheet` }),
}

export const resourceApi = {
  list: (params: Record<string, unknown> = {}) =>
    request<PageResult<ResourceItem>>({ url: '/api/resource/resources', params }),
  remove: (resourceId: number) =>
    request<void>({ url: `/api/resource/resources/${resourceId}`, method: 'DELETE' }),
  presign: (type: 'image' | 'document' | 'video', file: File) =>
    request<PresignedUpload>({
      url: `/api/resource/resources/presign/${type}`,
      method: 'POST',
      data: { fileName: file.name, fileSize: file.size },
    }),
  upload: async (url: string, file: File) => {
    await axios.put(url, file, {
      headers: { 'Content-Type': file.type || 'application/octet-stream' },
    })
  },
  confirm: (resourceId: number, video = false) =>
    request<ResourceItem>({
      url: `/api/resource/resources/confirm${video ? '/video' : ''}`,
      method: 'POST',
      data: { resourceId },
    }),
}

export const learningApi = {
  mastery: (classId: number) =>
    request<MasteryPoint[]>({ url: '/api/learning/student/mastery/overview', params: { classId } }),
  weakPoints: (classId: number) =>
    request<MasteryPoint[]>({
      url: '/api/learning/student/mastery/weak-points',
      params: { classId },
    }),
  wrongs: (params: Record<string, unknown>) =>
    request<PageResult<WrongRecord>>({ url: '/api/learning/student/wrongs', params }),
  resolveWrong: (wrongId: number) =>
    request<void>({ url: `/api/learning/student/wrongs/${wrongId}/resolve`, method: 'PUT' }),
  recommend: (params: {
    classId: number
    courseId: number
    scene: 'REVIEW_WEAK' | 'REVIEW_WRONG' | 'DAILY_PLAN'
    limit?: number
  }) => request<Recommendation>({ url: '/api/learning/student/recommend', params }),
  graph: (classId: number, courseId: number) =>
    request<GraphOverview>({
      url: '/api/learning/student/graph/overview',
      params: { classId, courseId },
    }),
  teacherMastery: (classId: number) =>
    request<MasteryPoint[]>({
      url: '/api/learning/teacher/mastery/class-overview',
      params: { classId },
    }),
  teacherRecommend: (classId: number) =>
    request<Recommendation>({ url: '/api/learning/teacher/recommend', params: { classId } }),
}
