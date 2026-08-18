# Intelli-Edu Docker 部署指南

## 目录结构

```
.hsunr/docker/
├── docker-compose.yml    # 服务编排
├── Dockerfile.gateway    # 网关服务
├── Dockerfile.user       # 用户服务
├── Dockerfile.resource   # 资源服务
├── Dockerfile.course     # 课程服务
├── Dockerfile.exam       # 考试服务
├── Dockerfile.knowledge  # 知识库服务
├── Dockerfile.learning   # 学习服务
├── Dockerfile.ai         # AI 服务
├── .dockerignore         # Docker 构建忽略规则
└── README.md             # 本说明文档
```

## 前置要求

- Docker Desktop（Windows）或 Docker Engine（Linux）
- 项目已通过 `mvn clean package -DskipTests` 构建，各模块 `target/` 目录下存在 JAR 包
- 远程 Nacos (`8.134.210.227:8848`) 必须可达，所有服务配置从 Nacos 获取

## 启动命令

在 `docker-compose.yml` 所在目录（`.hsunr/docker/`）执行：

```bash
# 一键构建 + 启动所有服务
docker-compose up -d --build

# 查看实时日志
docker-compose logs -f

# 查看某个服务的日志
docker-compose logs -f gateway

# 停止所有服务
docker-compose down

# 重启单个服务（例如 gateway）
docker-compose restart gateway
```

## 服务端口映射

| 服务 | 容器端口 | 宿主机端口 |
|------|---------|-----------|
| Intelli-Edu-gateway | 8890 | 8890 |
| Intelli-Edu-user | 8891 | 8891 |
| Intelli-Edu-resource | 8892 | 8892 |
| Intelli-Edu-course | 8893 | 8893 |
| Intelli-Edu-exam | 8894 | 8894 |
| Intelli-Edu-knowledge | 8895 | 8895 |
| Intelli-Edu-learning | 8896 | 8896 |
| Intelli-Edu-ai | 8897 | 8897 |

## 注意事项

- 构建时会从 Docker Hub 拉取 `eclipse-temurin:21-jre` 基础镜像，请确保网络畅通
- 所有服务依赖远程 Nacos 获取配置，本地无需配置数据库连接等参数
- 代码变更后需重新执行 `mvn clean package -DskipTests` 打包，再执行 `docker-compose up -d --build` 更新镜像
