export interface ResourceVO {
  resourceId: number
  resourceName: string
  resourceType: 1 | 2 | 3
  fileFormat: string
  fileSize: number
  accessUrl: string
  uploadStatus: 0 | 1 | 2
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

export interface ResourceQueryRequest {
  current?: number
  pageSize?: number
  sortField?: string
  sortOrder?: string
  resourceName?: string
  resourceType?: 1 | 2 | 3
  fileFormat?: string
  uploadStatus?: '1' | '2' | '3'
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
