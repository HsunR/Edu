<script setup lang="ts">
import BrandMark from '@/components/BrandMark.vue'
</script>
<template>
  <div class="grid min-h-screen place-items-center bg-canvas p-6">
    <div class="text-center">
      <BrandMark class="justify-center" />
      <p class="mt-12 text-8xl font-black tracking-[-.08em] text-[#dce2d8]">404</p>
      <h1 class="mt-4 text-2xl font-black">这节课还没准备好</h1>
      <p class="mt-2 text-sm text-[#7b847e]">你访问的页面不存在，回到学习空间继续探索吧。</p>
      <RouterLink to="/" class="btn-primary mt-6">返回首页</RouterLink>
    </div>
  </div>
</template>
