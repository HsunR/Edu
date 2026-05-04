import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const sidebar = ref({
    opened: true,
    withoutAnimation: false,
    hide: false
  })
  const device = ref<string>('desktop')
  const size = ref<string>('default')

  function toggleSideBar() {
    if (sidebar.value.hide) {
      return false
    }
    sidebar.value.opened = !sidebar.value.opened
    sidebar.value.withoutAnimation = false
  }

  function closeSideBar(withoutAnimation: boolean) {
    sidebar.value.opened = false
    sidebar.value.withoutAnimation = withoutAnimation
  }

  function toggleDevice(value: string) {
    device.value = value
  }

  function setSize(value: string) {
    size.value = value
  }

  return {
    sidebar,
    device,
    size,
    toggleSideBar,
    closeSideBar,
    toggleDevice,
    setSize
  }
})
