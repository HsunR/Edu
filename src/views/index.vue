<template>
  <div class="app-container home">
    首页
  </div>
</template>

<script setup name="Index">
import useUserStore from '@/store/modules/user'
import { onMounted } from 'vue'

const userStore = useUserStore()
onMounted(() => {
  console.log("shouye")
  userStore.getInfo()
})
</script>

<style scoped lang="scss">

</style>

