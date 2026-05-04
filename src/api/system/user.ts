import request from '@/utils/request'

export function getAuthRole(userId: number) {
  return request.get(`/system/user/authRole/${userId}`)
}

export function updateAuthRole(data: any) {
  return request.put('/system/user/authRole', data)
}

export function changeUserStatus(userId: number, status: string) {
  return request.put('/system/user/changeStatus', { userId, status })
}

export function listUser(params: any) {
  return request.get('/system/user/list', { params })
}

export function resetUserPwd(userId: number, password: string) {
  return request.put('/system/user/resetPwd', { userId, password })
}

export function delUser(userId: number) {
  return request.delete(`/system/user/${userId}`)
}

export function getUser(userId: number) {
  return request.get(`/system/user/${userId}`)
}

export function updateUser(data: any) {
  return request.put('/system/user', data)
}

export function addUser(data: any) {
  return request.post('/system/user', data)
}

export function deptTreeSelect() {
  return request.get('/system/user/deptTree')
}
