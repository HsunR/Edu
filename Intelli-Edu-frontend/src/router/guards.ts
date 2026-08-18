import router from './index'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'

// 配置 NProgress：禁用右上角加载旋转图标
NProgress.configure({ showSpinner: false })

// 免登录白名单路径，未携带 token 时也可直接访问
const whiteList = ['/login', '/register', '/401']

// 全局前置守卫：处理登录态校验、用户信息获取与动态路由生成
router.beforeEach(async (to, _from, next) => {
  // 每次路由跳转前启动进度条
  NProgress.start()

  const token = getToken()

  if (token) {
    // 已登录时访问登录页，直接重定向到首页
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else {
      const userStore = useUserStore()
      const permissionStore = usePermissionStore()

      if (!userStore.userInfo) {
        try {
          await userStore.fetchUserInfo()
        } catch {
          await userStore.logout()
          ElMessage.error('获取用户信息失败，请重新登录')
          next(`/login?redirect=${to.fullPath}`)
          NProgress.done()
          return
        }
      }

      if (!permissionStore.isRoutesGenerated) {
        permissionStore.generateRoutes()
        next({ ...to, replace: true })
      } else {
        if (to.matched.length === 0) {
          next('/404')
          NProgress.done()
        } else {
          next()
        }
      }
    }
  } else {
    // 未登录：白名单内的路径直接放行，其余重定向到登录页并携带回跳地址
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.fullPath}`)
      NProgress.done()
    }
  }
})

// 全局后置守卫：路由跳转完成后关闭进度条
router.afterEach(() => {
  NProgress.done()
})
