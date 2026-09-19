<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  BarChart3,
  Bell,
  BookOpen,
  ChevronDown,
  ClipboardCheck,
  Compass,
  GraduationCap,
  LayoutDashboard,
  Library,
  LogOut,
  Menu,
  ShieldCheck,
  UserRound,
  Users,
  X,
} from 'lucide-vue-next'
import BrandMark from '@/components/BrandMark.vue'
import { useSessionStore } from '@/stores/session'

const route = useRoute()
const router = useRouter()
const session = useSessionStore()
const profileOpen = ref(false)
const nav = computed(() => [
  { to: '/', label: '工作台', icon: LayoutDashboard },
  { to: '/courses', label: '课程浏览', icon: Compass },
  ...(session.role === 'Student' ? [{ to: '/my-classes', label: '我的班级', icon: BookOpen }] : []),
  ...(session.role === 'Teacher'
    ? [{ to: '/teaching', label: '课程管理', icon: GraduationCap }]
    : []),
  { to: '/exams', label: '考试与作业', icon: ClipboardCheck },
  { to: '/resources', label: '我的资源', icon: Library },
  ...(['Student', 'Teacher'].includes(session.role || '')
    ? [{ to: '/learning', label: '学情中心', icon: BarChart3 }]
    : []),
  ...(session.role === 'Admin' ? [{ to: '/users', label: '用户管理', icon: ShieldCheck }] : []),
])
const userName = computed(() => session.user?.name || '用户')
const profileLine = computed(
  () =>
    session.user?.studentProfile?.major ||
    session.user?.teacherProfile?.department ||
    session.user?.school ||
    session.roleLabel,
)
async function logout() {
  await session.logout()
  router.push('/login')
}
</script>

<template>
  <div class="min-h-screen bg-canvas">
    <Transition name="fade"
      ><div
        v-if="session.sidebarOpen"
        class="fixed inset-0 z-40 bg-ink/30 backdrop-blur-sm lg:hidden"
        @click="session.sidebarOpen = false"
    /></Transition>
    <aside
      :class="[
        'fixed inset-y-0 left-0 z-50 flex w-[260px] flex-col border-r bg-[#fbfcf8] px-5 py-6 transition-transform duration-300 lg:translate-x-0',
        session.sidebarOpen ? 'translate-x-0' : '-translate-x-full',
      ]"
    >
      <div class="flex items-center justify-between px-2">
        <BrandMark /><button class="icon-btn lg:hidden" @click="session.sidebarOpen = false">
          <X :size="18" />
        </button>
      </div>
      <nav class="mt-10 flex-1 space-y-1">
        <p class="eyebrow mb-3 px-3">已接入服务</p>
        <RouterLink
          v-for="item in nav"
          :key="item.to"
          :to="item.to"
          :class="[
            'flex items-center gap-3 rounded-2xl px-3 py-3 text-sm font-semibold transition',
            route.path === item.to || (item.to !== '/' && route.path.startsWith(item.to))
              ? 'bg-ink text-white shadow-lg shadow-ink/12'
              : 'text-[#68716b] hover:bg-[#eff1eb] hover:text-ink',
          ]"
          @click="session.sidebarOpen = false"
          ><component :is="item.icon" :size="19" />{{ item.label }}</RouterLink
        >
      </nav>
      <RouterLink to="/profile" class="flex items-center gap-3 rounded-2xl p-2 hover:bg-[#eff1eb]"
        ><div
          class="grid size-10 place-items-center overflow-hidden rounded-full bg-[#f2c79b] text-sm font-black"
        >
          <img
            v-if="session.user?.avatarUrl"
            :src="session.user.avatarUrl"
            class="h-full w-full object-cover"
          />
          <span v-else>{{ userName.slice(0, 1) }}</span>
        </div>
        <div class="min-w-0 flex-1">
          <p class="truncate text-sm font-bold">{{ userName }}</p>
          <p class="truncate text-[11px] text-[#89918c]">{{ profileLine }}</p>
        </div>
        <UserRound :size="16"
      /></RouterLink>
    </aside>
    <div class="lg:pl-[260px]">
      <header
        class="sticky top-0 z-30 flex h-[76px] items-center border-b bg-canvas/88 px-5 backdrop-blur-xl sm:px-8 lg:px-10"
      >
        <button class="icon-btn mr-3 lg:hidden" @click="session.sidebarOpen = true">
          <Menu :size="19" />
        </button>
        <div>
          <p class="eyebrow hidden sm:block">{{ route.meta.eyebrow }}</p>
          <h1 class="text-lg font-extrabold tracking-tight sm:text-xl">{{ route.meta.title }}</h1>
        </div>
        <div class="ml-auto flex items-center gap-2">
          <button class="icon-btn relative" aria-label="通知"><Bell :size="18" /></button>
          <div class="relative">
            <button
              class="ml-1 flex cursor-pointer items-center gap-2 rounded-full border bg-white py-1.5 pl-1.5 pr-3"
              @click="profileOpen = !profileOpen"
            >
              <span
                class="grid size-8 place-items-center rounded-full bg-[#f2c79b] text-xs font-black"
                >{{ userName.slice(0, 1) }}</span
              ><span class="hidden text-xs font-bold sm:block">{{ session.roleLabel }}</span
              ><ChevronDown :size="14" /></button
            ><Transition name="fade"
              ><div
                v-if="profileOpen"
                class="absolute right-0 top-12 w-48 rounded-2xl border bg-white p-2 shadow-xl"
              >
                <RouterLink
                  to="/profile"
                  class="flex items-center gap-2 rounded-xl px-3 py-2 text-sm hover:bg-[#f2f4ee]"
                  @click="profileOpen = false"
                  ><UserRound :size="15" />个人资料</RouterLink
                ><button
                  class="flex w-full cursor-pointer items-center gap-2 rounded-xl px-3 py-2 text-sm text-[#c34d3a] hover:bg-[#fff1ed]"
                  @click="logout"
                >
                  <LogOut :size="15" />退出登录
                </button>
              </div></Transition
            >
          </div>
        </div>
      </header>
      <main class="mx-auto max-w-[1560px] p-5 sm:p-8 lg:p-10">
        <RouterView v-slot="{ Component }"
          ><Transition name="page" mode="out-in"><component :is="Component" /></Transition
        ></RouterView>
      </main>
    </div>
  </div>
</template>
