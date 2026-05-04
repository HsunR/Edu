export interface PageRequest {
  current?: number
  pageSize?: number
  sortField?: string
  sortOrder?: 'asc' | 'desc'
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export enum ErrorCode {
  SUCCESS = 0,
  PARAMS_ERROR = 40000,
  NOT_LOGIN_ERROR = 40100,
  NO_AUTH_ERROR = 40101,
  NOT_FOUND_ERROR = 40400,
  FORBIDDEN_ERROR = 40300,
  SYSTEM_ERROR = 50000,
  OPERATION_ERROR = 50001,
}

export interface ApiResponse<T = unknown> {
  code: ErrorCode
  data: T
  message: string
}
