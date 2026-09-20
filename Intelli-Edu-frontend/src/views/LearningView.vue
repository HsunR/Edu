<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import {
  BookOpen,
  ChevronDown,
  ExternalLink,
  FileText,
  LoaderCircle,
  UserRound,
} from 'lucide-vue-next'
import { courseApi } from '@/api/services'
import { useUiStore } from '@/stores/ui'
import type { Course, EntityId, ResourceSummary, SectionResource } from '@/types'
const route = useRoute()
const ui = useUiStore()
const course = ref<Course | null>(null)
const loading = ref(true)
const error = ref('')
const openChapters = ref<EntityId[]>([])
const sectionLoading = ref<EntityId | null>(null)
const sections = computed(() => course.value?.chapters?.flatMap((c) => c.sections || []) || [])
async function load() {
  loading.value = true
  error.value = ''
  try {
    const courseId = String(route.params.id || '')
    if (!/^\d+$/.test(courseId)) throw new Error('无效的课程编号')
    course.value = await courseApi.detail(courseId)
    openChapters.value = course.value.chapters?.map((c) => c.chapterId) || []
  } catch (e) {
    error.value = e instanceof Error ? e.message : '课程加载失败'
  } finally {
    loading.value = false
  }
}
function toggle(id: EntityId) {
  openChapters.value = openChapters.value.includes(id)
    ? openChapters.value.filter((x) => x !== id)
    : [...openChapters.value, id]
}
function resourceLabel(r: SectionResource) {
  return `${r.resourceType || '资源'} #${r.resourceId}`
}
function resourceIconLabel(resource: ResourceSummary) {
  return ['', '视频', '文档', '图片'][resource.resourceType] || '资源'
}
async function loadSection(sectionId: EntityId) {
  if (!course.value) return
  const target = sections.value.find((section) => section.sectionId === sectionId)
  if (!target || target.resourceDetails) return
  sectionLoading.value = sectionId
  try {
    const detail = await courseApi.sectionDetail(sectionId)
    target.resourceDetails = detail.resourceDetails || []
  } catch (e) {
    ui.notify(e instanceof Error ? e.message : '小节资源加载失败', 'error')
  } finally {
    sectionLoading.value = null
  }
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
        <h3 class="text-2xl font-black">课程目录</h3>
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
                <button
                  class="flex w-full items-center gap-2 text-left text-sm font-bold"
                  :disabled="sectionLoading === section.sectionId"
                  @click="loadSection(section.sectionId)"
                >
                  <FileText :size="16" class="text-leaf" />{{ section.title
                  }}<span v-if="section.isFree" class="pill ml-auto bg-[#eaf2dd] py-1 text-leaf"
                    >免费</span
                  ><LoaderCircle
                    v-if="sectionLoading === section.sectionId"
                    :size="14"
                    class="ml-auto animate-spin text-[#9ba29d]"
                  /><span v-else-if="!section.isFree" class="ml-auto text-[11px] text-[#8b938d]"
                    >登录后查看</span
                  >
                </button>
                <div v-if="section.resourceDetails?.length" class="mt-3 ml-6 grid gap-2">
                  <component
                    v-for="resource in section.resourceDetails"
                    :key="resource.resourceId"
                    :is="resource.accessUrl ? 'a' : 'span'"
                    :href="resource.accessUrl"
                    :target="resource.accessUrl ? '_blank' : undefined"
                    rel="noopener noreferrer"
                    :class="[
                      'flex items-center gap-2 rounded-xl bg-[#f1f3ee] px-3 py-2 text-xs font-semibold text-[#68716b]',
                      resource.accessUrl ? 'hover:text-ink' : 'cursor-not-allowed opacity-60',
                    ]"
                  >
                    <span class="pill bg-white py-1">{{ resourceIconLabel(resource) }}</span>
                    <span class="min-w-0 flex-1 truncate">{{ resource.resourceName }}</span>
                    <ExternalLink :size="13" />
                  </component>
                </div>
                <div
                  v-else-if="!section.resourceDetails && section.resources?.length"
                  class="mt-3 ml-6 flex flex-wrap gap-2"
                >
                  <span
                    v-for="r in section.resources"
                    :key="r.id"
                    class="pill bg-[#f1f3ee] text-[#68716b]"
                    >{{ resourceLabel(r) }}</span
                  >
                </div>
                <p
                  v-else-if="section.resourceDetails && !section.resourceDetails.length"
                  class="mt-3 ml-6 text-xs text-[#8b938d]"
                >
                  本节暂无可访问资源
                </p>
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
