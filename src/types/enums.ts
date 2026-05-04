export enum UserType {
  Student = 'Student',
  Teacher = 'Teacher',
  Admin = 'Admin'
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

export enum RegisterType {
  Mobile = '手机验证码注册',
  Email = '邮箱验证码注册',
  Wechat = '微信OpenID注册'
}

export enum CourseStatus {
  Draft = 0,
  Published = 1,
  Archived = 2
}

export enum ClassStatus {
  Enrolling = 0,
  InProgress = 1,
  Ended = 2
}

export enum ExamType {
  Exam = 0,
  Practice = 1,
  Homework = 2
}

export enum ExamStatus {
  NotStarted = 0,
  InProgress = 1,
  Ended = 2,
  Graded = 3
}

export enum QuestionType {
  SingleChoice = 0,
  MultipleChoice = 1,
  TrueFalse = 2,
  FillBlank = 3,
  ShortAnswer = 4
}

export enum ResourceType {
  Video = 1,
  Document = 2,
  Image = 3
}

export enum SectionResourceType {
  Video = 'VIDEO',
  Document = 'DOCUMENT',
  Image = 'IMAGE'
}

export enum UploadStatus {
  Pending = 0,
  Success = 1,
  Failed = 2
}

export enum GradingStatus {
  NotGraded = 0,
  Graded = 1,
  AIGrading = 2
}

export enum PaperStatus {
  Draft = 0,
  Published = 1
}

export enum SheetStatus {
  NotStarted = 0,
  InProgress = 1,
  Ended = 2,
  Graded = 3
}
