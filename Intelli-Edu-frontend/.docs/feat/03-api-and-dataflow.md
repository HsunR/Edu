# 智慧教育平台 - API 接口与数据流文档

## 1. HTTP 请求封装

### 1.1 Axios 实例配置

| 配置项 | 值 |
|--------|-----|
| baseURL | `import.meta.env.VITE_APP_BASE_API` |
| timeout | 30000ms |
| 响应转换 | json-bigint（大整数转字符串，防止精度丢失） |

### 1.2 请求拦截器

```
请求发出
  ├── 有 Token 且非 noToken 标记？
  │   └── 是 → 添加 Authorization: Bearer {token}
  ├── 请求体是 FormData？
  │   └── 是 → Content-Type: multipart/form-data
  └── 其他 → Content-Type: application/json
```

### 1.3 响应拦截器

#### 成功响应处理

后端统一响应格式：`{ code: number, data: T, message: string }`

- `code === 0` → 返回 `data` 字段
- `code !== 0` → 根据错误码显示错误提示，Promise.reject

#### 错误码处理

| 错误码 | 枚举值 | 处理方式 |
|--------|--------|----------|
| 0 | SUCCESS | 正常返回 data |
| 40000 | PARAMS_ERROR | ElMessage.error 提示 |
| 40100 | NOT_LOGIN_ERROR | 弹窗提示重新登录 |
| 40101 | NO_AUTH_ERROR | 提示无权限 |
| 40300 | FORBIDDEN_ERROR | 提示禁止访问 |
| 40400 | NOT_FOUND_ERROR | 提示数据不存在 |
| 50000 | SYSTEM_ERROR | 提示系统异常 |
| 50001 | OPERATION_ERROR | 提示操作失败 |

#### 401 Token 刷新机制

```
收到 401 响应
  ├── 正在刷新中？
  │   └── 是 → 将请求加入等待队列，新 Token 后重试
  └── 否 → 使用 RefreshToken 调用 /api/user/auth/refresh-token
      ├── 刷新成功 → 更新 Token，重试队列中的请求
      └── 刷新失败 → 弹窗提示重新登录
```

### 1.4 封装的请求方法

```typescript
get<T>(url, params?, config?)    // GET 请求
post<T>(url, data?, config?)     // POST 请求
put<T>(url, data?, config?)      // PUT 请求
del<T>(url, config?)             // DELETE 请求
download(url, params, filename)  // 文件下载（Blob + file-saver）
```

---

## 2. API 模块划分

### 2.1 用户认证模块 (`api/user/`)

#### 认证接口 (`auth.ts`)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| login | POST | `/api/user/auth/login` | 登录 |
| sendLoginCode | POST | `/api/user/auth/login/send-code` | 发送登录验证码 |
| logout | POST | `/api/user/auth/logout` | 登出 |
| refreshTokenApi | POST | `/api/user/auth/refresh-token` | 刷新 Token |
| register | POST | `/api/user/auth/register` | 注册 |
| sendRegisterCode | POST | `/api/user/auth/register/send-code` | 发送注册验证码 |

#### 用户接口 (`user.ts`)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| getUserInfo | GET | `/api/user/users/info` | 获取当前用户信息 |
| updateUserInfo | PUT | `/api/user/users/info` | 更新用户信息 |
| updateAvatar | PUT | `/api/user/users/avatar` | 更新头像 |
| getUserList | GET | `/api/user/users` | 获取用户列表（管理员） |

#### 核心类型

```typescript
LoginRequest {
  loginType: LoginType      // 登录方式
  username?: string         // 用户名
  password?: string         // 密码
  mobile?: string           // 手机号
  email?: string            // 邮箱
  code?: string             // 验证码
  openId?: string           // 微信 OpenID
}

LoginResult {
  userId: number
  userType: UserType        // Student | Teacher | Admin
  accessToken: string
  refreshToken: string
}

UserDetailVO {
  userId, name, type, sex, avatarUrl, personalSignature, school
  email, mobile, status
  studentProfile?: StudentProfileVO   // 学生档案
  teacherProfile?: TeacherProfileVO   // 教师档案
}
```

---

### 2.2 课程模块 (`api/course/`)

#### 课程接口 (`course.ts`)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| getCourseList | GET | `/api/course/courses` | 课程列表（分页） |
| getCourseDetail | GET | `/api/course/courses/:id` | 课程详情（含章节） |
| createCourse | POST | `/api/course/courses` | 创建课程 |
| updateCourse | PUT | `/api/course/courses/:id` | 更新课程 |
| deleteCourse | DELETE | `/api/course/courses/:id` | 删除课程 |
| publishCourse | PUT | `/api/course/courses/:id/publish` | 发布课程 |
| archiveCourse | PUT | `/api/course/courses/:id/archive` | 归档课程 |
| getTeachingCourses | GET | `/api/course/courses/teaching` | 我教的课 |
| getCourseClasses | GET | `/api/course/courses/:id/classes` | 课程下的班级 |
| getCourseBrief | GET | `/api/course/courses/:id/brief` | 课程简要信息 |
| getBatchCourseBrief | POST | `/api/course/courses/batch` | 批量获取课程信息 |
| checkMemberInClass | GET | `/api/course/classes/:id/check-member` | 检查是否为班级成员 |

#### 章节接口 (`chapter.ts`)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| getCategoryTree | GET | `/api/course/categories/` | 获取分类树 |
| addChapter | POST | `/api/course/courses/:id/chapters` | 添加章节 |
| updateChapter | PUT | `/api/course/chapters/:id` | 更新章节 |
| deleteChapter | DELETE | `/api/course/chapters/:id` | 删除章节 |
| reorderChapters | PUT | `/api/course/courses/:id/chapters/order` | 章节排序 |

#### 小节接口 (`section.ts`)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| addSection | POST | `/api/course/chapters/:id/sections` | 添加小节 |
| updateSection | PUT | `/api/course/sections/:id` | 更新小节 |
| deleteSection | DELETE | `/api/course/sections/:id` | 删除小节 |
| getSectionDetail | GET | `/api/course/sections/:id/detail` | 小节详情（含资源） |
| reorderSections | PUT | `/api/course/chapters/:id/sections/order` | 小节排序 |
| addSectionResource | POST | `/api/course/sections/:id/resources` | 添加小节资源 |
| removeSectionResource | DELETE | `/api/course/sections/:id/resources/:rid` | 移除小节资源 |
| reorderSectionResources | PUT | `/api/course/sections/:id/resources/order` | 小节资源排序 |

#### 班级接口 (`class.ts`)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| createClass | POST | `/api/course/classes` | 创建班级 |
| updateClass | PUT | `/api/course/classes/:id` | 更新班级 |
| getClassMembers | GET | `/api/course/classes/:id/members` | 班级成员列表 |
| removeMember | DELETE | `/api/course/classes/:id/members/:mid` | 移除成员 |
| quitClass | POST | `/api/course/classes/:id/quit` | 退出班级 |
| joinClass | POST | `/api/course/classes/join` | 加入班级（邀请码） |
| getMyClasses | GET | `/api/course/classes/my` | 我的班级列表 |

#### 核心类型

```typescript
CourseVO {
  courseId, courseName, coverUrl, description
  teacherId, teacherName, teacherAvatar
  categoryId, categoryName, status, isPublic, createdAt
}

CourseDetailVO extends CourseVO {
  chapters: ChapterVO[]
}

ChapterVO {
  chapterId, courseId, title, orderIndex
  sections: SectionVO[]
}

SectionVO {
  sectionId, chapterId, title, orderIndex, isFree
  resources: SectionResourceVO[]
}

SectionResourceVO {
  id, sectionId, resourceId, resourceType, orderIndex
  resourceName, accessUrl
}

ClassVO {
  classId, courseId, courseName, className
  teacherId, teacherName, inviteCode
  maxStudents, currentStudents
  startDate, endDate, status, createdAt
}
```

---

### 2.3 考试模块 (`api/exam/`)

#### 题库管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| getQuestionBankList | GET | `/api/exam/question-banks` | 题库列表 |
| createQuestionBank | POST | `/api/exam/question-banks` | 创建题库 |
| updateQuestionBank | PUT | `/api/exam/question-banks/:id` | 更新题库 |
| deleteQuestionBank | DELETE | `/api/exam/question-banks/:id` | 删除题库 |

#### 题目管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| getQuestionList | GET | `/api/exam/questions` | 题目列表 |
| getQuestionDetail | GET | `/api/exam/questions/:id` | 题目详情 |
| createQuestion | POST | `/api/exam/questions/banks/:bankId` | 创建题目 |
| updateQuestion | PUT | `/api/exam/questions/:id` | 更新题目 |
| deleteQuestion | DELETE | `/api/exam/questions/:id` | 删除题目 |

#### 试卷管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| getPaperList | GET | `/api/exam/papers` | 试卷列表 |
| getPaperDetail | GET | `/api/exam/papers/:id` | 试卷详情（含题目） |
| createPaper | POST | `/api/exam/papers` | 创建试卷 |
| updatePaper | PUT | `/api/exam/papers/:id` | 更新试卷 |
| deletePaper | DELETE | `/api/exam/papers/:id` | 删除试卷 |
| publishPaper | PUT | `/api/exam/papers/:id/publish` | 发布试卷 |
| addPaperQuestions | POST | `/api/exam/papers/:id/questions` | 添加试卷题目 |
| removePaperQuestion | DELETE | `/api/exam/papers/:id/questions/:qid` | 移除试卷题目 |
| reorderPaperQuestions | PUT | `/api/exam/papers/:id/questions/order` | 试卷题目排序 |

#### 考试管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| getExamList | GET | `/api/exam/exams` | 考试列表 |
| createExam | POST | `/api/exam/exams` | 创建考试 |
| updateExam | PUT | `/api/exam/exams/:id` | 更新考试 |
| deleteExam | DELETE | `/api/exam/exams/:id` | 删除考试 |
| getExamSheets | GET | `/api/exam/exams/:id/sheets` | 考试答题卡列表 |
| getExamStats | GET | `/api/exam/exams/:id/stats` | 考试统计 |

#### 答题与批改

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| enterExam | POST | `/api/exam/answers/exams/:id/enter` | 进入考试 |
| getMySheet | GET | `/api/exam/answers/exams/:id/my-sheet` | 获取我的答题卡 |
| saveAnswer | PUT | `/api/exam/answers/sheets/:sid/questions/:qid` | 保存答案 |
| submitExam | POST | `/api/exam/answers/sheets/:sid/submit` | 提交考试 |
| getSheetDetail | GET | `/api/exam/exams/sheets/:sid/detail` | 答题卡详情 |
| gradeRecord | PUT | `/api/exam/exams/records/:rid/grade` | 批改单题 |
| finishGrading | POST | `/api/exam/exams/sheets/:sid/finish-grading` | 完成批改 |

#### 核心类型

```typescript
QuestionVO {
  questionId, bankId, questionType, stem, analysis
  answer, score, difficulty, options, createdAt, updatedAt
}

// 题目类型: SingleChoice(0) | MultipleChoice(1) | TrueFalse(2) | FillBlank(3) | ShortAnswer(4)
// 难度: VeryEasy(1) | Easy(2) | Medium(3) | Hard(4) | VeryHard(5)

PaperVO {
  paperId, paperName, courseId, teacherId
  totalScore, sections, status, questionCount
}

ExamVO {
  examId, examName, paperId, paperName, classId, courseId, teacherId
  examType, startTime, endTime, durationMinutes
  allowLateSubmit, status
}

// 考试类型: Exam(0) | Practice(1) | Homework(2)
// 考试状态: NotStarted(0) | InProgress(1) | Ended(2) | Graded(3)

AnswerSheetDetailVO {
  sheetId, examId, examName, studentId, status
  totalScore, objectiveScore, subjectiveScore
  submitCount, startAnswerTime, submitTime, deadline
  records: AnswerRecordVO[]
}

ExamStatsVO {
  totalStudents, submittedCount, answeringCount, gradedCount
  maxScore, minScore, avgScore
}
```

---

### 2.4 AI 模块 (`api/ai/`)

#### AI 课程对话 (`course.ts`)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| doChatByStream | POST | `/api/ai/aiCourse/doChatByStream` | 流式对话 |
| doChatByStreamMono | POST | `/api/ai/aiCourse/doChatByStreamMono` | Mono 流式对话 |
| doChatWithRagByStream | POST | `/api/ai/aiCourse/doChatWithRagByStream` | RAG 增强流式对话 |
| doChatWithToolAndRag | POST | `/api/ai/aiCourse/doChatWithToolAndRagByStream` | 工具+RAG 流式对话 |
| uploadChat | POST | `/api/ai/aiCourse/uploadChat` | 上传文件对话 |

#### 聊天记录管理 (`chat.ts`)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| createChatList | POST | `/api/ai/chatList/createChatList` | 创建对话 |
| getChatListByConversationId | GET | `/api/ai/chatList/getChatListByConversationId` | 按 ID 获取对话 |
| getUserChatList | GET | `/api/ai/chatList/getUserChatList` | 用户对话列表 |
| updateChatList | POST | `/api/ai/chatList/updateChatList` | 更新对话 |

#### 文件上传 (`upload.ts`)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| uploadFile | POST | `/api/ai/upload` | 上传文件到 AI 模块 |

---

### 2.5 知识点模块 (`api/knowledge/`)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| createPoint | POST | `/api/knowledge/points` | 创建知识点 |
| updatePoint | PUT | `/api/knowledge/points/:id` | 更新知识点 |
| deletePoint | DELETE | `/api/knowledge/points/:id` | 删除知识点 |
| getKnowledgeTree | GET | `/api/knowledge/points/tree` | 获取知识点树 |
| getPointQuestions | GET | `/api/knowledge/points/:id/questions` | 知识点关联题目 |
| bindQuestions | POST | `/api/knowledge/points/:id/questions` | 绑定题目 |
| unbindQuestion | DELETE | `/api/knowledge/points/:id/questions/:qid` | 解绑题目 |
| getPointSections | GET | `/api/knowledge/points/:id/sections` | 知识点关联小节 |
| bindSections | POST | `/api/knowledge/points/:id/sections` | 绑定小节 |
| unbindSection | DELETE | `/api/knowledge/points/:id/sections/:sid` | 解绑小节 |
| getQuestionPoints | GET | `/api/knowledge/questions/:id/points` | 题目关联的知识点 |
| getSectionPoints | GET | `/api/knowledge/sections/:id/points` | 小节关联的知识点 |

---

### 2.6 资源模块 (`api/resource/`)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| getResourceList | GET | `/api/resource/resources` | 资源列表 |
| getResourceDetail | GET | `/api/resource/resources/:id` | 资源详情 |
| deleteResource | DELETE | `/api/resource/resources/:id` | 删除资源 |
| confirmUpload | POST | `/api/resource/resources/confirm` | 确认文档/图片上传 |
| confirmVideoUpload | POST | `/api/resource/resources/confirm/video` | 确认视频上传 |
| presignDocument | POST | `/api/resource/resources/presign/document` | 文档预签名 |
| presignImage | POST | `/api/resource/resources/presign/image` | 图片预签名 |
| presignVideo | POST | `/api/resource/resources/presign/video` | 视频预签名 |

#### 文件上传流程

```
1. 前端调用 presign 接口获取预签名 URL
   ├── 文档/图片 → 返回 PresignedUrlVO { resourceId, uploadUrl, accessUrl }
   └── 视频 → 返回 VodPresignedUrlVO { resourceId, vodSessionKey, mediaUploadUrls, coverUploadUrl }

2. 前端使用预签名 URL 直传文件到云存储
   ├── 文档/图片 → PUT uploadUrl (Content-Type: file.type)
   └── 视频 → 分片上传到 mediaUploadUrls + 封面上传到 coverUploadUrl

3. 前端调用 confirm 接口确认上传
   ├── 文档/图片 → confirmUpload({ resourceId })
   └── 视频 → confirmVideoUpload({ resourceId, vodSessionKey })
```

---

### 2.7 系统管理模块 (`api/system/`)

#### 用户管理 (`user.ts`)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| getUserList | GET | `/api/system/users` | 系统用户列表 |
| getUser | GET | `/api/system/users/:id` | 用户详情 |
| addUser | POST | `/api/system/users` | 新增用户 |
| updateUser | PUT | `/api/system/users/:id` | 更新用户 |
| deleteUser | DELETE | `/api/system/users/:id` | 删除用户 |

#### 角色管理 (`role.ts`)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| getRoleList | GET | `/api/system/roles` | 角色列表 |
| addRole | POST | `/api/system/roles` | 新增角色 |
| updateRole | PUT | `/api/system/roles/:id` | 更新角色 |
| deleteRole | DELETE | `/api/system/roles/:id` | 删除角色 |

#### 菜单管理 (`menu.ts`)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| getMenuList | GET | `/api/system/menus` | 菜单列表 |
| addMenu | POST | `/api/system/menus` | 新增菜单 |
| updateMenu | PUT | `/api/system/menus/:id` | 更新菜单 |
| deleteMenu | DELETE | `/api/system/menus/:id` | 删除菜单 |

#### 部门管理 (`dept.ts`)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| getDeptList | GET | `/api/system/depts` | 部门列表 |
| addDept | POST | `/api/system/depts` | 新增部门 |
| updateDept | PUT | `/api/system/depts/:id` | 更新部门 |
| deleteDept | DELETE | `/api/system/depts/:id` | 删除部门 |

#### 通知管理 (`notice.ts`)

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| getNoticeList | GET | `/api/system/notices` | 通知列表 |
| addNotice | POST | `/api/system/notices` | 新增通知 |
| updateNotice | PUT | `/api/system/notices/:id` | 更新通知 |
| deleteNotice | DELETE | `/api/system/notices/:id` | 删除通知 |

---

## 3. 状态管理数据流

### 3.1 Store 架构

```
Pinia Store
├── user          # 用户认证与信息
├── course        # 课程数据
├── exam          # 考试/答题状态
├── permission    # 权限路由
├── app           # 应用 UI 状态
├── settings      # 主题/布局设置
└── tagsView      # 标签页状态
```

### 3.2 User Store 数据流

```
登录流程:
  login(LoginRequest) → API → LoginResult
    → 存储 accessToken/refreshToken 到 localStorage
    → fetchUserInfo() → API → UserDetailVO
      → userInfo 包含: userId, name, type, sex, avatarUrl, studentProfile, teacherProfile

计算属性:
  userType → userInfo.type
  isStudent → userType === 'Student'
  isTeacher → userType === 'Teacher'
  isAdmin → userType === 'Admin'
  isLoggedIn → !!token

登出流程:
  logout() → API → 清除 token/userInfo → 移除 localStorage
```

### 3.3 Course Store 数据流

```
课程浏览:
  fetchCategoryTree() → API → CategoryVO[] (缓存，只加载一次)
  fetchCourseList(CourseQueryRequest) → API → PageResult<CourseVO>
    → courseList + courseTotal

教学课程:
  fetchTeachingCourses(CourseQueryRequest) → API → PageResult<CourseVO>
    → teachingCourses + teachingTotal

课程详情:
  fetchCourseDetail(courseId) → API → CourseDetailVO (含 chapters)
    → currentCourse

计算属性:
  isCourseOwner → currentCourse.teacherId === userInfo.userId
```

### 3.4 Exam Store 数据流

```
进入考试:
  enterExamAction(examId) → API enterExam → AnswerSheetVO
    → getMySheet(examId) → AnswerSheetDetailVO
      → currentSheet + startCountdown()

答题过程:
  saveAnswerAction(sheetId, questionId, content)
    → answers Map 更新 → API saveAnswer

提交考试:
  submitExamAction(sheetId) → API submitExam
    → stopCountdown() → currentSheet.status = Ended

倒计时:
  remainingSeconds → 基于当前时间与 deadline 的差值
  到时自动提交

计算属性:
  isAnswering → currentSheet.status === InProgress
  answeredCount → answers.size
```

### 3.5 Permission Store 数据流

```
路由生成:
  generateRoutes()
    → 加载 dynamicRoutes → router.addRoute()
    → 设置 sidebarRouters / defaultRoutes / topbarRouters
    → isRoutesGenerated = true

路由守卫中:
  首次访问 → fetchUserInfo → generateRoutes → next({ ...to, replace: true })
  后续访问 → isRoutesGenerated === true → 直接放行
```

### 3.6 App Store 数据流

```
UI 状态:
  sidebar.opened → 侧边栏展开/折叠 (localStorage: sidebarStatus)
  device → 'desktop' | 'mobile' (响应式监听窗口宽度)
  size → Element Plus 组件尺寸 (localStorage: size)

持久化:
  size 字段通过 pinia-plugin-persistedstate 持久化
```

### 3.7 Settings Store 数据流

```
主题/布局设置:
  theme → 主题色 (#409EFF)
  sideTheme → 侧边栏主题 (theme-dark)
  isDarkMode → 暗黑模式 (useDark from @vueuse/core)
  topNav, tagsView, tagsIcon, fixedHeader, sidebarLogo...

持久化:
  通过 pinia-plugin-persistedstate 持久化到 localStorage (key: layout-setting)
  持久化字段: theme, sideTheme, topNav, tagsView, tagsIcon, fixedHeader, sidebarLogo, dynamicTitle, footerVisible
```

---

## 4. 通用类型定义

### 4.1 分页类型

```typescript
PageRequest {
  current?: number       // 当前页码
  pageSize?: number      // 每页条数
  sortField?: string     // 排序字段
  sortOrder?: 'asc' | 'desc'  // 排序方向
}

PageResult<T> {
  records: T[]           // 数据列表
  total: number          // 总记录数
  size: number           // 每页条数
  current: number        // 当前页码
  pages: number          // 总页数
}
```

### 4.2 API 响应类型

```typescript
ApiResponse<T> {
  code: ErrorCode        // 错误码
  data: T                // 响应数据
  message: string        // 响应消息
}
```

### 4.3 枚举汇总

| 枚举 | 值 | 说明 |
|------|-----|------|
| LoginType | 1-4 | 手机/邮箱/微信/用户名 |
| RegisterType | 字符串 | 手机/邮箱/微信注册 |
| UserType | Student/Teacher/Admin | 用户角色 |
| Sex | 未知/男/女 | 性别 |
| UserStatus | 正常/禁止 | 用户状态 |
| CourseStatus | 0/1/2 | 草稿/已发布/已归档 |
| YesNo | 0/1 | 否/是 |
| ClassStatus | 0/1/2 | 招募中/进行中/已结束 |
| QuestionType | 0-4 | 单选/多选/判断/填空/简答 |
| Difficulty | 1-5 | 非常简单→非常困难 |
| ExamType | 0/1/2 | 考试/练习/作业 |
| ExamStatus | 0/1/2/3 | 未开始/进行中/已结束/已批改 |
| SheetStatus | 0/1/2/3 | 未开始/进行中/已结束/已批改 |
| GradingStatus | 0/1/2 | 未批改/已批改/AI批改中 |
| PaperStatus | 0/1 | 草稿/已发布 |
| ResourceType | 1/2/3 | 视频/文档/图片 |
| UploadStatus | 0/1/2 | 待上传/成功/失败 |
| SectionResourceType | VIDEO/DOCUMENT/IMAGE | 小节资源类型 |

---

## 5. 组合式函数 (Composables)

| 函数 | 文件 | 功能 |
|------|------|------|
| useLoading | `useLoading.ts` | 管理加载状态 |
| useTable | `useTable.ts` | 表格通用逻辑（分页、搜索、排序） |
| useCountdown | `useCountdown.ts` | 倒计时（验证码 60s 倒计时） |
| useUpload | `useUpload.ts` | 上传通用逻辑 |
| usePermission | `usePermission.ts` | 权限判断 |
| useMessage | `useMessage.ts` | 消息提示封装 |
| useClipboard | `useClipboard.ts` | 剪贴板操作 |

---

## 6. 自定义指令

| 指令 | 文件 | 功能 |
|------|------|------|
| v-permission | `permission.ts` | 按权限控制元素显示/隐藏 |
| v-copy | `copy.ts` | 点击复制文本到剪贴板 |
