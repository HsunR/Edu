export interface QuestionBankVO {
  bankId: number
  bankName: string
  courseId: number
  teacherId: number
  description: string
  questionCount: number
  createdAt: string
  updatedAt: string
}

export interface QuestionBankCreateRequest {
  bankName: string
  courseId: number
  description?: string
}

export interface QuestionBankUpdateRequest {
  bankName?: string
  description?: string
}

export interface QuestionBankQueryRequest {
  current?: number
  pageSize?: number
  sortField?: string
  sortOrder?: string
  courseId?: number
  keyword?: string
}

export interface QuestionOptionVO {
  optionId: number
  label: string
  content: string
  isCorrect: boolean
  orderIndex: number
}

export interface QuestionOptionDTO {
  label: string
  content: string
  isCorrect?: boolean
  orderIndex?: number
}

export interface QuestionVO {
  questionId: number
  bankId: number
  questionType: 0 | 1 | 2 | 3 | 4
  stem: string
  analysis: string
  answer: string
  score: number
  difficulty: 1 | 2 | 3 | 4 | 5
  options: QuestionOptionVO[]
  createdAt: string
  updatedAt: string
}

export interface QuestionCreateRequest {
  questionType: 0 | 1 | 2 | 3 | 4
  stem: string
  analysis?: string
  answer?: string
  score: number
  difficulty?: 1 | 2 | 3 | 4 | 5
  options?: QuestionOptionDTO[]
}

export interface QuestionUpdateRequest {
  stem?: string
  analysis?: string
  answer?: string
  score?: number
  difficulty?: 1 | 2 | 3 | 4 | 5
  options?: QuestionOptionDTO[]
}

export interface QuestionQueryRequest {
  current?: number
  pageSize?: number
  sortField?: string
  sortOrder?: string
  bankId?: number
  questionType?: 0 | 1 | 2 | 3 | 4
  difficulty?: 1 | 2 | 3 | 4 | 5
  keyword?: string
}

export interface PaperVO {
  paperId: number
  paperName: string
  courseId: number
  teacherId: number
  totalScore: number
  sections: any[]
  status: 0 | 1
  questionCount: number
  createdAt: string
  updatedAt: string
}

export interface PaperDetailVO extends PaperVO {
  questions: PaperQuestionVO[]
}

export interface PaperQuestionVO {
  id: number
  paperId: number
  questionId: number
  orderIndex: number
  score: number
  sectionIndex: number
  question: QuestionVO
  questionSnapshot: any
}

export interface PaperCreateRequest {
  paperName: string
  courseId: number
  sections?: Array<{ index: number; title: string }>
}

export interface PaperUpdateRequest {
  paperName?: string
  sections?: any[]
}

export interface PaperQueryRequest {
  current?: number
  pageSize?: number
  sortField?: string
  sortOrder?: string
  courseId?: number
  status?: 0 | 1
  keyword?: string
}

export interface QuestionItem {
  questionId: number
  score: number
  sectionIndex?: number
}

export interface PaperQuestionAddRequest {
  questions: QuestionItem[]
}

export interface PaperQuestionOrderItem {
  id: number
  orderIndex: number
  sectionIndex?: number
}

export interface PaperQuestionOrderRequest {
  items: PaperQuestionOrderItem[]
}

export interface ExamVO {
  examId: number
  examName: string
  paperId: number
  paperName: string
  classId: number
  courseId: number
  teacherId: number
  examType: 0 | 1 | 2
  startTime: string
  endTime: string
  durationMinutes: number
  allowLateSubmit: boolean
  status: 0 | 1 | 2 | 3
  createdAt: string
}

export interface ExamCreateRequest {
  examName: string
  paperId: number
  classId: number
  examType: 0 | 1 | 2
  startTime: string
  endTime: string
  durationMinutes?: number
  allowLateSubmit?: boolean
}

export interface ExamUpdateRequest {
  examName?: string
  startTime?: string
  endTime?: string
  durationMinutes?: number
  allowLateSubmit?: boolean
}

export interface ExamQueryRequest {
  current?: number
  pageSize?: number
  sortField?: string
  sortOrder?: string
  classId?: number
  courseId?: number
  examType?: 0 | 1 | 2
  status?: 0 | 1 | 2 | 3
  keyword?: string
}

export interface ExamStatsVO {
  totalStudents: number
  submittedCount: number
  answeringCount: number
  gradedCount: number
  maxScore: number
  minScore: number
  avgScore: number
}

export interface AnswerSheetVO {
  sheetId: number
  examId: number
  studentId: number
  studentName: string
  status: 0 | 1 | 2 | 3
  totalScore: number
  objectiveScore: number
  subjectiveScore: number
  submitCount: number
  startAnswerTime: string
  submitTime: string
  deadline: string
}

export interface AnswerRecordVO {
  recordId: number
  questionId: number
  answerContent: string
  score: number
  isCorrect: boolean
  gradingStatus: 0 | 1 | 2
  graderId: number
  comment: string
  questionType: 0 | 1 | 2 | 3 | 4
  stem: string
  questionScore: number
  correctAnswer: string
}

export interface AnswerSheetDetailVO {
  sheetId: number
  examId: number
  examName: string
  studentId: number
  status: 0 | 1 | 2 | 3
  totalScore: number
  objectiveScore: number
  subjectiveScore: number
  submitCount: number
  startAnswerTime: string
  submitTime: string
  deadline: string
  records: AnswerRecordVO[]
}

export interface AnswerSaveRequest {
  answerContent?: string
}

export interface GradeRequest {
  score: number
  comment?: string
}
