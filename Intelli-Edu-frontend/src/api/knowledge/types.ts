export interface KnowledgePointVO {
  pointId: string
  pointName: string
  courseId: string
  parentId: string
  description: string
  orderIndex: number
  createdAt: string
  updatedAt: string
}

export interface KnowledgeTreeVO {
  pointId: string
  pointName: string
  courseId: string
  parentId: string
  description: string
  orderIndex: number
  children: KnowledgeTreeVO[]
}

export interface PointCreateRequest {
  pointName: string
  courseId: string
  parentId?: string
  description?: string
  orderIndex?: number
}

export interface PointUpdateRequest {
  pointName?: string
  description?: string
  orderIndex?: number
}

export interface QuestionBindRequest {
  questionIds: string[]
}

export interface SectionBindRequest {
  sectionIds: string[]
}
