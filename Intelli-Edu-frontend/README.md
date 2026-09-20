# Intelli Edu Frontend

智慧教育平台前端，使用 Vue 3、TypeScript、Vite 与 Tailwind CSS。页面功能严格对应当前后端 Controller，不包含本地业务 Mock。

```bash
pnpm install
pnpm dev
pnpm build
pnpm check
```

开发服务器默认运行于 `http://localhost:5173`，并将 `/api` 代理到 `http://localhost:8890`。请先启动仓库中的 Docker Compose 后端环境，再进行登录和业务操作。

## 环境变量

- `VITE_API_BASE_URL`：生产环境 API 根地址；留空时使用同源 `/api`。
- `VITE_PROXY_TARGET`：开发环境 Vite 代理目标，默认 `http://localhost:8890`。
- `VITE_OPEN_BROWSER`：设为 `false` 可禁止开发服务器自动打开浏览器。

`pnpm check` 会依次执行格式检查、单元测试、TypeScript 类型检查和生产构建。前端网络层使用无损 JSON 解析，后端的 19 位雪花 ID 会作为字符串在路由和请求中传递，避免浏览器数字精度丢失。

详细功能对照见 [BACKEND_INTEGRATION.md](BACKEND_INTEGRATION.md)。
