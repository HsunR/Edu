# Intelli-Edu-learning 模块开发进度与接口说明

> 服务名：`Intelli-Edu-learning`  
> 端口：`8896`  
> Context Path：`/api/learning`  
> 数据库：`intelli_edu_learning`  

---

## 一、模块定位

Learning 模块负责**学情事实数据**的存储与查询，以及**规则化个性化推荐**编排：

- **写入**：考试批改完成后，通过 RabbitMQ 消费 `SheetGradedEvent`，增量更新 `lp_mastery`、`lp_wrong_record`、`lp_wrong_point`。
- **读取**：学生/教师通过 HTTP API 查询掌握度、错题；学生端图谱与推荐；对内 Feign 供 AI 模块 RAG。
- **边界**：LLM 不参与题目排序；AI 模块只消费本模块的结构化结果生成自然语言。

与 Knowledge（知识点目录）、Exam（题目）、Course（班级权限）共用 `point_id` / `question_id` / `class_id`，无跨库物理外键。

---

## 二、整体完成度概览

| 维度 | 完成度 | 说明 |
|------|--------|------|
| 数据库表 + MQ 写入链路 | ✅ 已完成 | 4 张 `lp_*` 表 + `GradedEventHandler` |
| 学生端学情 API（F3） | ✅ 后端完成 | 5 个接口；前端未对接 |
| 学生端课程图谱（F2） | ✅ 后端完成 | 1 个聚合接口；前端三 Tab 未对接 |
| 学生端规则推荐（F5） | ✅ 后端完成 | WMCLR 算法；无资源推荐、无整包缓存 |
| 教师端学情统计（F4） | ✅ 后端完成 | 5 个统计接口 + 1 个推荐；无教师前端页 |
| 对内 Inner API | ⚠️ 部分完成 | `profile/summary`、`mastery/list` 已有；缺 `wrong/detail`、Feign `recommend` |
| 前端页面 | ❌ 未做 | 错题集、课程图谱、学情分析、推荐 UI 均为占位 |
| AI 能力（F6～F8） | ❌ 未做 | 在 AI 服务，依赖 Learning Feign |
| 单元测试 | ❌ 未做 | 无 `*Test.java` |
| 接口联调验证 | 🟡 进行中 | 需经网关 8890 + Bearer Token；RabbitMQ 环境待就绪 |

**后端 HTTP 接口：15 个已全部实现。**

---

## 三、已完成功能清单

### 3.1 数据层与 MQ

| 组件 | 路径 | 作用 |
|------|------|------|
| `LpMastery` 等 4 实体 | `model/entity/` | 映射 `lp_mastery`、`lp_wrong_record`、`lp_wrong_point`、`lp_sheet_graded_log` |
| `LpMasteryMapper.xml` | `resources/mapper/` | 班级掌握度聚合 SQL（教师端） |
| `LpWrongRecordMapper.xml` | `resources/mapper/` | 高频错题、知识点分布、错因分布等 |
| `GradedEventConsumer` | `consumer/` | 监听 `PROFILE_SHEET_GRADED` 队列 |
| `GradedEventHandler` | `service/` | 幂等写入/回滚掌握度与错题；维护 `lp_sheet_graded_log` |
| `LearningMqConfig` | `config/` | 声明队列与绑定 |

### 3.2 服务层核心类

| 类 | 功能包 | 职责 |
|----|--------|------|
| `StudentLearningService` | F3 | 学生学情编排、班级成员校验 |
| `MasteryQueryService` | F3/F2/F4 | `lp_mastery` 查询与薄弱点筛选 |
| `WrongRecordQueryService` | F3 | 错题分页、统计、归属校验 |
| `LearningEnrichmentService` | F3/F4 | Feign Knowledge 补知识点名称 |
| `GraphOverviewService` | F2 | Knowledge 树 + 掌握度合并为 `GraphOverviewVO` |
| `TeacherLearningService` | F4 | 班级维度聚合 + User Feign 补学生姓名 |
| `ClassAccessService` | 通用 | 学生成员校验、教师任教班级校验（Course Feign） |
| `RecommendService` | F5 | 推荐编排入口（学生/教师） |
| `WeakPointDetector` | F5 | 薄弱度计算（BKT 简化思路） |
| `QuestionRanker` | F5 | WMCLR 题目精排 + Redis 重复惩罚 |
| `InnerLearningService` | Inner | 学情摘要 DTO，供 AI RAG |

### 3.3 配置

| 配置前缀 | 类 | 主要项 |
|----------|-----|--------|
| `learning.*` | `LearningProperties` | `weakMasteryThreshold=60`、`weakConfidenceMinAnswers=3` |
| `recommend.*` | `RecommendProperties` | WMCLR 五维权重、默认 limit、重复推荐天数 |

### 3.4 跨服务依赖（已实现）

| 服务 | Feign / Inner | 用途 |
|------|---------------|------|
| Course | `checkClassTeacher`、`getSectionBatch` | 权限、推荐章节标题 |
| Knowledge | `getPointTree`、`getPointBatch`、`getQuestionIdsByPoint`、`getSectionIdsByPoint`、`getPointsByQuestions` | 图谱、富化、内容召回 |
| Exam | `getQuestionBatch` | 推荐题目题干、题型 |
| User | `getUserSimpleBatch` | 教师端错题详情学生姓名 |
| API 模块 | `LearningFeignClient.getProfileSummary` | AI 模块调用学情摘要 |

### 3.5 文档与测试资产

| 文件 | 说明 |
|------|------|
| `docs/Learning模块接口测试.md` | 联调说明、参数、常见错误 |
| `docs/learning-api.http` | 15 个接口 REST Client 用例 |
| `数据库/learning_test_data.sql` | 跨库最小测试数据 |
| `数据库/learning_test_data_password_update.sql` | 测试账号密码更新（`Lp@123456`） |
| `intelli-edu-推荐与AI能力-算法与实现方案.md` | WMCLR / RAG 算法说明（项目根目录） |

---

## 四、待办事项（未做 / 未完成）

### 4.1 P0 — 前端对接（阻塞用户可见功能）

| 任务 | 功能包 | 说明 |
|------|--------|------|
| 新建 `src/api/learning/` | F3/F2/F5 | `mastery.ts`、`wrong.ts`、`graph.ts`、`recommend.ts`、`types.ts` |
| `ErrorSet/ErrorSetIndex.vue` | F3 | 错题分页、统计、标记解决 |
| `CurriculumMap` 三 Tab | F2 | 对接 `graph/overview`；传递 `classId` |
| 推荐入口 UI | F5 | 图谱「去复习」、错题「同类练习」、首页「今日复习」 |
| `TeachingCourses/LearningAnalytics/` | F4 | 教师学情分析页（新建） |

### 4.2 P1 — 后端增强

| 任务 | 说明 |
|------|------|
| 错题列表补题干 | `WrongRecordVO` 未 Feign Exam 填充 `stem` |
| 错题按 `pointId` 筛选 | `WrongRecordQueryRequest` 无该字段 |
| 推荐结果中的学习资源 | `RecommendResultVO` 无 `resources` 字段，未接 Resource Feign |
| 推荐整包 Redis 缓存 | 仅有单题重复惩罚，无 `recommend:{studentId}:{classId}:{scene}` TTL 缓存 |
| `LearningFeignClient.recommend` | AI F7 需要，当前 Feign 仅 `getProfileSummary` |
| Inner `GET /inner/wrongs/{wrongId}` | AI F6 错因分析前置 |
| 图谱 `kn_knowledge_relation` 边 | 当前仅用父子树，无前置依赖关系边 |
| 已删知识点孤儿数据 | 展示层过滤策略未专门实现 |

### 4.3 P2 — AI 模块（非 Learning 服务内）

| 功能包 | 内容 | 状态 |
|--------|------|------|
| F6 | `POST /api/ai/student/wrong-analysis` | ❌ |
| F7 | `POST /api/ai/student/learning-report`、复习话术 | ❌ |
| F8 | 教师 AI 教学建议 | ❌ |

### 4.4 P3 — 工程与运维

| 任务 | 说明 |
|------|------|
| 单元测试 | `WeakPointDetector`、`QuestionRanker`、`GradedEventHandler` 等 |
| RabbitMQ 环境 | 本地需 `localhost:5672` 或修改配置；否则批改无法自动写 `lp_*` |
| 网关鉴权联调 | 必须 `8890` + `Authorization: Bearer {accessToken}`，见 §七 |
| 项目总 `接口文档.md` | 未同步 Learning 15 个新接口 |
| Maven 全量编译 | 曾因 JVM OOM 未完整验证 |

---

## 五、HTTP 接口详细说明（含实现注释）

> 基础路径：`/api/learning`  
> 鉴权：学生/教师接口需 `@RequireRole`；`studentId`/`teacherId` 从 Token 经网关注入 `UserContextHolder`，**禁止客户端传 userId 越权**。  
> Inner 接口无角色注解，供服务间 Feign 直连 `8896`。

---

### 5.1 学生端学情 — `StudentLearningController`（F3）

#### ① `GET /student/mastery/overview`

| 项 | 内容 |
|----|------|
| **实现类** | `StudentLearningService.getMasteryOverview` → `MasteryQueryService.listOverview` |
| **作用** | 查询当前学生在指定班级下，各知识点的掌握度列表，用于学情概览卡片或图谱对照。 |
| **数据来源** | `lp_mastery`（`student_id` + `class_id`） |
| **权限** | `@RequireRole(STUDENT)` + `ClassAccessService.validateStudentMember` |
| **富化** | 通过 Knowledge Feign 补充 `pointName` |
| **Query** | `classId`（必填） |
| **响应** | `List<MasteryOverviewVO>`：`pointId`、`pointName`、`masteryLevel`、`answerCount`、`correctCount`、`lastPracticeAt` 等 |
| **说明** | 无掌握度记录的知识点不会出现在列表中；与图谱接口中「未练习」的展示策略由前端合并处理。 |

---

#### ② `GET /student/mastery/weak-points`

| 项 | 内容 |
|----|------|
| **实现类** | `StudentLearningService.getWeakPoints` → `MasteryQueryService.listWeakPoints` |
| **作用** | 筛选掌握度低于阈值的知识点，用于「薄弱点」专区或推荐入口。 |
| **阈值** | `learning.weak-mastery-threshold`（默认 60） |
| **权限** | 同学情概览 |
| **Query** | `classId`（必填） |
| **响应** | `List<MasteryOverviewVO>`，按掌握度升序 |

---

#### ③ `GET /student/wrongs`

| 项 | 内容 |
|----|------|
| **实现类** | `StudentLearningService.pageWrongRecords` → `WrongRecordQueryService.pageWrongRecords` |
| **作用** | 错题本分页列表，支持按班级、课程、题型、是否已解决筛选。 |
| **数据来源** | `lp_wrong_record` + `lp_wrong_point`（关联知识点） |
| **权限** | 若传 `classId` 则校验班级成员 |
| **Query** | `classId`、`courseId`、`questionType`、`isResolved`、`current`、`pageSize` |
| **响应** | `Page<WrongRecordVO>`：错题元数据 + `points[]`（知识点 ID/名称） |
| **待增强** | 未 Feign Exam 补题目 `stem`（题干） |

---

#### ④ `GET /student/wrongs/stats`

| 项 | 内容 |
|----|------|
| **实现类** | `StudentLearningService.getWrongStats` → `WrongRecordQueryService.statWrongRecords` |
| **作用** | 错题统计：按题型分布、按知识点分布，供错题集页图表展示。 |
| **数据来源** | `LpWrongRecordMapper.statByQuestionType`、`statByKnowledgePoint` |
| **Query** | `classId` 与 `courseId` **至少传一个** |
| **响应** | `WrongStatsVO`：`byQuestionType[]`、`byKnowledgePoint[]`（含 `pointName`） |

---

#### ⑤ `PUT /student/wrongs/{wrongId}/resolve`

| 项 | 内容 |
|----|------|
| **实现类** | `StudentLearningService.resolveWrongRecord` |
| **作用** | 学生手动标记某条错题为「已解决」。 |
| **逻辑** | 校验 `wrongId` 归属当前学生 → 更新 `is_resolved=1`、`resolved_at=now()`；已解决则幂等返回 |
| **Path** | `wrongId` |
| **响应** | 无 body（void） |

---

### 5.2 学生端课程图谱 — `StudentGraphController`（F2）

#### ⑥ `GET /student/graph/overview`

| 项 | 内容 |
|----|------|
| **实现类** | `GraphOverviewService.getStudentGraphOverview` |
| **作用** | **聚合接口**：一次返回「课程知识点树 + 当前学生在该班的掌握度」，供大纲/思维导图/图谱三 Tab 共用。 |
| **步骤** | ① 校验班级成员 ② `KnowledgeFeignClient.getPointTree(courseId)` ③ 查 `lp_mastery` 建 `pointId→masteryLevel` Map ④ 递归合并为 `GraphPointVO` 树 ⑤ 每节点 Feign 查 `sectionIds` 供跳转章节 |
| **Query** | `classId`、`courseId`（均必填） |
| **响应** | `GraphOverviewVO`：`courseId`、`classId`、`weakThreshold`、`points[]`（树形，含 `masteryLevel`、`isWeak`、`sectionIds`、`childPoints`） |
| **说明** | 未作答知识点 `masteryLevel=null`；`isWeak` 仅在有掌握度且低于阈值时为 true。`kn_knowledge_relation` 关系边未接入。 |

---

### 5.3 学生端个性化推荐 — `StudentRecommendController`（F5）

#### ⑦ `GET /student/recommend`

| 项 | 内容 |
|----|------|
| **实现类** | `RecommendService.recommendForStudent` |
| **作用** | 基于个人 `lp_mastery` + `lp_wrong_record`，按场景输出薄弱知识点、推荐题目、关联章节；**WMCLR 算法排序，不用 LLM**。 |
| **算法** | ① `WeakPointDetector` 识别薄弱点 ② `collectCandidateQuestionIds` 按场景从 `kn_question_knowledge` 召回 ③ `QuestionRanker` WMCLR 精排 ④ 关联章节 `buildSectionRecommends` |
| **场景 scene** | 见下表 |
| **Query** | `classId`（必填）、`courseId`（可选）、`scene`（默认 `REVIEW_WEAK`）、`limit`（1～50，默认 10） |
| **响应** | `RecommendResultVO`：`scene`、`summary`、`points[]`、`questions[]`、`sections[]` |

| scene | 触发场景 | 候选题召回策略 |
|-------|----------|----------------|
| `REVIEW_WEAK` | 图谱/掌握度「去复习」 | 薄弱知识点关联题目 |
| `REVIEW_WRONG` | 错题集「同类练习」 | 未解决错题 + 同知识点题目 |
| `DAILY_PLAN` | 课程首页「今日复习」 | 错题 + 薄弱点题目综合；题目 limit 最多 3 |

**WMCLR 公式：**

```text
Score(q) = w1*S_weak + w2*S_wrong + w3*S_diff + w4*S_new - w5*S_rep
```

权重见 `RecommendProperties`；`S_rep` 通过 Redis `learning:recommend:{studentId}:{questionId}` 惩罚近 7 天已推荐题。

**待增强：** 无 `resources[]`（节下视频/文档）；无整包结果缓存。

---

### 5.4 教师端学情 — `TeacherLearningController`（F4）

#### ⑧ `GET /teacher/mastery/class-overview`

| 项 | 内容 |
|----|------|
| **实现类** | `TeacherLearningService.getClassMasteryOverview` → `LpMasteryMapper.statClassMastery` |
| **作用** | 班级维度：每个知识点的平均掌握度、参与学生数、薄弱人数，用于教师学情总览。 |
| **权限** | `@RequireRole(TEACHER)` + `validateClassTeacher` |
| **Query** | `classId` |
| **响应** | `List<ClassMasteryPointVO>`：`pointId`、`pointName`、`avgMasteryLevel`、`studentCount`、`weakStudentCount` |

---

#### ⑨ `GET /teacher/mastery/student`

| 项 | 内容 |
|----|------|
| **实现类** | `TeacherLearningService.getStudentMastery` → 复用 `MasteryQueryService.listOverview` |
| **作用** | 教师查看**指定学生**在某班的完整掌握度列表（与学生端 overview 数据一致，视角不同）。 |
| **Query** | `classId`、`studentId` |
| **响应** | `List<MasteryOverviewVO>` |

---

#### ⑩ `GET /teacher/wrongs/frequent`

| 项 | 内容 |
|----|------|
| **实现类** | `TeacherLearningService.getFrequentWrongs` → `LpWrongRecordMapper.statFrequentQuestions` |
| **作用** | 班级高频错题 TOP N：按 `SUM(wrong_count)` 降序，用于备课重点。 |
| **Query** | `classId`；`limit`（可选，默认 20） |
| **响应** | `List<FrequentWrongQuestionVO>`：`questionId`、`questionType`、`wrongStudentCount`、`totalWrongTimes` |

---

#### ⑪ `GET /teacher/wrongs/point-distribution`

| 项 | 内容 |
|----|------|
| **实现类** | `TeacherLearningService.getClassWrongPointDistribution` |
| **作用** | 班级错题按知识点分布统计（全班聚合，非单学生）。 |
| **Query** | `classId` |
| **响应** | `WrongStatsVO`（仅填充 `byKnowledgePoint`） |

---

#### ⑫ `GET /teacher/wrongs/question-detail`

| 项 | 内容 |
|----|------|
| **实现类** | `TeacherLearningService.getQuestionWrongDetail` |
| **作用** | 某题在班内的错答明细：哪些学生错过、各 `wrong_type` 分布。 |
| **Query** | `classId`、`questionId` |
| **响应** | `WrongQuestionDetailVO`：`students[]`（含 `studentName`）、`wrongTypeDistribution[]` |

---

#### ⑬ `GET /teacher/recommend`

| 项 | 内容 |
|----|------|
| **实现类** | `RecommendService.recommendForTeacher` |
| **作用** | 教师教学干预推荐：`scene` 固定为 `TEACHER_INTERVENTION`；输出班薄弱知识点 + 班高频错题 + 建议补讲章节。 |
| **逻辑** | 班级掌握度 `statClassMastery` 取平均最低的知识点；`statFrequentQuestions` 取高频错题；不跑学生级 WMCLR |
| **Query** | `classId`；`limit`（可选） |
| **响应** | `RecommendResultVO`（同学生端结构） |

---

### 5.5 对内接口 — `InnerLearningController`（Feign / AI）

> 一般不经过网关；无 `@RequireRole`。调用方（如 AI 服务）通过 Feign 访问 `http://Intelli-Edu-learning/api/learning/inner/**`。

#### ⑭ `GET /inner/profile/summary`

| 项 | 内容 |
|----|------|
| **实现类** | `InnerLearningService.buildProfileSummary` |
| **Feign** | `LearningFeignClient.getProfileSummary` |
| **作用** | 生成「学情一页纸」结构化摘要，供 **AI RAG** 注入 Prompt；**不是推荐算法输出**。 |
| **聚合内容** | 平均掌握度、薄弱点 Top5（含名称）、未解决错题数、高频错题 Top5（`questionId`、`wrongCount`、`wrongType`） |
| **Query** | `studentId`、`classId` |
| **响应** | `LearningProfileSummaryDTO` |

---

#### ⑮ `GET /inner/mastery/list`

| 项 | 内容 |
|----|------|
| **实现类** | `MasteryQueryService.listOverview`（直接复用） |
| **作用** | 内部获取学生掌握度全量列表，避免 AI 模块重复写 SQL。 |
| **Query** | `studentId`、`classId` |
| **响应** | `List<MasteryOverviewVO>` |

---

## 六、非 HTTP 能力说明

### 6.1 MQ 消费 — 学情数据写入唯一入口

```
Exam finishGrading
  → 发布 SheetGradedEvent（RabbitMQ）
  → GradedEventConsumer.onSheetGraded
  → GradedEventHandler.handle
      → upsert lp_mastery（按知识点累计得分/题数）
      → upsert lp_wrong_record（错题累加 wrong_count）
      → 维护 lp_wrong_point（题-点关联）
      → 写入 lp_sheet_graded_log（幂等 + 重复提交回滚）
```

**掌握度公式：** `mastery_level = round(earned_score / total_score × 100)`（`total_score=0` 时为 0）。

### 6.2 权限校验 — `ClassAccessService`

| 方法 | 调用方 | 实现 |
|------|--------|------|
| `validateStudentMember` | 学生端 API | Course Feign 校验学生是否在班 |
| `validateClassTeacher` | 教师端 API | `CourseFeignClient.checkClassTeacher` |

---

## 七、联调注意事项

### 7.1 鉴权（常见报错）

| 现象 | 原因 | 处理 |
|------|------|------|
| `未获取到用户类型信息` | 直连 8896 且未带网关注入头 | 改走 `8890` + `Authorization: Bearer {token}` |
| 403 权限不足 | 学生 Token 调 `/teacher/**` 或用户不在该班 | 换对应角色 Token / 检查 `co_class_member` |

Learning 服务**不解析 JWT**，依赖网关写入：

- `X-Request-From: gateway`
- `X-User-Id`
- `X-User-Type`（学生 `1`，教师 `2`）

### 7.2 测试数据

执行 `数据库/learning_test_data.sql` 后可用：

- 学生：`lp_test_student` / `Lp@123456`
- 教师：`lp_test_teacher` / `Lp@123456`
- `classId=999999010000000004`，`courseId=999999010000000003`

### 7.3 RabbitMQ

消费者连 `localhost:5672` 失败时，批改事件无法入库，HTTP 查询为空属正常；可用手动 SQL 或启动 RabbitMQ。

---

## 八、接口总览速查表

| # | 方法 | 路径 | 角色 | 功能包 | 请求体 |
|---|------|------|------|--------|--------|
| 1 | GET | `/student/mastery/overview` | 学生 | F3 | 无 |
| 2 | GET | `/student/mastery/weak-points` | 学生 | F3 | 无 |
| 3 | GET | `/student/wrongs` | 学生 | F3 | 无 |
| 4 | GET | `/student/wrongs/stats` | 学生 | F3 | 无 |
| 5 | PUT | `/student/wrongs/{wrongId}/resolve` | 学生 | F3 | 无 |
| 6 | GET | `/student/graph/overview` | 学生 | F2 | 无 |
| 7 | GET | `/student/recommend` | 学生 | F5 | 无 |
| 8 | GET | `/teacher/mastery/class-overview` | 教师 | F4 | 无 |
| 9 | GET | `/teacher/mastery/student` | 教师 | F4 | 无 |
| 10 | GET | `/teacher/wrongs/frequent` | 教师 | F4 | 无 |
| 11 | GET | `/teacher/wrongs/point-distribution` | 教师 | F4 | 无 |
| 12 | GET | `/teacher/wrongs/question-detail` | 教师 | F4 | 无 |
| 13 | GET | `/teacher/recommend` | 教师 | F4/F5 | 无 |
| 14 | GET | `/inner/profile/summary` | 内部 | AI | 无 |
| 15 | GET | `/inner/mastery/list` | 内部 | AI | 无 |

---

## 九、建议后续开发顺序

1. 网关 + 测试 SQL 跑通 15 个接口（`learning-api.http`）。
2. 启动 RabbitMQ，验证批改 → MQ → `lp_*` 自动写入。
3. 前端 `api/learning` + 错题集（F3）→ 课程图谱（F2）→ 推荐入口（F5）。
4. 教师学情分析页（F4）。
5. 后端增强：错题题干、推荐资源、Feign `recommend`。
6. AI 模块 F6～F8 对接 Inner API。

---

## 十、相关文档

| 文档 | 路径 |
|------|------|
| 功能开发总方案 | `intelli-edu-知识点与推荐-功能开发文档.md` |
| 算法与 AI 方案 | `intelli-edu-推荐与AI能力-算法与实现方案.md` |
| 接口联调手册 | `docs/Learning模块接口测试.md` |
| HTTP 用例文件 | `docs/learning-api.http` |
