import request from '@/api/request'
import type { RoleQueryRequest, RoleRequest, SysUserQueryRequest } from './types'

export function listRole(params: RoleQueryRequest) { return request.get('/system/role/list', { params }) }
export function getRole(roleId: number) { return request.get(`/system/role/${roleId}`) }
export function addRole(data: RoleRequest) { return request.post('/system/role', data) }
export function updateRole(data: RoleRequest) { return request.put('/system/role', data) }
export function delRole(roleId: number) { return request.delete(`/system/role/${roleId}`) }
export function changeRoleStatus(roleId: number, status: string) { return request.put('/system/role/changeStatus', { roleId, status }) }
export function dataScope(data: RoleRequest) { return request.put('/system/role/dataScope', data) }
export function deptTreeSelect() { return request.get('/system/role/deptTree') }
export function allocatedUserList(params: SysUserQueryRequest) { return request.get('/system/role/authUser/allocatedList', { params }) }
export function authUserCancel(data: { userId: number; roleId: number }) { return request.put('/system/role/authUser/cancel', data) }
export function authUserCancelAll(data: { roleId: number; userIds: string }) { return request.put('/system/role/authUser/cancelAll', data) }
export function authUserSelectAll(data: { roleId: number; userIds: string }) { return request.put('/system/role/authUser/selectAll', data) }
export function unallocatedUserList(params: SysUserQueryRequest) { return request.get('/system/role/authUser/unallocatedList', { params }) }
