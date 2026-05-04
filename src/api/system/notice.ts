import request from '@/utils/request'

export function listNotice(params: any) { return request.get('/system/notice/list', { params }) }
export function getNotice(noticeId: number) { return request.get(`/system/notice/${noticeId}`) }
export function addNotice(data: any) { return request.post('/system/notice', data) }
export function updateNotice(data: any) { return request.put('/system/notice', data) }
export function delNotice(noticeId: number) { return request.delete(`/system/notice/${noticeId}`) }
