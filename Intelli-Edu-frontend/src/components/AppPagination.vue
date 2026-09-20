<script setup lang="ts">
import { ChevronLeft, ChevronRight } from 'lucide-vue-next'

const props = withDefaults(
  defineProps<{ page: number; pageSize: number; total: number; disabled?: boolean }>(),
  { disabled: false },
)
const emit = defineEmits<{ change: [page: number] }>()
</script>

<template>
  <nav
    v-if="page > 1 || total > pageSize"
    class="mt-7 flex items-center justify-center gap-3"
    aria-label="分页"
  >
    <button
      class="icon-btn"
      :disabled="disabled || page <= 1"
      aria-label="上一页"
      @click="emit('change', page - 1)"
    >
      <ChevronLeft :size="17" />
    </button>
    <span class="text-sm font-bold"
      >第 {{ page }} / {{ Math.max(page, Math.ceil(total / pageSize)) }} 页</span
    >
    <button
      class="icon-btn"
      :disabled="disabled || page >= Math.ceil(total / pageSize)"
      aria-label="下一页"
      @click="emit('change', page + 1)"
    >
      <ChevronRight :size="17" />
    </button>
  </nav>
</template>
