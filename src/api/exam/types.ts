import type {
  Difficulty,
  ExamStatus,
  ExamType,
  GradingStatus,
  PaperStatus,
  QuestionType,
  SheetStatus
} from '@/types/enums'
import type { PageRequest } from '@/types/api'

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

export interface QuestionBankQueryRequest extends PageRequest {
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
  questionType: QuestionType
  stem: string
  analysis: string
  answer: string
  score: number
  difficulty: Difficulty
  options: QuestionOptionVO[]
  createdAt: string
  updatedAt: string
}

export interface QuestionCreateRequest {
  questionType: QuestionType
  stem: string
  analysis?: string
  answer?: string
  score: number
  difficulty?: Difficulty
  options?: QuestionOptionDTO[]
}

export interface QuestionUpdateRequest {
  stem?: string
  analysis?: string
  answer?: string
  score?: number
  difficulty?: Difficulty
  options?: QuestionOptionDTO[]
}

export interface QuestionQueryRequest extends PageRequest {
  bankId?: number
  questionType?: QuestionType
  difficulty?: Difficulty
  keyword?: string
}

export interface PaperSection {
  index: number
  title: string
}

export interface PaperVO {
  paperId: number
  paperName: string
  courseId: number
  teacherId: number
  totalScore: number
  sections: PaperSection[]
  status: PaperStatus
  questionCount: number
  createdAt: string
  updatedAt: string
}

export interface QuestionSnapshot {
  questionId: number
  questionType: QuestionType
  stem: string
  answer: string
  score: number
  difficulty: Difficulty
  options: QuestionOptionVO[]
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
  questionSnapshot: QuestionSnapshot
}

export interface PaperCreateRequest {
  paperName: string
  courseId: number
  sections?: PaperSection[]
}

export interface PaperUpdateRequest {
  paperName?: string
  sections?: PaperSection[]
}

export interface PaperQueryRequest extends PageRequest {
  courseId?: number
  status?: PaperStatus
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
  examType: ExamType
  startTime: string
  endTime: string
  durationMinutes: number
  allowLateSubmit: boolean
  status: ExamStatus
  createdAt: string
}

export interface ExamCreateRequest {
  examName: string
  paperId: number
  classId: number
  examType: ExamType
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

export interface ExamQueryRequest extends PageRequest {
  classId?: number
  courseId?: number
  examType?: ExamType
  status?: ExamStatus
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
  status: SheetStatus
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
  gradingStatus: GradingStatus
  graderId: number
  comment: string
  questionType: QuestionType
  stem: string
  questionScore: number
  correctAnswer: string
}

export interface AnswerSheetDetailVO {
  sheetId: number
  examId: number
  examName: string
  studentId: number
  status: SheetStatus
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
