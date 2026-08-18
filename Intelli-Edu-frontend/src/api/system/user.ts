import request from '@/api/request'
import type { AuthRoleRequest, SysUserQueryRequest, SysUserRequest } from './types'

export function getAuthRole(userId: string) {
  return request.get(`/system/user/authRole/${userId}`)
}

export function updateAuthRole(data: AuthRoleRequest) {
  return request.put('/system/user/authRole', data)
}

export function changeUserStatus(userId: string, status: string) {
  return request.put('/system/user/changeStatus', { userId, status })
}

export function listUser(params: SysUserQueryRequest) {
  return request.get('/system/user/list', { params })
}

export function resetUserPwd(userId: string, password: string) {
  return request.put('/system/user/resetPwd', { userId, password })
}

export function delUser(userId: string) {
  return request.delete(`/system/user/${userId}`)
}

export function getUser(userId: string) {
  return request.get(`/system/user/${userId}`)
}

export function updateUser(data: SysUserRequest) {
  return request.put('/system/user', data)
}

export function addUser(data: SysUserRequest) {
  return request.post('/system/user', data)
}

export function deptTreeSelect() {
  return request.get('/system/user/deptTree')
}
