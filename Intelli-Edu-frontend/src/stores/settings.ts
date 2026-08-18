import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useDark, useToggle } from '@vueuse/core'
import defaultSettings from '@/settings'

const isDark = useDark()
const toggleDark = useToggle(isDark)

export const useSettingsStore = defineStore('settings', () => {
  const theme = ref(defaultSettings.theme || '#409EFF')
  const sideTheme = ref(defaultSettings.sideTheme || 'theme-dark')
  const showSettings = ref(defaultSettings.showSettings)
  const topNav = ref(defaultSettings.topNav)
  const tagsView = ref(defaultSettings.tagsView)
  const tagsIcon = ref(defaultSettings.tagsIcon)
  const fixedHeader = ref(defaultSettings.fixedHeader)
  const sidebarLogo = ref(defaultSettings.sidebarLogo)
  const dynamicTitle = ref(defaultSettings.dynamicTitle)
  const footerVisible = ref(defaultSettings.footerVisible)
  const footerContent = ref(defaultSettings.footerContent)
  const isDarkMode = ref(isDark.value)

  function changeSetting(key: string, value: unknown) {
    switch (key) {
      case 'theme': theme.value = value as string; break
      case 'sideTheme': sideTheme.value = value as string; break
      case 'topNav': topNav.value = value as boolean; break
      case 'tagsView': tagsView.value = value as boolean; break
      case 'tagsIcon': tagsIcon.value = value as boolean; break
      case 'fixedHeader': fixedHeader.value = value as boolean; break
      case 'sidebarLogo': sidebarLogo.value = value as boolean; break
      case 'dynamicTitle': dynamicTitle.value = value as boolean; break
      case 'footerVisible': footerVisible.value = value as boolean; break
    }
  }

  function toggleTheme() {
    isDarkMode.value = !isDarkMode.value
    toggleDark()
  }

  return {
    theme, sideTheme, showSettings, topNav, tagsView, tagsIcon,
    fixedHeader, sidebarLogo, dynamicTitle, footerVisible, footerContent,
    isDarkMode,
    changeSetting, toggleTheme
  }
}, {
  persist: {
    key: 'layout-setting',
    paths: ['theme', 'sideTheme', 'topNav', 'tagsView', 'tagsIcon',
            'fixedHeader', 'sidebarLogo', 'dynamicTitle', 'footerVisible']
  }
})
