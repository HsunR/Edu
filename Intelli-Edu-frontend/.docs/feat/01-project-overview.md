# 智慧教育平台 (Intelli-Edu) 前端项目概述

## 1. 项目基本信息

| 项目 | 说明 |
|------|------|
| 项目名称 | 智慧教育平台 (Intelli-Edu) |
| 版本 | 3.9.0 |
| 框架 | Vue 3.5.16 + TypeScript 6.x |
| 构建工具 | Vite 6.3.5 |
| UI 框架 | Element Plus 2.9.x |
| 状态管理 | Pinia 3.0.2 + pinia-plugin-persistedstate |
| 路由 | Vue Router 4.5.1 (History 模式) |
| HTTP 客户端 | Axios 1.9.0 |
| CSS 预处理 | Sass (sass-embedded 1.89.1) |
| 包管理器 | npm |

## 2. 技术栈详情

### 2.1 核心依赖

| 库 | 版本 | 用途 |
|----|------|------|
| vue | 3.5.16 | 前端框架 |
| vue-router | 4.5.1 | 路由管理 |
| pinia | 3.0.2 | 状态管理 |
| element-plus | 2.9.x | UI 组件库 |
| @element-plus/icons-vue | 2.3.1 | Element Plus 图标 |
| axios | 1.9.0 | HTTP 请求 |
| echarts | 5.6.0 | 图表可视化 |
| @vueuse/core | 13.3.0 | Vue 组合式工具集 |

### 2.2 功能性依赖

| 库 | 用途 |
|----|------|
| @videojs-player/vue + video.js | 视频播放器 |
| @vueup/vue-quill | 富文本编辑器 |
| relation-graph-vue3 | 知识图谱关系图 |
| marked | Markdown 渲染 |
| moment | 日期处理 |
| js-cookie | Cookie 操作 |
| jsencrypt | RSA 加密 |
| json-bigint | 大整数 JSON 解析 |
| file-saver | 文件下载 |
| clipboard | 剪贴板操作 |
| fuse.js | 模糊搜索 |
| vuedraggable | 拖拽排序 |
| splitpanes | 分屏面板 |
| vue-cropper | 图片裁剪 |

### 2.3 开发依赖

| 库 | 用途 |
|----|------|
| @vitejs/plugin-vue | Vue SFC 支持 |
| unplugin-auto-import | 自动导入 API |
| unplugin-vue-components | 自动注册组件 |
| unplugin-vue-setup-extend-plus | setup 语法扩展 |
| vite-plugin-compression | Gzip/Brotli 压缩 |
| vite-plugin-svg-icons | SVG 图标系统 |
| @umijs/openapi | OpenAPI 代码生成 |

## 3. 项目目录结构

```
src/
├── api/                    # API 接口层
│   ├── ai/                 # AI 相关接口（聊天、课程AI、上传）
│   ├── course/             # 课程相关接口（课程、章节、小节、班级）
│   ├── exam/               # 考试相关接口（题库、题目、试卷、考试、答题）
│   ├── knowledge/          # 知识点接口（知识点树、绑定关系）
│   ├── resource/           # 资源接口（文件上传、预签名、确认）
│   ├── system/             # 系统管理接口（用户、角色、菜单、部门、通知）
│   ├── user/               # 用户接口（认证、用户信息）
│   ├── index.ts            # API 统一导出
│   └── request.ts          # Axios 封装（拦截器、Token 刷新、错误处理）
├── assets/                 # 静态资源
│   ├── icons/svg/          # SVG 图标（80+个）
│   ├── images/             # 图片资源（登录背景、头像等）
│   ├── logo/               # Logo
│   └── styles/             # 全局样式（SCSS）
├── components/             # 全局公共组件
│   ├── Breadcrumb/         # 面包屑导航
│   ├── DictTag/            # 字典标签
│   ├── Editor/             # 富文本编辑器
│   ├── FileUpload/         # 文件上传
│   ├── Hamburger/          # 侧边栏折叠按钮
│   ├── HeaderSearch/       # 头部搜索
│   ├── IconSelect/         # 图标选择器
│   ├── ImagePreview/       # 图片预览
│   ├── ImageUpload/        # 图片上传
│   ├── Pagination/         # 分页组件
│   ├── ParentView/         # 父级路由视图
│   ├── ResourceSelector/   # 资源选择器
│   ├── ResourceUpload/     # 资源上传（支持视频/文档/图片预签名上传）
│   ├── RightToolbar/       # 右侧工具栏
│   ├── Screenfull/         # 全屏切换
│   ├── SvgIcon/            # SVG 图标组件
│   ├── TopNav/             # 顶部导航
│   ├── VideoPlayer/        # 视频播放器
│   ├── iFrame/             # 内嵌 iframe
│   └── setup.ts            # 全局组件注册
├── composables/            # 组合式函数
│   ├── useClipboard.ts     # 剪贴板
│   ├── useCountdown.ts     # 倒计时
│   ├── useLoading.ts       # 加载状态
│   ├── useMessage.ts       # 消息提示
│   ├── usePermission.ts    # 权限判断
│   ├── useTable.ts         # 表格通用逻辑
│   └── useUpload.ts        # 上传通用逻辑
├── directives/             # 自定义指令
│   ├── copy.ts             # 复制指令
│   └── permission.ts       # 权限指令
├── layout/                 # 布局组件
│   ├── components/         # 布局子组件
│   │   ├── Copyright/      # 版权信息
│   │   ├── IframeToggle/   # iframe 切换
│   │   ├── InnerLink/      # 内部链接
│   │   ├── Sidebar/        # 侧边栏（Logo、菜单项、链接）
│   │   ├── TagsView/       # 标签页视图
│   │   ├── AppMain.vue     # 主内容区
│   │   └── Navbar.vue      # 顶部导航栏
│   └── index.vue           # 主布局（侧边栏+顶部栏+内容区）
├── router/                 # 路由配置
│   ├── routes/
│   │   ├── constant.ts     # 常量路由（登录、404、首页、个人中心）
│   │   ├── course.ts       # 课程路由（学习、教学、详情等）
│   │   ├── dynamic.ts      # 动态权限路由
│   │   └── system.ts       # 系统管理路由
│   ├── guards.ts           # 路由守卫（认证、权限）
│   └── index.ts            # 路由实例
├── stores/                 # Pinia 状态管理
│   ├── app.ts              # 应用状态（侧边栏、设备、尺寸）
│   ├── course.ts           # 课程状态（当前课程、分类树、课程列表）
│   ├── exam.ts             # 考试状态（答题卡、答案、倒计时）
│   ├── permission.ts       # 权限路由状态
│   ├── settings.ts         # 设置状态（主题、暗黑模式等）
│   ├── tagsView.ts         # 标签页状态
│   └── user.ts             # 用户状态（Token、用户信息、登录登出）
├── types/                  # TypeScript 类型定义
│   ├── api.ts              # 通用 API 类型（分页、错误码、响应结构）
│   ├── enums.ts            # 枚举定义（用户类型、课程状态、题目类型等）
│   ├── router.ts           # 路由类型
│   └── global.d.ts         # 全局类型声明
├── utils/                  # 工具函数
│   ├── auth.ts             # Token 管理（localStorage）
│   ├── errorCode.ts        # 错误码映射
│   ├── index.ts            # 通用工具
│   ├── jsencrypt.ts        # RSA 加密
│   ├── theme.ts            # 主题工具
│   └── validate.ts         # 验证工具
├── views/                  # 页面视图
│   ├── course/             # 课程模块页面
│   ├── error/              # 错误页面（401、404）
│   ├── home/               # 首页
│   ├── login/              # 登录/注册
│   ├── message/            # 消息
│   ├── redirect/           # 重定向
│   ├── resource/           # 资源管理
│   ├── setting/            # 设置
│   └── system/             # 系统管理页面
├── App.vue                 # 根组件
├── main.ts                 # 入口文件
└── settings.ts             # 应用设置
```

## 4. 应用入口与初始化流程

### 4.1 main.ts 初始化顺序

1. 创建 Vue 应用实例
2. 创建 Pinia 并注册持久化插件
3. 注册路由
4. 注册 Element Plus（中文语言包）
5. 注册自定义指令（权限、复制）
6. 注册全局组件（Pagination、Editor、SvgIcon 等）
7. 挂载应用

### 4.2 路由守卫流程

```
用户访问页面
  ├── 有 Token？
  │   ├── 是 → 访问 /login？→ 重定向到首页
  │   │       → 其他页面 → 有用户信息？→ 放行
  │   │                     → 无用户信息 → 获取用户信息 → 生成权限路由 → 放行
  │   │                                       → 失败 → 登出 → 跳转登录页
  │   └── 否 → 在白名单中？(/login, /register) → 放行
  │           → 不在 → 跳转登录页（携带 redirect 参数）
```

## 5. 用户角色体系

系统定义了三种用户角色：

| 角色 | 枚举值 | 说明 |
|------|--------|------|
| 学生 | `Student` | 浏览课程、加入班级、学习、答题 |
| 教师 | `Teacher` | 创建课程、管理章节、出题、批改 |
| 管理员 | `Admin` | 系统管理、用户管理、权限控制 |

## 6. 认证机制

- **登录方式**：用户名密码、手机验证码、邮箱验证码、微信（预留）
- **Token 管理**：双 Token 机制（AccessToken + RefreshToken）
- **Token 存储**：localStorage（key: `intelli_edu_access_token` / `intelli_edu_refresh_token`）
- **Token 刷新**：401 响应时自动使用 RefreshToken 刷新，刷新期间请求排队等待
- **请求认证**：所有请求自动携带 `Authorization: Bearer {token}` 头

## 7. 全局配置

### 7.1 应用设置 (settings.ts)

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| title | 智慧教育平台 | 应用标题 |
| sideTheme | theme-dark | 侧边栏主题 |
| topNav | false | 是否启用顶部导航 |
| tagsView | true | 是否显示标签页 |
| fixedHeader | false | 是否固定头部 |
| sidebarLogo | true | 是否显示侧边栏 Logo |
| dynamicTitle | false | 动态标题 |
| footerVisible | false | 是否显示页脚 |

### 7.2 环境变量

| 变量 | 开发环境 | 生产环境 |
|------|----------|----------|
| VITE_APP_TITLE | 智慧教育平台 | 智慧教育平台 |
| VITE_APP_ENV | development | production |
| VITE_APP_BASE_API | (空) | (空) |
| VITE_BUILD_COMPRESS | - | gzip |
