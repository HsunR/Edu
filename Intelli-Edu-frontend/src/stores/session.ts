import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { authApi, userApi } from '@/api/services'
import { ApiError } from '@/api/client'
import type { LoginResult, User, UserRole } from '@/types'

export const useSessionStore = defineStore('session', () => {
  const user = ref<User | null>(null)
  const initialized = ref(false)
  const sidebarOpen = ref(false)
  const accessToken = ref(localStorage.getItem('intelli-access-token') || '')
  const role = computed<UserRole | null>(() => user.value?.userType || null)
  const roleLabel = computed(
    () => ({ Student: '学生', Teacher: '教师', Admin: '管理员' })[role.value || 'Student'],
  )
  const loggedIn = computed(() => Boolean(accessToken.value))

  function saveTokens(result: LoginResult) {
    accessToken.value = result.accessToken
    localStorage.setItem('intelli-access-token', result.accessToken)
    localStorage.setItem('intelli-refresh-token', result.refreshToken)
  }
  async function login(data: Parameters<typeof authApi.login>[0]) {
    const result = await authApi.login(data)
    saveTokens(result)
    await loadUser()
  }
  async function loadUser() {
    if (accessToken.value) user.value = await userApi.me()
  }
  async function initialize() {
    if (initialized.value) return
    try {
      await loadUser()
    } catch (error) {
      if (error instanceof ApiError && error.code === 40100) clear()
    } finally {
      initialized.value = true
    }
  }
  function clear() {
    accessToken.value = ''
    user.value = null
    localStorage.removeItem('intelli-access-token')
    localStorage.removeItem('intelli-refresh-token')
  }
  async function logout() {
    try {
      if (accessToken.value) await authApi.logout()
    } catch {
      // 后端暂时不可用时也应允许用户清除本地会话。
    } finally {
      clear()
    }
  }
  window.addEventListener('auth-expired', clear)
  window.addEventListener('auth-token-refreshed', (event) => {
    accessToken.value = (event as CustomEvent<string>).detail
  })
  window.addEventListener('storage', (event) => {
    if (event.key === 'intelli-access-token') {
      accessToken.value = event.newValue || ''
      if (!event.newValue) user.value = null
    }
  })
  return {
    user,
    role,
    roleLabel,
    loggedIn,
    initialized,
    sidebarOpen,
    login,
    loadUser,
    initialize,
    logout,
    clear,
  }
})
