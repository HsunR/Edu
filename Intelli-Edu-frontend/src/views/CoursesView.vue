<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { ChevronLeft, ChevronRight, Search } from 'lucide-vue-next'
import { courseApi } from '@/api/services'
import type { Category, Course, EntityId } from '@/types'
import CourseCard from '@/components/CourseCard.vue'
const courses = ref<Course[]>([])
const categories = ref<Category[]>([])
const total = ref(0)
const page = ref(1)
const query = ref('')
const categoryId = ref<EntityId | undefined>()
const loading = ref(true)
const error = ref('')
async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await courseApi.list({
      current: page.value,
      pageSize: 12,
      courseName: query.value || undefined,
      categoryId: categoryId.value,
      status: 1,
    })
    courses.value = result.records
    total.value = result.total
  } catch (e) {
    error.value = e instanceof Error ? e.message : '加载失败'
  } finally {
    loading.value = false
  }
}

function search() {
  page.value = 1
  load()
}

function previousPage() {
  page.value--
  load()
}

function nextPage() {
  page.value++
  load()
}

onMounted(async () => {
  try {
    categories.value = await courseApi.categories()
  } catch {}
  await load()
})
watch(categoryId, () => {
  page.value = 1
  load()
})
</script>
<template>
  <div>
    <section
      class="flex flex-col gap-5 rounded-[32px] border bg-[#e9efdf] p-6 sm:p-8 lg:flex-row lg:items-end lg:justify-between"
    >
      <div>
        <p class="eyebrow text-leaf">课程广场</p>
        <h2 class="mt-3 text-3xl font-black sm:text-4xl">发现适合你的课程</h2>
        <p class="mt-3 text-sm text-[#657068]">发现公开课程，了解课程内容与授课教师。</p>
      </div>
      <form class="relative w-full lg:w-80" @submit.prevent="search">
        <Search class="absolute left-4 top-1/2 -translate-y-1/2 text-[#89918c]" :size="18" /><input
          v-model="query"
          class="field pl-11"
          placeholder="搜索课程名称…"
        />
      </form>
    </section>
    <div class="mt-7 flex flex-wrap gap-2">
      <button
        :class="[
          'rounded-full px-4 py-2 text-xs font-bold',
          !categoryId ? 'bg-ink text-white' : 'border bg-white',
        ]"
        @click="categoryId = undefined"
      >
        全部</button
      ><button
        v-for="c in categories"
        :key="c.categoryId"
        :class="[
          'rounded-full px-4 py-2 text-xs font-bold',
          categoryId === c.categoryId ? 'bg-ink text-white' : 'border bg-white',
        ]"
        @click="categoryId = c.categoryId"
      >
        {{ c.categoryName }}</button
      ><span class="ml-auto text-xs text-[#89918c]">共 {{ total }} 门</span>
    </div>
    <div v-if="loading" class="grid grid-cols-1 gap-6 pt-6 sm:grid-cols-2 xl:grid-cols-3">
      <div v-for="i in 6" :key="i" class="h-80 animate-pulse rounded-[26px] bg-white" />
    </div>
    <div v-else-if="error" class="card mt-6 py-16 text-center">
      <p class="font-bold text-red-600">{{ error }}</p>
      <button class="btn-secondary mt-4" @click="load">重新加载</button>
    </div>
    <div v-else-if="courses.length" class="mt-6 grid gap-6 sm:grid-cols-2 xl:grid-cols-3">
      <CourseCard v-for="course in courses" :key="course.courseId" :course="course" />
    </div>
    <div v-else class="card mt-6 py-20 text-center">
      <p class="font-bold">暂无符合条件的公开课程</p>
    </div>
    <div v-if="total > 12" class="mt-8 flex justify-center gap-2">
      <button class="icon-btn" :disabled="page <= 1" @click="previousPage">
        <ChevronLeft :size="17" /></button
      ><span class="grid h-10 place-items-center px-3 text-sm font-bold">第 {{ page }} 页</span
      ><button class="icon-btn" :disabled="page * 12 >= total" @click="nextPage">
        <ChevronRight :size="17" />
      </button>
    </div>
  </div>
</template>
