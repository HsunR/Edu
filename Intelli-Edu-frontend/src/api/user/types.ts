import type { LoginType, RegisterType, Sex, UserStatus, UserType } from '@/types/enums'
import type { PageRequest } from '@/types/api'

export interface LoginRequest {
  loginType: LoginType
  username?: string
  password?: string
  mobile?: string
  email?: string
  code?: string
  openId?: string
}

export interface LoginResult {
  userId: string
  userType: UserType
  accessToken: string
  refreshToken: string
}

export interface SendLoginCodeRequest {
  loginType: LoginType
  mobile?: string
  email?: string
}

export interface RegisterRequest {
  name: string
  password: string
  registerType: RegisterType
  mobile?: string
  email?: string
  code?: string
  studentNo: string
  grade?: string
  major?: string
  enrollmentYear?: number
  school?: string
}

export interface SendRegisterCodeRequest {
  registerType: RegisterType
  mobile?: string
  email?: string
}

export interface UserVO {
  userId: string
  name: string
  userType: UserType
  sex: Sex
  avatarUrl: string
  personalSignature: string
  school: string
}

export interface UserDetailVO {
  userId: string
  name: string
  userType: UserType
  sex: Sex
  avatarUrl: string
  personalSignature: string
  school: string
  email: string
  mobile: string
  status: UserStatus
  studentProfile?: StudentProfileVO
  teacherProfile?: TeacherProfileVO
}

export interface StudentProfileVO {
  studentNo: string
  grade: string
  major: string
  enrollmentYear: number
}

export interface TeacherProfileVO {
  teacherNo: string
  title: string
  department: string
  bio: string
}

export interface UserUpdateRequest {
  name?: string
  sex?: Sex
  school?: string
  personalSignature?: string
}

export interface PasswordUpdateRequest {
  oldPassword: string
  newPassword: string
}

export interface ProfileUpdateRequest {
  studentNo?: string
  grade?: string
  major?: string
  enrollmentYear?: number
  teacherNo?: string
  title?: string
  department?: string
  bio?: string
}

export interface AssignTeacherRequest {
  userId: string
  teacherNo: string
  title?: string
  department?: string
  bio?: string
}

export interface UserQueryRequest extends PageRequest {
  userId?: string
  name?: string
  userType?: UserType
  sex?: Sex
  email?: string
  mobile?: string
  school?: string
  status?: UserStatus
}
