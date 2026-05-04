import request from '@/api/request'
import type { DeptQueryRequest, DeptRequest } from './types'

export function listDept(params: DeptQueryRequest) { return request.get('/system/dept/list', { params }) }
export function getDept(deptId: number) { return request.get(`/system/dept/${deptId}`) }
export function addDept(data: DeptRequest) { return request.post('/system/dept', data) }
export function updateDept(data: DeptRequest) { return request.put('/system/dept', data) }
export function delDept(deptId: number) { return request.delete(`/system/dept/${deptId}`) }
export function listDeptExcludeChild(deptId: number) { return request.get(`/system/dept/list/exclude/${deptId}`) }
