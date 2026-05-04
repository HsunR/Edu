import type { ResourceType, UploadStatus } from '@/types/enums'
import type { PageRequest } from '@/types/api'

export interface ResourceVO {
  resourceId: number
  resourceName: string
  resourceType: ResourceType
  fileFormat: string
  fileSize: number
  accessUrl: string
  uploadStatus: UploadStatus
  createdAt: string
}

export interface ResourceDetailVO extends ResourceVO {
  uploaderId: number
  videoMeta?: VideoMetaVO
}

export interface VideoMetaVO {
  duration: number
  coverUrl: string
  definition: string
  transcodeStatus: number
}

export interface ResourceQueryRequest extends PageRequest {
  resourceName?: string
  resourceType?: ResourceType
  fileFormat?: string
  uploadStatus?: UploadStatus
  createdFrom?: string
  createdTo?: string
}

export interface PresignRequest {
  fileName: string
  fileSize: number
}

export interface PresignedUrlVO {
  resourceId: number
  uploadUrl: string
  storageKey: string
  accessUrl: string
  expiresIn: number
}

export interface VodPresignedUrlVO {
  resourceId: number
  vodSessionKey: string
  mediaUploadUrls: string[]
  coverUploadUrl: string
  expiresIn: number
}

export interface UploadConfirmRequest {
  resourceId: number
}

export interface VideoConfirmRequest {
  resourceId: number
  vodSessionKey: string
}
