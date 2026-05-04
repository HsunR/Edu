import request from '@/api/request'
import type { ChatStreamParams, UploadChatParams } from './types'

export function doChatByStream(params: ChatStreamParams) {
  return request.post<string[]>('/api/ai/aiCourse/doChatByStream', null, { params })
}

export function doChatByStreamMono(params: ChatStreamParams) {
  return request.post<string[]>('/api/ai/aiCourse/doChatByStreamMono', null, { params })
}

export function doChatWithRagByStream(params: ChatStreamParams) {
  return request.post<string[]>('/api/ai/aiCourse/doChatWithRagByStream', null, { params })
}

export function doChatWithToolAndRag(params: ChatStreamParams) {
  return request.post<string[]>('/api/ai/aiCourse/doChatWithToolAndRagByStream', null, { params })
}

export function uploadChat(params: UploadChatParams, data: FormData) {
  return request.post<string[]>('/api/ai/aiCourse/uploadChat', data, {
    headers: { 'Content-Type': 'multipart/form-data' },
    params
  })
}
