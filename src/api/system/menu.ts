import request from '@/utils/request'

export function listMenu(params: any) { return request.get('/system/menu/list', { params }) }
export function getMenu(menuId: number) { return request.get(`/system/menu/${menuId}`) }
export function addMenu(data: any) { return request.post('/system/menu', data) }
export function updateMenu(data: any) { return request.put('/system/menu', data) }
export function delMenu(menuId: number) { return request.delete(`/system/menu/${menuId}`) }
export function treeselect() { return request.get('/system/menu/treeselect') }
export function roleMenuTreeselect(roleId: number) { return request.get(`/system/menu/roleMenuTreeselect/${roleId}`) }
