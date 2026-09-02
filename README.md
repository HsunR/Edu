# Intelli-Edu

Intelli-Edu 是一个前后端统一管理的智慧教育平台仓库。前端位于 `Intelli-Edu-frontend`，Spring Cloud 微服务位于 `Intelli-Edu-backend`。

## 本地一键启动

本地环境只要求安装 Docker Desktop 或 Docker Engine。首次启动会自动完成后端编译、前端依赖安装、空数据库创建、Flyway 表结构迁移，以及 Nacos 命名空间与配置初始化。

```powershell
cd Intelli-Edu-backend/.hsunr/docker
docker compose up -d --build
docker compose ps -a
```

常用入口：

- 前端：<http://localhost:5173>
- 网关接口文档：<http://localhost:8890/doc.html>
- Nacos：<http://localhost:8848/nacos>
- RabbitMQ：<http://localhost:15672>

完整说明、端口、数据初始化和排错方法见 [本地 Docker 环境文档](Intelli-Edu-backend/.hsunr/docker/README.md)。

## 仓库结构

```text
Intelli-Edu/
├── Intelli-Edu-backend/             # Java 21 / Spring Cloud 微服务
│   └── .hsunr/docker/               # 本地开发环境唯一编排入口
├── Intelli-Edu-frontend/            # Vue 3 / Vite 前端
└── README.md
```

## 外部功能

核心服务启动不要求云密钥。对象存储与视频点播、短信和邮件仍是可选远程能力；没有配置时，对应功能不可用，但不影响本地基础设施和核心微服务启动。变量模板见 `Intelli-Edu-backend/.hsunr/docker/.env.example`。
