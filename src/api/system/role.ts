import request from '@/utils/request'

export function listRole(params: any) { return request.get('/system/role/list', { params }) }
export function getRole(roleId: number) { return request.get(`/system/role/${roleId}`) }
export function addRole(data: any) { return request.post('/system/role', data) }
export function updateRole(data: any) { return request.put('/system/role', data) }
export function delRole(roleId: number) { return request.delete(`/system/role/${roleId}`) }
export function changeRoleStatus(roleId: number, status: string) { return request.put('/system/role/changeStatus', { roleId, status }) }
export function dataScope(data: any) { return request.put('/system/role/dataScope', data) }
export function deptTreeSelect() { return request.get('/system/role/deptTree') }
export function allocatedUserList(params: any) { return request.get('/system/role/authUser/allocatedList', { params }) }
export function authUserCancel(data: any) { return request.put('/system/role/authUser/cancel', data) }
export function authUserCancelAll(data: any) { return request.put('/system/role/authUser/cancelAll', data) }
export function authUserSelectAll(data: any) { return request.put('/system/role/authUser/selectAll', data) }
export function unallocatedUserList(params: any) { return request.get('/system/role/authUser/unallocatedList', { params }) }
