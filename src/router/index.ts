import { createRouter, createWebHistory } from 'vue-router'
import { constantRoutes } from './routes/constant'
import { courseRoutes } from './routes/course'
import { systemRoutes } from './routes/system'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [...constantRoutes, ...courseRoutes, ...systemRoutes],
  scrollBehavior: () => ({ top: 0 }),
})

export default router

export { constantRoutes } from './routes/constant'
export { dynamicRoutes } from './routes/dynamic'
