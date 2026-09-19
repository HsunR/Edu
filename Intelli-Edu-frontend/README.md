# Intelli Edu Frontend

智慧教育平台前端，使用 Vue 3、TypeScript、Vite 与 Tailwind CSS。页面功能严格对应当前后端 Controller，不包含本地业务 Mock。

```bash
pnpm install
pnpm dev
pnpm build
```

开发服务器默认运行于 `http://localhost:5173`，并将 `/api` 代理到 `http://localhost:8890`。请先启动仓库中的 Docker Compose 后端环境，再进行登录和业务操作。

详细功能对照见 [BACKEND_INTEGRATION.md](BACKEND_INTEGRATION.md)。
