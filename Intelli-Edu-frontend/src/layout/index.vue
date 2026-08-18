<template>
  <div class="app-wrapper" :style="{ '--current-color': theme }">
    <div :class="{ 'fixed-header': fixedHeader }">
      <navbar @setLayout="setLayout" />
    </div>
    <app-main />
  </div>
</template>

<script setup>
import { useWindowSize } from '@vueuse/core'
import { AppMain, Navbar } from './components'
import { useAppStore } from '@/stores/app'
import { useSettingsStore } from '@/stores/settings'

const settingsStore = useSettingsStore()
const theme = computed(() => settingsStore.theme)
const fixedHeader = computed(() => settingsStore.fixedHeader)

function setLayout() {
  console.warn('setLayout: Settings panel not yet implemented')
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  position: relative;
  height: 100%;
  width: 100%;
}

.fixed-header {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 9;
  width: 100%;
  transition: width 0.28s;
}
</style>
