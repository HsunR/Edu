import request from '@/api/request'
import type { NoticeQueryRequest, NoticeRequest } from './types'

export function listNotice(params: NoticeQueryRequest) { return request.get('/system/notice/list', { params }) }
export function getNotice(noticeId: string) { return request.get(`/system/notice/${noticeId}`) }
export function addNotice(data: NoticeRequest) { return request.post('/system/notice', data) }
export function updateNotice(data: NoticeRequest) { return request.put('/system/notice', data) }
export function delNotice(noticeId: string) { return request.delete(`/system/notice/${noticeId}`) }
