import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const sidebar = ref({
    opened: localStorage.getItem('sidebarStatus') === '1',
    withoutAnimation: false,
    hide: false
  })
  const device = ref<string>('desktop')
  const size = ref<string>(localStorage.getItem('size') || 'default')

  function toggleSideBar(withoutAnimation?: boolean) {
    if (sidebar.value.hide) {
      return false
    }
    sidebar.value.opened = !sidebar.value.opened
    sidebar.value.withoutAnimation = !!withoutAnimation
    if (sidebar.value.opened) {
      localStorage.setItem('sidebarStatus', '1')
    } else {
      localStorage.setItem('sidebarStatus', '0')
    }
  }

  function closeSideBar(withoutAnimation: boolean) {
    localStorage.setItem('sidebarStatus', '0')
    sidebar.value.opened = false
    sidebar.value.withoutAnimation = withoutAnimation
  }

  function toggleDevice(value: string) {
    device.value = value
  }

  function setSize(value: string) {
    size.value = value
    localStorage.setItem('size', value)
  }

  function toggleSideBarHide(status: boolean) {
    sidebar.value.hide = status
  }

  return {
    sidebar,
    device,
    size,
    toggleSideBar,
    closeSideBar,
    toggleDevice,
    setSize,
    toggleSideBarHide
  }
}, {
  persist: {
    pick: ['size'],
  }
})
