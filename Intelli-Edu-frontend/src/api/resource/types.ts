import type { ResourceType, UploadStatus } from '@/types/enums'
import type { PageRequest } from '@/types/api'

export interface ResourceVO {
  resourceId: string
  resourceName: string
  resourceType: ResourceType
  fileFormat: string
  fileSize: number
  accessUrl: string
  uploadStatus: UploadStatus
  createdAt: string
}

export interface ResourceDetailVO extends ResourceVO {
  uploaderId: string
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
  resourceId: string
  uploadUrl: string
  storageKey: string
  accessUrl: string
  expiresIn: number
}

export interface UploadConfirmRequest {
  resourceId: string
}
