export enum LoginType {
  Mobile = '1',
  Email = '2',
  WeChat = '3',
  Username = '4'
}

export const LoginTypeLabels: Record<LoginType, string> = {
  [LoginType.Mobile]: '手机号',
  [LoginType.Email]: '邮箱',
  [LoginType.WeChat]: '微信',
  [LoginType.Username]: '用户名'
}

export enum RegisterType {
  Mobile = '手机验证码注册',
  Email = '邮箱验证码注册',
  Wechat = '微信OpenID注册'
}

export const RegisterTypeLabels: Record<RegisterType, string> = {
  [RegisterType.Mobile]: '手机注册',
  [RegisterType.Email]: '邮箱注册',
  [RegisterType.Wechat]: '微信注册'
}

export enum UserType {
  Student = 'Student',
  Teacher = 'Teacher',
  Admin = 'Admin'
}

export const UserTypeLabels: Record<UserType, string> = {
  [UserType.Student]: '学生',
  [UserType.Teacher]: '教师',
  [UserType.Admin]: '管理员'
}

export enum Sex {
  Unknown = '未知',
  Male = '男',
  Female = '女'
}

export enum UserStatus {
  Normal = '正常',
  Banned = '禁止'
}

export enum CourseStatus {
  Draft = 0,
  Published = 1,
  Archived = 2
}

export const CourseStatusLabels: Record<CourseStatus, string> = {
  [CourseStatus.Draft]: '草稿',
  [CourseStatus.Published]: '已发布',
  [CourseStatus.Archived]: '已归档'
}

export enum YesNo {
  No = 0,
  Yes = 1
}

export enum ClassStatus {
  Enrolling = 0,
  InProgress = 1,
  Ended = 2
}

export const ClassStatusLabels: Record<ClassStatus, string> = {
  [ClassStatus.Enrolling]: '招募中',
  [ClassStatus.InProgress]: '进行中',
  [ClassStatus.Ended]: '已结束'
}

export enum QuestionType {
  SingleChoice = 0,
  MultipleChoice = 1,
  TrueFalse = 2,
  FillBlank = 3,
  ShortAnswer = 4
}

export const QuestionTypeLabels: Record<QuestionType, string> = {
  [QuestionType.SingleChoice]: '单选题',
  [QuestionType.MultipleChoice]: '多选题',
  [QuestionType.TrueFalse]: '判断题',
  [QuestionType.FillBlank]: '填空题',
  [QuestionType.ShortAnswer]: '简答题'
}

export enum Difficulty {
  VeryEasy = 1,
  Easy = 2,
  Medium = 3,
  Hard = 4,
  VeryHard = 5
}

export const DifficultyLabels: Record<Difficulty, string> = {
  [Difficulty.VeryEasy]: '非常简单',
  [Difficulty.Easy]: '简单',
  [Difficulty.Medium]: '中等',
  [Difficulty.Hard]: '困难',
  [Difficulty.VeryHard]: '非常困难'
}

export enum ExamType {
  Exam = 0,
  Practice = 1,
  Homework = 2
}

export const ExamTypeLabels: Record<ExamType, string> = {
  [ExamType.Exam]: '考试',
  [ExamType.Practice]: '练习',
  [ExamType.Homework]: '作业'
}

export enum ExamStatus {
  NotStarted = 0,
  InProgress = 1,
  Ended = 2,
  Graded = 3
}

export const ExamStatusLabels: Record<ExamStatus, string> = {
  [ExamStatus.NotStarted]: '未开始',
  [ExamStatus.InProgress]: '进行中',
  [ExamStatus.Ended]: '已结束',
  [ExamStatus.Graded]: '已批改'
}

export enum SheetStatus {
  Answering = 0,
  Submitted = 1,
  Graded = 2
}

export const SheetStatusLabels: Record<SheetStatus, string> = {
  [SheetStatus.Answering]: '答题中',
  [SheetStatus.Submitted]: '已提交',
  [SheetStatus.Graded]: '已批阅'
}

export enum GradingStatus {
  NotGraded = 0,
  Graded = 1,
  AIGrading = 2
}

export const GradingStatusLabels: Record<GradingStatus, string> = {
  [GradingStatus.NotGraded]: '未批改',
  [GradingStatus.Graded]: '已批改',
  [GradingStatus.AIGrading]: 'AI批改中'
}

export enum PaperStatus {
  Draft = 0,
  Published = 1
}

export const PaperStatusLabels: Record<PaperStatus, string> = {
  [PaperStatus.Draft]: '草稿',
  [PaperStatus.Published]: '已发布'
}

export enum ResourceType {
  Video = 1,
  Document = 2,
  Image = 3
}

export const ResourceTypeLabels: Record<ResourceType, string> = {
  [ResourceType.Video]: '视频',
  [ResourceType.Document]: '文档',
  [ResourceType.Image]: '图片'
}

export enum UploadStatus {
  Pending = 1,
  Success = 2,
  Failed = 3
}

export const UploadStatusLabels: Record<UploadStatus, string> = {
  [UploadStatus.Pending]: '待确认',
  [UploadStatus.Success]: '上传成功',
  [UploadStatus.Failed]: '上传失败'
}

export enum SectionResourceType {
  Video = 'VIDEO',
  Document = 'DOCUMENT',
  Image = 'IMAGE'
}

export const SectionResourceTypeLabels: Record<SectionResourceType, string> = {
  [SectionResourceType.Video]: '视频',
  [SectionResourceType.Document]: '文档',
  [SectionResourceType.Image]: '图片'
}
