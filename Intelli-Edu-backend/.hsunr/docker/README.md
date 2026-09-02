# Intelli-Edu 本地 Docker 环境

该目录是项目本地化运行的唯一入口。除 COS/VOD、短信和邮件等可选云功能外，前端、全部后端微服务以及基础设施均由 Docker Compose 启动。

## 环境要求

- Docker Desktop 4.x，或支持 Docker Compose v2 与 BuildKit 的 Docker Engine
- 建议为 Docker 分配至少 8 GB 内存
- 首次构建需要访问 Docker Hub 和 Maven Central

宿主机不需要安装 Java、Maven、Node.js、PostgreSQL、Redis、RabbitMQ 或 Nacos。

## 一键启动

在本目录执行：

```powershell
docker compose up -d --build
docker compose ps -a
```

第一次构建需要下载镜像与 Maven/npm 依赖，耗时会明显长于后续启动。后续启动直接执行：

```powershell
docker compose up -d
```

查看日志：

```powershell
docker compose logs -f
docker compose logs -f gateway
docker compose logs -f nacos-init
```

停止服务但保留数据：

```powershell
docker compose down
```

完全清空本地数据并重新验证首次初始化流程：

```powershell
docker compose down --volumes
docker compose up -d --build
```

`down --volumes` 会删除本项目的本地 PostgreSQL、Nacos 和前端依赖卷，只应在确认不需要这些本地数据时使用。

## 自动初始化

启动过程会自动完成：

1. Nacos 以单机开发模式启动。
2. `nacos-init` 创建 `Intelli-Edu-backend` 命名空间。
3. `nacos-config/*.yaml` 逐项发布到 `DEFAULT_GROUP`。
4. PostgreSQL 创建 10 个空业务数据库，并在 RAG 数据库启用 pgvector 扩展。
5. Redis 和 RabbitMQ 创建本地开发实例。
6. 多阶段 `Dockerfile.backend` 从源码统一编译全部后端模块。
7. 各业务服务启动时由 Flyway 执行自身的版本化数据库迁移，再注册到本地 Nacos；前端将 `/api` 代理到网关。

Nacos 配置使用可审查的文本文件，不再依赖包含远程密钥的二进制导出包。每次重建 `nacos-init` 容器时会幂等覆盖同名配置。

## 目录结构

```text
.hsunr/docker/
├── .env.example             # 可选云服务变量模板
├── .dockerignore            # 后端镜像构建上下文排除规则
├── docker-compose.yml       # 唯一服务编排文件
├── Dockerfile.backend       # 全部 Java 微服务的统一多阶段构建
├── nacos-config/            # 可版本管理的本地 Nacos 配置
├── postgres-init/           # PostgreSQL 空库与扩展初始化脚本
└── README.md
```

前端开发镜像位于 `Intelli-Edu-frontend/Dockerfile.dev`。

## 服务与端口

| 服务 | 本地地址或端口 |
|---|---|
| Vue/Vite 前端 | <http://localhost:5173> |
| 网关 / Knife4j | <http://localhost:8890/doc.html> |
| Nacos 控制台 | <http://localhost:8848/nacos> |
| user | 8891 |
| resource | 8892 |
| course | 8893 |
| exam | 8894 |
| knowledge | 8895 |
| learning | 8896 |
| PostgreSQL | localhost:5433 |
| Redis | localhost:6380 |
| RabbitMQ AMQP | localhost:5672 |
| RabbitMQ 管理端 | <http://localhost:15672> |

本地开发账号：

- PostgreSQL：`intelli_edu` / `intelli-edu-dev`
- Redis 密码：`intelli-edu-dev`
- RabbitMQ：`rabbitmq` / `intelli-edu-dev`

Nacos 关闭鉴权且只绑定 `127.0.0.1`，仅用于本地开发。

## 数据说明

`postgres-init` 只创建空数据库，并在 `intelli_edu_rag` 启用 pgvector。表、索引、约束和序列由各服务内的 Flyway 迁移管理；初始迁移来自 schema-only 导出，不包含用户账号或其他业务数据。因此：

- 服务可以正常建库和启动；
- 首次登录可能需要注册新账号；
- 不会把远程开发库中的业务数据复制到本机。

建库脚本只在 PostgreSQL 数据卷第一次创建时运行。修改它后需要删除 `postgres-data` 卷再启动，才能重新执行。

## 数据库迁移（Flyway）

当前可运行服务各自拥有一个独立迁移目录：

- `Intelli-Edu-user` → `intelli_edu_user`
- `Intelli-Edu-resource` → `intelli_edu_resource`
- `Intelli-Edu-course` → `intelli_edu_course`
- `Intelli-Edu-exam` → `intelli_edu_exam`
- `Intelli-Edu-Knowledge` → `intelli_edu_knowledge`
- `Intelli-Edu-learning` → `intelli_edu_learning`

迁移文件位于服务源码的 `src/main/resources/db/migration/`，遵循 `V<版本>__<说明>.sql` 命名规则。例如新增用户头像字段时，在 user 服务新增 `V2__add_user_avatar.sql`。不要修改已发布的迁移文件，也不要在 Docker 初始化脚本中添加业务表。

Flyway 会在服务启动时校验并执行未应用迁移。为兼容已有本地数据卷，非空且尚无 Flyway 记录的数据库会被标记为基线版本 `1`，不会重复执行初始建表；新建空库则自动执行 `V1`。应用侧禁用了 `clean`，重建数据库只能通过明确执行 `docker compose down --volumes` 完成。

## 可选远程能力

复制变量模板后可按需填写：

```powershell
Copy-Item .env.example .env
```

`.env` 已被 Git 忽略。支持的变量包括：

- 腾讯云 COS、VOD、短信
- SMTP 邮件

保留 `local-disabled` 默认值时，核心服务仍应启动，但调用相应云接口会失败。当前项目尚未实现本地对象存储与视频转码替代。

## 验证与排错

确认所有容器状态：

```powershell
docker compose ps -a
```

确认 Nacos 初始化成功：

```powershell
docker compose logs nacos-init
```

预期日志包含：

```text
Nacos namespace and configurations initialized successfully.
```

确认后端均使用本地 Nacos：

```powershell
docker compose config | Select-String 'nacos:8848'
```

常见问题：

- `nacos-init` 非 0 退出：查看其日志，通常是 Nacos 尚未就绪或配置文件格式错误。
- PostgreSQL 初始化失败：检查 `postgres` 日志；修正后使用 `docker compose down --volumes` 重新初始化。
- 后端构建失败：确认 Docker 可以访问 Maven Central，并查看 BuildKit 构建输出。
- 前端依赖失败：确认 Docker 可以访问 npm registry。
- 云功能报鉴权错误：在 `.env` 中填写对应密钥，或暂不调用该功能。
