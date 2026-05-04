import request from '@/utils/request'

export function listDept(params: any) { return request.get('/system/dept/list', { params }) }
export function getDept(deptId: number) { return request.get(`/system/dept/${deptId}`) }
export function addDept(data: any) { return request.post('/system/dept', data) }
export function updateDept(data: any) { return request.put('/system/dept', data) }
export function delDept(deptId: number) { return request.delete(`/system/dept/${deptId}`) }
export function listDeptExcludeChild(deptId: number) { return request.get(`/system/dept/list/exclude/${deptId}`) }
