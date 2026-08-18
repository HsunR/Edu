# 前端 AI 开发指南 - 数据模型与 TypeScript 类型

## 1. 通用类型

### 1.1 统一响应

```typescript
// types/common.ts
export interface BaseResponse<T> {
  code: number
  data: T
  message: string
  requestId: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface PageRequest {
  current: number
  pageSize: number
}
```

### 1.2 枚举值定义

```typescript
// types/enums.ts

/** 用户类型 */
export enum UserType {
  STUDENT = 1,
  TEACHER = 2,
  ADMIN = 3
}

export const UserTypeMap: Record<number, string> = {
  [UserType.STUDENT]: '学生',
  [UserType.TEACHER]: '教师',
  [UserType.ADMIN]: '管理员'
}

/** 用户状态 */
export enum UserStatus {
  BAN = 0,
  NORMAL = 1
}

export const UserStatusMap: Record<number, string> = {
  [UserStatus.BAN]: '禁用',
  [UserStatus.NORMAL]: '正常'
}

/** 课程状态 */
export enum CourseStatus {
  DRAFT = 0,
  PUBLISHED = 1,
  ARCHIVED = 2
}

export const CourseStatusMap: Record<number, string> = {
  [CourseStatus.DRAFT]: '草稿',
  [CourseStatus.PUBLISHED]: '已发布',
  [CourseStatus.ARCHIVED]: '已归档'
}

export const CourseStatusTagType: Record<number, '' | 'success' | 'info' | 'warning' | 'danger'> = {
  [CourseStatus.DRAFT]: 'info',
  [CourseStatus.PUBLISHED]: 'success',
  [CourseStatus.ARCHIVED]: 'warning'
}

/** 题型 */
export enum QuestionType {
  SINGLE_CHOICE = 0,
  MULTI_CHOICE = 1,
  TRUE_FALSE = 2,
  FILL_BLANK = 3,
  SHORT_ANSWER = 4
}

export const QuestionTypeMap: Record<number, string> = {
  [QuestionType.SINGLE_CHOICE]: '单选题',
  [QuestionType.MULTI_CHOICE]: '多选题',
  [QuestionType.TRUE_FALSE]: '判断题',
  [QuestionType.FILL_BLANK]: '填空题',
  [QuestionType.SHORT_ANSWER]: '简答题'
}

/** 考试状态 */
export enum ExamStatus {
  DRAFT = 0,
  PUBLISHED = 1,
  IN_PROGRESS = 2,
  ENDED = 3
}

/** 答卷状态 */
export enum SheetStatus {
  NOT_STARTED = 0,
  IN_PROGRESS = 1,
  SUBMITTED = 2,
  GRADING = 3,
  COMPLETED = 4
}

/** 资源类型 */
export enum ResourceType {
  IMAGE = 0,
  DOCUMENT = 1,
  VIDEO = 2,
  AUDIO = 3
}

export const ResourceTypeMap: Record<number, string> = {
  [ResourceType.IMAGE]: '图片',
  [ResourceType.DOCUMENT]: '文档',
  [ResourceType.VIDEO]: '视频',
  [ResourceType.AUDIO]: '音频'
}

/** 性别 */
export enum SexType {
  UNKNOWN = 0,
  MALE = 1,
  FEMALE = 2
}

export const SexTypeMap: Record<number, string> = {
  [SexType.UNKNOWN]: '未知',
  [SexType.MALE]: '男',
  [SexType.FEMALE]: '女'
}
```

## 2. 用户模块类型

```typescript
// types/user.ts

/** 用户基础信息 */
export interface User {
  userId: number
  name: string
  userType: UserType
  avatarUrl?: string
  email?: string
  mobile?: string
  sex?: SexType
  school?: string
  personalSignature?: string
  status: UserStatus
  createdAt: string
  updatedAt: string
}

/** 用户简要信息（列表展示） */
export interface UserVO {
  userId: number
  name: string
  avatarUrl?: string
  userType: UserType
  school?: string
}

/** 用户详细信息 */
export interface UserDetailVO extends User {
  // 根据角色包含不同档案
  teacherProfile?: TeacherProfile
  studentProfile?: StudentProfile
}

/** 教师档案 */
export interface TeacherProfile {
  profileId: number
  userId: number
  title?: string        // 职称
  department?: string   // 所属院系
  major?: string        // 专业领域
  teachingYears?: number // 教龄
}

/** 学生档案 */
export interface StudentProfile {
  profileId: number
  userId: number
  grade?: string        // 年级
  major?: string        // 专业
  studentNo?: string    // 学号
}

// ========== 请求 DTO ==========

/** 登录请求 */
export interface LoginRequest {
  loginType: LoginType
  // 用户名密码登录
  username?: string
  password?: string
  // 手机验证码登录
  mobile?: string
  code?: string
  // 邮箱验证码登录
  email?: string
}

export enum LoginType {
  USERNAME_PASSWORD = 0,
  MOBILE_CODE = 1,
  EMAIL_CODE = 2
}

/** 注册请求 */
export interface RegisterRequest {
  registerType: RegisterType
  name: string
  password: string
  // 手机注册
  mobile?: string
  code?: string
  // 邮箱注册
  email?: string
}

export enum RegisterType {
  MOBILE_CODE = 0,
  EMAIL_CODE = 1
}

/** 登录响应 */
export interface LoginResult {
  accessToken: string
  refreshToken: string
  expiresIn: number      // 过期时间（秒）
  userId: number
  userType: UserType
  name: string
}

/** 用户信息更新 */
export interface UserUpdateRequest {
  name?: string
  sex?: SexType
  school?: string
  personalSignature?: string
}

/** 密码修改 */
export interface PasswordUpdateRequest {
  oldPassword: string
  newPassword: string
}

/** 用户查询 */
export interface UserQueryRequest extends PageRequest {
  keyword?: string
  userType?: UserType
  status?: UserStatus
}

/** 发送验证码 */
export interface SendCodeRequest {
  type: 'register' | 'login'
  target: string  // 手机号或邮箱
}
```

## 3. 课程模块类型

```typescript
// types/course.ts

/** 课程 */
export interface Course {
  courseId: number
  courseName: string
  description?: string
  coverUrl?: string
  teacherId: number
  teacherName?: string    // 关联查询返回
  categoryId?: number
  status: CourseStatus
  isPublic: number        // 0=私有, 1=公开
  createdAt: string
  updatedAt: string
}

/** 课程列表 VO */
export interface CourseVO {
  courseId: number
  courseName: string
  description?: string
  coverUrl?: string
  teacherName?: string
  categoryName?: string
  status: CourseStatus
  studentCount?: number   // 学生人数
}

/** 课程详情（含章节目录） */
export interface CourseDetailVO extends CourseVO {
  chapters: ChapterVO[]
  teacherInfo?: UserVO
}

/** 章节 */
export interface Chapter {
  chapterId: number
  courseId: number
  chapterName: string
  orderIndex: number
}

export interface ChapterVO extends Chapter {
  sections: SectionVO[]
}

/** 小节 */
export interface Section {
  sectionId: number
  chapterId: number
  sectionName: string
  contentType?: number    // 内容类型：视频/文档/图文
  orderIndex: number
}

export interface SectionVO extends Section {
  resources?: SectionResourceVO[]
}

/** 小节资源关联 */
export interface SectionResourceVO {
  id: number
  sectionId: number
  resourceId: number
  resourceType: ResourceType
  resourceName?: string
  url?: string
  orderIndex: number
}

/** 分类 */
export interface Category {
  categoryId: number
  parentId?: number
  categoryName: string
  children?: Category[]   // 树形结构
}

/** 班级 */
export interface Clazz {
  classId: number
  courseId: number
  className: string
  teacherId: number
  maxStudents?: number
  inviteCode?: string
  status: number
  createdAt: string
}

export interface ClassVO extends Clazz {
  courseName?: string
  memberCount?: number
}

/** 班级成员 */
export interface ClassMember {
  id: number
  classId: number
  studentId: number
  studentName?: string
  joinTime: string
}

// ========== 请求 DTO ==========

export interface CourseCreateRequest {
  courseName: string
  description?: string
  coverUrl?: string
  categoryId?: number
  isPublic?: number
}

export interface CourseUpdateRequest {
  courseName?: string
  description?: string
  coverUrl?: string
  categoryId?: number
  isPublic?: number
}

export interface CourseQueryRequest extends PageRequest {
  keyword?: string
  categoryId?: number
  status?: CourseStatus
  teacherId?: number
}

export interface ChapterCreateRequest {
  chapterName: string
}

export interface SectionCreateRequest {
  sectionName: string
  contentType?: number
}

export interface JoinClassRequest {
  inviteCode: string
}
```

## 4. 考试模块类型

```typescript
// types/exam.ts

/** 题库 */
export interface QuestionBank {
  bankId: number
  bankName: string
  courseId?: number
  courseName?: string
  teacherId: number
  teacherName?: string
  questionCount?: number
  createdAt: string
}

/** 题目 */
export interface Question {
  questionId: number
  bankId: number
  questionType: QuestionType
  content: string
  answer?: string
  analysis?: string
  difficulty: number      // 1-5
  score?: number
  teacherId: number
  options?: QuestionOption[]
  createdAt: string
}

/** 题目选项 */
export interface QuestionOption {
  optionId: number
  questionId: number
  optionLabel: string     // A/B/C/D
  optionContent: string
  isCorrect: boolean
}

/** 试卷 */
export interface Paper {
  paperId: number
  paperName: string
  totalScore: number
  durationMinutes?: number
  teacherId: number
  status: number          // 0=草稿, 1=已发布
  questionCount?: number
  createdAt: string
}

export interface PaperVO extends Paper {
  questions?: PaperQuestionVO[]
}

/** 试卷题目关联 */
export interface PaperQuestionVO {
  id: number
  paperId: number
  questionId: number
  questionOrder: number
  score: number
  question?: Question     // 嵌套题目详情
}

/** 考试 */
export interface Exam {
  examId: number
  examName: string
  paperId: number
  paperName?: string
  classId?: number
  className?: string
  courseId?: number
  courseName?: string
  teacherId: number
  examType?: number
  startTime?: string
  endTime?: string
  durationMinutes?: number
  allowLateSubmit: boolean
  status: ExamStatus
  createdAt: string
}

export interface ExamVO extends Exam {
  stats?: ExamStatsVO
}

/** 考试统计 */
export interface ExamStatsVO {
  totalStudents: number
  submittedCount: number
  averageScore?: number
  maxScore?: number
  minScore?: number
  scoreDistribution?: Record<string, number>  // 分数段分布
}

/** 答题卡 */
export interface AnswerSheet {
  sheetId: number
  examId: number
  studentId: number
  totalScore?: number
  status: SheetStatus
  submitTime?: string
  createdAt: string
}

export interface AnswerSheetVO extends AnswerSheet {
  studentName?: string
  examName?: string
}

/** 答题记录 */
export interface AnswerRecord {
  recordId: number
  sheetId: number
  questionId: number
  studentAnswer?: string
  score?: number
  isCorrect?: boolean
  questionSnapshot?: Question  // 题目快照
}

/** 答卷详情 */
export interface AnswerSheetDetailVO {
  sheet: AnswerSheetVO
  records: AnswerRecord[]
  exam: ExamVO
}

// ========== 请求 DTO ==========

export interface QuestionCreateRequest {
  questionType: QuestionType
  content: string
  answer: string
  analysis?: string
  difficulty: number
  score: number
  options?: OptionCreateRequest[]
}

export interface OptionCreateRequest {
  optionLabel: string
  optionContent: string
  isCorrect: boolean
}

export interface PaperCreateRequest {
  paperName: string
  totalScore: number
  durationMinutes?: number
}

export interface ExamCreateRequest {
  examName: string
  paperId: number
  classId?: number
  startTime?: string
  endTime?: string
  durationMinutes?: number
  allowLateSubmit?: boolean
}

export interface AnswerSaveRequest {
  answerContent: string
}

export interface GradeRequest {
  score: number
  comment?: string
}

export interface ExamQueryRequest extends PageRequest {
  classId?: number
  courseId?: number
  status?: ExamStatus
  keyword?: string
}
```

## 5. 知识模块类型

```typescript
// types/knowledge.ts

/** 知识点 */
export interface KnowledgePoint {
  pointId: number
  pointName: string
  courseId: number
  parentId?: number
  description?: string
  orderIndex: number
  createdAt: string
}

export interface KnowledgePointVO extends KnowledgePoint {
  children?: KnowledgePointVO[]
}

/** 知识树 */
export interface KnowledgeTreeVO {
  pointId: number
  pointName: string
  description?: string
  children: KnowledgeTreeVO[]
}

// ========== 请求 DTO ==========

export interface PointCreateRequest {
  pointName: string
  courseId: number
  parentId?: number
  description?: string
}

export interface PointUpdateRequest {
  pointName?: string
  description?: string
}

export interface SectionBindRequest {
  sectionIds: number[]
}

export interface QuestionBindRequest {
  questionIds: number[]
}
```

## 6. 资源模块类型

```typescript
// types/resource.ts

/** 资源 */
export interface RsResource {
  resourceId: number
  resourceName: string
  resourceType: ResourceType
  url: string
  size?: number           // 字节
  mimeType?: string
  uploaderId: number
  courseId?: number
  status: number
  createdAt: string
}

export interface ResourceVO {
  resourceId: number
  resourceName: string
  resourceType: ResourceType
  url: string
  size?: number
  createdAt: string
}

export interface ResourceDetailVO extends ResourceVO {
  mimeType?: string
  uploaderName?: string
  courseName?: string
  videoMeta?: VideoMetaVO
}

/** 视频元数据 */
export interface VideoMetaVO {
  metaId: number
  resourceId: number
  duration?: number       // 秒
  width?: number
  height?: number
  bitrate?: number
  vodFileId?: string
}

/** 预签名 URL 响应 */
export interface PresignedUrlVO {
  presignedUrl: string    // 直接上传到这个 URL
  resourceId: number
  expiresAt?: string      // URL 过期时间
}

export interface VodPresignedUrlVO extends PresignedUrlVO {
  vodFileId?: string
}

// ========== 请求 DTO ==========

export interface PresignRequest {
  fileName: string
  fileSize?: number
  mimeType?: string
  courseId?: number
}

export interface UploadConfirmRequest {
  resourceId: number
  url: string
  fileName?: string
  fileSize?: number
}

export interface VideoConfirmRequest {
  resourceId: number
  vodFileId: string
}

export interface ResourceQueryRequest extends PageRequest {
  resourceType?: ResourceType
  keyword?: string
}
```

## 7. AI 模块类型

```typescript
// types/ai.ts

/** 聊天会话 */
export interface ChatList {
  chatId: number
  userId: number
  conversationId: string
  title: string
  createdAt: string
}

/** 聊天消息 */
export interface ChatMessage {
  messageId: number
  chatId: number
  role: 'user' | 'assistant' | 'system'
  content: string
  createdAt: string
}

/** 聊天会话 VO */
export interface ChatListVO {
  chatId: number
  userId: number
  conversationId: string
  title: string
  createdAt: string
  messages?: ChatMessage[]
}

// ========== 请求 DTO ==========

export interface ChatRequest {
  conversationId: string   // 会话唯一标识
  userPrompt: string       // 用户输入
}

export interface ChatListQueryRequest {
  userId?: number
  conversationId?: string
}

export interface ChatListAddRequest {
  title?: string
  userId?: number
}

export interface ChatListUpdateRequest {
  conversationId: string
  title?: string
}

export interface ChatListDeleteRequest {
  chatId: number
}
```

## 8. 类型使用示例

```typescript
// 在 Vue 组件中使用
<script setup lang="ts">
import type { CourseVO, CourseQueryRequest } from '@/types/course'
import type { PageResult } from '@/types/common'

const courseList = ref<CourseVO[]>([])
const queryParams = reactive<CourseQueryRequest>({
  current: 1,
  pageSize: 10,
  keyword: ''
})

const fetchCourses = async () => {
  const res: PageResult<CourseVO> = await courseApi.listPublicCourses(queryParams)
  courseList.value = res.records
}
</script>
```
