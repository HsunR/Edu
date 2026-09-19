<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import {
  BookOpen,
  ChevronDown,
  ExternalLink,
  FileText,
  LockKeyhole,
  UserRound,
} from 'lucide-vue-next'
import { courseApi } from '@/api/services'
import type { Course, SectionResource } from '@/types'
const route = useRoute()
const course = ref<Course | null>(null)
const loading = ref(true)
const error = ref('')
const openChapters = ref<number[]>([])
const sections = computed(() => course.value?.chapters?.flatMap((c) => c.sections || []) || [])
async function load() {
  loading.value = true
  try {
    course.value = await courseApi.detail(Number(route.params.id))
    openChapters.value = course.value.chapters?.map((c) => c.chapterId) || []
  } catch (e) {
    error.value = e instanceof Error ? e.message : '课程加载失败'
  } finally {
    loading.value = false
  }
}
function toggle(id: number) {
  openChapters.value = openChapters.value.includes(id)
    ? openChapters.value.filter((x) => x !== id)
    : [...openChapters.value, id]
}
function resourceLabel(r: SectionResource) {
  return `${r.resourceType || '资源'} #${r.resourceId}`
}
onMounted(load)
</script>
<template>
  <div v-if="loading" class="card h-96 animate-pulse" />
  <div v-else-if="error" class="card py-20 text-center">
    <p class="font-bold text-red-600">{{ error }}</p>
    <button class="btn-secondary mt-4" @click="load">重新加载</button>
  </div>
  <div v-else-if="course" class="space-y-6">
    <section class="relative overflow-hidden rounded-[34px] bg-ink p-8 text-white sm:p-10">
      <img
        v-if="course.coverUrl"
        :src="course.coverUrl"
        class="absolute inset-0 h-full w-full object-cover opacity-25"
      />
      <div class="relative max-w-3xl">
        <span class="pill bg-white/10 text-lime">{{ course.categoryName || '课程' }}</span>
        <h2 class="mt-5 text-4xl font-black">{{ course.courseName }}</h2>
        <p class="mt-4 max-w-2xl leading-7 text-white/65">
          {{ course.description || '暂无课程简介' }}
        </p>
        <div class="mt-6 flex flex-wrap gap-5 text-sm text-white/70">
          <span class="flex items-center gap-2"
            ><UserRound :size="17" />{{ course.teacherName || '授课教师' }}</span
          ><span class="flex items-center gap-2"
            ><BookOpen :size="17" />{{ course.chapters?.length || 0 }} 章 ·
            {{ sections.length }} 节</span
          >
        </div>
      </div>
    </section>
    <section class="grid gap-6 lg:grid-cols-[1fr_300px]">
      <div class="card p-6 sm:p-8">
        <p class="eyebrow">CURRICULUM</p>
        <h3 class="mt-2 text-2xl font-black">课程目录</h3>
        <div class="mt-6 space-y-3">
          <div
            v-for="(chapter, index) in course.chapters"
            :key="chapter.chapterId"
            class="overflow-hidden rounded-2xl border"
          >
            <button
              class="flex w-full items-center gap-3 bg-[#f8f9f5] p-4 text-left"
              @click="toggle(chapter.chapterId)"
            >
              <span class="grid size-8 place-items-center rounded-xl bg-white text-xs font-black">{{
                String(index + 1).padStart(2, '0')
              }}</span
              ><b>{{ chapter.title }}</b
              ><ChevronDown
                :class="[
                  'ml-auto transition',
                  openChapters.includes(chapter.chapterId) ? 'rotate-180' : '',
                ]"
                :size="17"
              />
            </button>
            <div v-if="openChapters.includes(chapter.chapterId)" class="divide-y">
              <div v-for="section in chapter.sections" :key="section.sectionId" class="p-4">
                <div class="flex items-center gap-2 text-sm font-bold">
                  <FileText :size="16" class="text-leaf" />{{ section.title
                  }}<span v-if="section.isFree" class="pill ml-auto bg-[#eaf2dd] py-1 text-leaf"
                    >免费</span
                  ><LockKeyhole v-else :size="13" class="ml-auto text-[#9ba29d]" />
                </div>
                <div v-if="section.resources?.length" class="mt-3 ml-6 flex flex-wrap gap-2">
                  <span
                    v-for="r in section.resources"
                    :key="r.id"
                    class="pill bg-[#f1f3ee] text-[#68716b]"
                    >{{ resourceLabel(r) }}</span
                  >
                </div>
              </div>
              <p v-if="!chapter.sections?.length" class="p-5 text-sm text-[#8b938d]">
                本章暂无小节
              </p>
            </div>
          </div>
          <p v-if="!course.chapters?.length" class="py-10 text-center text-sm text-[#8b938d]">
            课程尚未配置目录
          </p>
        </div>
      </div>
      <aside class="card h-fit p-6">
        <h3 class="font-black">课程状态</h3>
        <dl class="mt-5 space-y-4 text-sm">
          <div class="flex justify-between">
            <dt class="text-[#8b938d]">状态</dt>
            <dd class="font-bold">{{ ['草稿', '已发布', '已归档'][course.status] }}</dd>
          </div>
          <div class="flex justify-between">
            <dt class="text-[#8b938d]">可见性</dt>
            <dd class="font-bold">{{ course.isPublic ? '公开' : '班级可见' }}</dd>
          </div>
          <div class="flex justify-between">
            <dt class="text-[#8b938d]">创建时间</dt>
            <dd class="font-bold">{{ new Date(course.createdAt).toLocaleDateString() }}</dd>
          </div>
        </dl>
        <RouterLink to="/resources" class="btn-secondary mt-6 w-full py-2.5"
          >查看我的资源 <ExternalLink :size="14"
        /></RouterLink>
      </aside>
    </section>
  </div>
</template>
