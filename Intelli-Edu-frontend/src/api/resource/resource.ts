import request from '@/api/request'
import type { PageResult } from '@/types/api'
import type { ResourceVO, ResourceDetailVO, ResourceQueryRequest, PresignRequest, PresignedUrlVO, UploadConfirmRequest } from './types'

export function getResourceList(params: ResourceQueryRequest) {
  return request.get<any, PageResult<ResourceVO>>('/api/resource/resources', { params })
}

export function getResourceDetail(resourceId: string) {
  return request.get<any, ResourceDetailVO>(`/api/resource/resources/${resourceId}`)
}

export function deleteResource(resourceId: string) {
  return request.delete(`/api/resource/resources/${resourceId}`)
}

export function confirmUpload(data: UploadConfirmRequest) {
  return request.post<any, ResourceVO>('/api/resource/resources/confirm', data)
}

export function confirmVideoUpload(data: UploadConfirmRequest) {
  return request.post<any, ResourceDetailVO>('/api/resource/resources/confirm/video', data)
}

export function presignDocument(data: PresignRequest) {
  return request.post<any, PresignedUrlVO>('/api/resource/resources/presign/document', data)
}

export function presignImage(data: PresignRequest) {
  return request.post<any, PresignedUrlVO>('/api/resource/resources/presign/image', data)
}

export function presignVideo(data: PresignRequest) {
  return request.post<any, PresignedUrlVO>('/api/resource/resources/presign/video', data)
}
