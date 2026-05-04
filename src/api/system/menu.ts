import request from '@/api/request'
import type { MenuQueryRequest, MenuRequest } from './types'

export function listMenu(params: MenuQueryRequest) { return request.get('/system/menu/list', { params }) }
export function getMenu(menuId: number) { return request.get(`/system/menu/${menuId}`) }
export function addMenu(data: MenuRequest) { return request.post('/system/menu', data) }
export function updateMenu(data: MenuRequest) { return request.put('/system/menu', data) }
export function delMenu(menuId: number) { return request.delete(`/system/menu/${menuId}`) }
export function treeselect() { return request.get('/system/menu/treeselect') }
export function roleMenuTreeselect(roleId: number) { return request.get(`/system/menu/roleMenuTreeselect/${roleId}`) }
