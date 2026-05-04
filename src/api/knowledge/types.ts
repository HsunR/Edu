export interface KnowledgePointVO {
  pointId: number
  pointName: string
  courseId: number
  parentId: number
  description: string
  orderIndex: number
  createdAt: string
  updatedAt: string
}

export interface KnowledgeTreeVO {
  pointId: number
  pointName: string
  courseId: number
  parentId: number
  description: string
  orderIndex: number
  children: KnowledgeTreeVO[]
}

export interface PointCreateRequest {
  pointName: string
  courseId: number
  parentId?: number
  description?: string
  orderIndex?: number
}

export interface PointUpdateRequest {
  pointName?: string
  description?: string
  orderIndex?: number
}

export interface QuestionBindRequest {
  questionIds: number[]
}

export interface SectionBindRequest {
  sectionIds: number[]
}
