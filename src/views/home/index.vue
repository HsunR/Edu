<template>
  <div class="home-portal">
    <div class="portal-header">
      <h1 class="portal-title">欢迎使用 Intelli-Edu</h1>
      <p class="portal-subtitle">{{ greeting }}，{{ userStore.userInfo?.nickname || '同学' }}</p>
    </div>

    <div class="portal-grid">
      <div
        v-for="menu in menuCards"
        :key="menu.path"
        class="menu-card"
        :style="{ '--card-color': menu.color }"
        @click="handleMenuClick(menu)"
      >
        <div class="card-icon-wrapper">
          <svg-icon :icon-class="menu.icon" class="card-icon" />
        </div>
        <div class="card-content">
          <h3 class="card-title">{{ menu.title }}</h3>
          <p class="card-desc">{{ menu.desc }}</p>
        </div>
        <div class="card-arrow">
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { UserType } from '@/types/enums'

const router = useRouter()
const userStore = useUserStore()

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

interface MenuCard {
  title: string
  desc: string
  icon: string
  path: string
  color: string
  roles?: UserType[]
}

const allMenuCards: MenuCard[] = [
  {
    title: '课程浏览',
    desc: '浏览和搜索所有课程',
    icon: 'dashboard',
    path: '/course/browse',
    color: '#409EFF',
  },
  {
    title: '我学的课',
    desc: '查看已加入的课程',
    icon: 'education',
    path: '/course/learning',
    color: '#67C23A',
    roles: [UserType.Student, UserType.Teacher, UserType.Admin],
  },
  {
    title: '我教的课',
    desc: '管理你的教学课程',
    icon: 'skill',
    path: '/course/teaching',
    color: '#E6A23C',
    roles: [UserType.Teacher, UserType.Admin],
  },
  {
    title: '我的资源',
    desc: '管理个人资源文件',
    icon: 'documentation',
    path: '/resource',
    color: '#F56C6C',
  },
  {
    title: '系统管理',
    desc: '用户、角色与系统配置',
    icon: 'system',
    path: '/system/user',
    color: '#909399',
    roles: [UserType.Admin],
  },
  {
    title: '个人中心',
    desc: '查看和编辑个人信息',
    icon: 'user',
    path: '/user/profile',
    color: '#b37feb',
  },
]

const menuCards = computed(() => {
  const userType = userStore.userType
  return allMenuCards.filter((card) => {
    if (!card.roles) return true
    return card.roles.includes(userType as UserType)
  })
})

function handleMenuClick(menu: MenuCard) {
  router.push(menu.path)
}
</script>

<style scoped lang="scss">
.home-portal {
  min-height: calc(100vh - 100px);
  padding: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.portal-header {
  text-align: center;
  margin-bottom: 48px;
}

.portal-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--el-text-color-primary);
  margin: 0 0 8px 0;
}

.portal-subtitle {
  font-size: 16px;
  color: var(--el-text-color-secondary);
  margin: 0;
}

.portal-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  width: 100%;
  max-width: 1100px;
}

.menu-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 28px 24px;
  background: var(--el-bg-color-overlay, #fff);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid var(--el-border-color-lighter, #ebeef5);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 4px;
    height: 100%;
    background: var(--card-color);
    border-radius: 4px 0 0 4px;
    opacity: 0;
    transition: opacity 0.3s ease;
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
    border-color: var(--card-color);

    &::before {
      opacity: 1;
    }

    .card-icon-wrapper {
      background: var(--card-color);

      .card-icon {
        color: #fff;
      }
    }

    .card-arrow {
      opacity: 1;
      transform: translateX(0);
    }
  }
}

.card-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  background: color-mix(in srgb, var(--card-color) 12%, transparent);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.card-icon {
  font-size: 28px;
  color: var(--card-color);
  transition: color 0.3s ease;
}

.card-content {
  flex: 1;
  min-width: 0;
}

.card-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0 0 4px 0;
}

.card-desc {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin: 0;
}

.card-arrow {
  opacity: 0;
  transform: translateX(-8px);
  transition: all 0.3s ease;
  color: var(--card-color);
  font-size: 18px;
  flex-shrink: 0;
}
</style>
