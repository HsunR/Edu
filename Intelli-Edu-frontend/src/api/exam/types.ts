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
  bankId: string
  bankName: string
  courseId: string
  teacherId: string
  description: string
  questionCount: number
  createdAt: string
  updatedAt: string
}

export interface QuestionBankCreateRequest {
  bankName: string
  courseId: string
  description?: string
}

export interface QuestionBankUpdateRequest {
  bankName?: string
  description?: string
}

export interface QuestionBankQueryRequest extends PageRequest {
  courseId?: string
  keyword?: string
}

export interface QuestionOptionVO {
  optionId: string
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
  questionId: string
  bankId: string
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
  bankId?: string
  questionType?: QuestionType
  difficulty?: Difficulty
  keyword?: string
}

export interface PaperSection {
  index: number
  title: string
}

export interface PaperVO {
  paperId: string
  paperName: string
  courseId: string
  teacherId: string
  totalScore: number
  sections: PaperSection[]
  status: PaperStatus
  questionCount: number
  createdAt: string
  updatedAt: string
}

export interface QuestionSnapshot {
  questionId: string
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
  id: string
  paperId: string
  questionId: string
  orderIndex: number
  score: number
  sectionIndex: number
  question: QuestionVO
  questionSnapshot: QuestionSnapshot
}

export interface PaperCreateRequest {
  paperName: string
  courseId: string
  sections?: PaperSection[]
}

export interface PaperUpdateRequest {
  paperName?: string
  sections?: PaperSection[]
}

export interface PaperQueryRequest extends PageRequest {
  courseId?: string
  status?: PaperStatus
  keyword?: string
}

export interface QuestionItem {
  questionId: string
  score: number
  sectionIndex?: number
}

export interface PaperQuestionAddRequest {
  questions: QuestionItem[]
}

export interface PaperQuestionOrderItem {
  id: string
  orderIndex: number
  sectionIndex?: number
}

export interface PaperQuestionOrderRequest {
  items: PaperQuestionOrderItem[]
}

export interface ExamVO {
  examId: string
  examName: string
  paperId: string
  paperName: string
  classId: string
  courseId: string
  teacherId: string
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
  paperId: string
  classId: string
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
  classId?: string
  courseId?: string
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
  sheetId: string
  examId: string
  studentId: string
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
  recordId: string
  questionId: string
  answerContent: string
  score: number
  isCorrect: boolean
  gradingStatus: GradingStatus
  graderId: string
  comment: string
  questionType: QuestionType
  stem: string
  questionScore: number
  correctAnswer: string
}

export interface AnswerSheetDetailVO {
  sheetId: string
  examId: string
  examName: string
  studentId: string
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
