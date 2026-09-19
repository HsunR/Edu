<script setup lang="ts">
import { ArrowUpRight, BookOpen, UserRound } from 'lucide-vue-next'
import type { Course } from '@/types'
defineProps<{ course: Course }>()
const palettes = [
  'from-violet-500 to-indigo-600',
  'from-orange-400 to-rose-500',
  'from-emerald-400 to-teal-600',
  'from-sky-400 to-blue-600',
]
function palette(id: number) {
  return palettes[id % palettes.length]
}
</script>
<template>
  <article
    class="group overflow-hidden rounded-[26px] border bg-white transition duration-300 hover:-translate-y-1 hover:shadow-xl hover:shadow-ink/8"
  >
    <div
      :class="[
        'relative h-40 overflow-hidden bg-gradient-to-br p-5 text-white',
        palette(course.courseId),
      ]"
    >
      <img
        v-if="course.coverUrl"
        :src="course.coverUrl"
        :alt="course.courseName"
        class="absolute inset-0 h-full w-full object-cover"
      />
      <div class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent" />
      <span
        class="relative rounded-full bg-white/18 px-3 py-1.5 text-[10px] font-bold backdrop-blur"
        >{{ course.categoryName || '未分类' }}</span
      >
      <h3 class="absolute bottom-5 left-5 right-5 text-xl font-bold">{{ course.courseName }}</h3>
    </div>
    <div class="p-5">
      <p class="line-clamp-2 h-10 text-sm leading-5 text-[#747d77]">
        {{ course.description || '暂无课程简介' }}
      </p>
      <div class="mt-4 flex items-center gap-4 text-xs text-[#7e8781]">
        <span class="inline-flex items-center gap-1.5"
          ><UserRound :size="14" />{{ course.teacherName || '授课教师' }}</span
        ><span class="inline-flex items-center gap-1.5"
          ><BookOpen :size="14" />{{ course.isPublic ? '公开' : '班级可见' }}</span
        >
      </div>
      <div class="mt-4 flex items-center border-t pt-4">
        <span class="pill bg-[#eef4e5] text-leaf">已发布</span
        ><RouterLink
          :to="`/courses/${course.courseId}`"
          class="ml-auto grid size-9 place-items-center rounded-full bg-ink text-white transition group-hover:rotate-45 group-hover:bg-leaf"
          ><ArrowUpRight :size="16"
        /></RouterLink>
      </div>
    </div>
  </article>
</template>
