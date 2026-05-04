import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi } from '@/api/user/auth'
import { getUserInfo, updateUserInfo as updateUserInfoApi, updateAvatar as updateAvatarApi } from '@/api/user/user'
import { getToken, setToken, removeToken, getRefreshToken, setRefreshToken, removeRefreshToken } from '@/utils/auth'
import type { LoginRequest, UserDetailVO, UserUpdateRequest } from '@/api/user/types'
import { UserType } from '@/types/enums'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken() || '')
  const refreshToken = ref(getRefreshToken() || '')
  const userInfo = ref<UserDetailVO | null>(null)

  const userType = computed(() => userInfo.value?.type)
  const isStudent = computed(() => userType.value === UserType.Student)
  const isTeacher = computed(() => userType.value === UserType.Teacher)
  const isAdmin = computed(() => userType.value === UserType.Admin)
  const isLoggedIn = computed(() => !!token.value)

  async function login(data: LoginRequest) {
    const res = await loginApi(data)
    token.value = res.accessToken
    refreshToken.value = res.refreshToken
    setToken(res.accessToken)
    setRefreshToken(res.refreshToken)
    await fetchUserInfo()
  }

  async function fetchUserInfo() {
    userInfo.value = await getUserInfo()
  }

  async function updateUserInfo(data: UserUpdateRequest) {
    await updateUserInfoApi(data)
    await fetchUserInfo()
  }

  async function updateAvatar(avatarUrl: string) {
    await updateAvatarApi(avatarUrl)
    await fetchUserInfo()
  }

  async function logout() {
    try {
      await logoutApi()
    } finally {
      token.value = ''
      refreshToken.value = ''
      userInfo.value = null
      removeToken()
      removeRefreshToken()
    }
  }

  function resetState() {
    token.value = ''
    refreshToken.value = ''
    userInfo.value = null
  }

  return {
    token,
    refreshToken,
    userInfo,
    userType,
    isStudent,
    isTeacher,
    isAdmin,
    isLoggedIn,
    login,
    fetchUserInfo,
    updateUserInfo,
    updateAvatar,
    logout,
    resetState
  }
})
