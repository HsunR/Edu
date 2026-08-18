import request from '@/api/request'

export function uploadFile(data: FormData) {
  return request.post<string>('/api/ai/upload', data, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
