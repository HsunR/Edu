export interface LoginRequest {
  loginType: '1' | '2' | '3' | '4'
  username?: string
  password?: string
  mobile?: string
  email?: string
  code?: string
  openId?: string
}

export interface LoginResult {
  userId: number
  userType: 'Student' | 'Teacher' | 'Admin'
  accessToken: string
  refreshToken: string
}

export interface SendLoginCodeRequest {
  loginType: number
  mobile?: string
  email?: string
}

export interface RegisterRequest {
  name: string
  password: string
  registerType: '手机验证码注册' | '邮箱验证码注册' | '微信OpenID注册'
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
  registerType: '手机验证码注册' | '邮箱验证码注册' | '微信OpenID注册'
  mobile?: string
  email?: string
}

export interface UserVO {
  userId: number
  name: string
  type: 'Student' | 'Teacher' | 'Admin'
  sex: '未知' | '男' | '女'
  avatarUrl: string
  personalSignature: string
  school: string
}

export interface UserDetailVO {
  userId: number
  name: string
  type: 'Student' | 'Teacher' | 'Admin'
  sex: '未知' | '男' | '女'
  avatarUrl: string
  personalSignature: string
  school: string
  email: string
  mobile: string
  status: '正常' | '禁止'
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
  sex?: '未知' | '男' | '女'
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
  userId: number
  teacherNo: string
  title?: string
  department?: string
  bio?: string
}

export interface UserQueryRequest {
  current?: number
  pageSize?: number
  sortField?: string
  sortOrder?: string
  userId?: number
  name?: string
  userType?: string
  sex?: number
  email?: string
  mobile?: string
  school?: string
  status?: number
}
