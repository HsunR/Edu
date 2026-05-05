import request from '@/api/request'
import type { PageResult } from '@/types/api'
import type { UserVO, UserDetailVO, UserUpdateRequest, PasswordUpdateRequest, ProfileUpdateRequest, AssignTeacherRequest, UserQueryRequest } from './types'

export function getUserList(params: UserQueryRequest) {
  return request.get<PageResult<UserVO>>('/api/user/users', { params })
}

export function getUserInfo() {
  return request.get<UserDetailVO>('/api/user/users/me')
}

export function getUserById(userId: string) {
  return request.get<UserVO>(`/api/user/users/${userId}`)
}

export function updateUserInfo(data: UserUpdateRequest) {
  return request.put('/api/user/users/me', data)
}

export function updateAvatar(avatarUrl: string) {
  return request.put('/api/user/users/me/avatar', null, { params: { avatarUrl } })
}

export function updatePassword(data: PasswordUpdateRequest) {
  return request.put('/api/user/users/me/password', data)
}

export function updateProfile(data: ProfileUpdateRequest) {
  return request.put('/api/user/users/me/profile', data)
}

export function deleteUser(userId: string) {
  return request.delete(`/api/user/users/${userId}`)
}

export function assignTeacher(userId: string, data: AssignTeacherRequest) {
  return request.put(`/api/user/users/${userId}/assign-teacher`, data)
}
