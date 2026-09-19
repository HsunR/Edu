<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Archive, CirclePlus, MoreHorizontal, Send } from 'lucide-vue-next'
import { courseApi } from '@/api/services'
import type { Course } from '@/types'
const courses = ref<Course[]>([])
const loading = ref(true)
const error = ref('')
const showCreate = ref(false)
const saving = ref(false)
const form = ref({
  courseName: '',
  description: '',
  coverUrl: '',
  categoryId: undefined as number | undefined,
  isPublic: 1,
})
async function load() {
  loading.value = true
  error.value = ''
  try {
    courses.value = (await courseApi.teaching({ current: 1, pageSize: 50 })).records
  } catch (e) {
    error.value = e instanceof Error ? e.message : '加载失败'
  } finally {
    loading.value = false
  }
}
async function create() {
  saving.value = true
  try {
    await courseApi.create(form.value)
    showCreate.value = false
    form.value = {
      courseName: '',
      description: '',
      coverUrl: '',
      categoryId: undefined,
      isPublic: 1,
    }
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '创建失败'
  } finally {
    saving.value = false
  }
}
async function publish(course: Course) {
  try {
    await courseApi.publish(course.courseId)
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '发布失败'
  }
}
async function archive(course: Course) {
  try {
    await courseApi.archive(course.courseId)
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '归档失败'
  }
}
onMounted(load)
</script>
<template>
  <div>
    <section
      class="flex flex-col gap-5 rounded-[32px] bg-ink p-7 text-white sm:flex-row sm:items-center sm:justify-between sm:p-9"
    >
      <div>
        <p class="eyebrow text-lime">TEACHING COURSES</p>
        <h2 class="mt-3 text-3xl font-black">我教的课程</h2>
        <p class="mt-2 text-sm text-white/50">创建、发布和归档课程。</p>
      </div>
      <button class="btn-primary bg-lime text-ink hover:bg-white" @click="showCreate = true">
        <CirclePlus :size="17" />创建课程
      </button>
    </section>
    <p v-if="error" class="mt-5 rounded-2xl bg-red-50 p-4 text-sm text-red-600">{{ error }}</p>
    <div v-if="loading" class="mt-6 grid gap-5 md:grid-cols-2 xl:grid-cols-3">
      <div v-for="i in 3" :key="i" class="h-64 animate-pulse rounded-[26px] bg-white" />
    </div>
    <div v-else-if="courses.length" class="mt-6 grid gap-5 md:grid-cols-2 xl:grid-cols-3">
      <article v-for="course in courses" :key="course.courseId" class="card overflow-hidden">
        <div class="relative h-32 bg-gradient-to-br from-violet-500 to-indigo-600 p-5 text-white">
          <img
            v-if="course.coverUrl"
            :src="course.coverUrl"
            class="absolute inset-0 h-full w-full object-cover opacity-60"
          />
          <div class="relative flex justify-between">
            <span class="pill bg-white/15">{{ ['草稿', '已发布', '已归档'][course.status] }}</span
            ><MoreHorizontal :size="18" />
          </div>
          <h3 class="absolute bottom-5 left-5 font-black">{{ course.courseName }}</h3>
        </div>
        <div class="p-5">
          <p class="line-clamp-2 h-10 text-sm text-[#747d77]">
            {{ course.description || '暂无简介' }}
          </p>
          <div class="mt-5 flex gap-2">
            <RouterLink :to="`/courses/${course.courseId}`" class="btn-secondary flex-1 py-2.5"
              >查看</RouterLink
            ><button
              v-if="course.status === 0"
              class="icon-btn"
              title="发布"
              @click="publish(course)"
            >
              <Send :size="16" /></button
            ><button
              v-if="course.status === 1"
              class="icon-btn"
              title="归档"
              @click="archive(course)"
            >
              <Archive :size="16" />
            </button>
          </div>
        </div>
      </article>
    </div>
    <div v-else class="card mt-6 py-20 text-center"><p class="font-bold">还没有创建课程</p></div>
    <Transition name="fade"
      ><div
        v-if="showCreate"
        class="fixed inset-0 z-50 grid place-items-center bg-ink/35 p-5 backdrop-blur-sm"
        @click.self="showCreate = false"
      >
        <form class="card w-full max-w-lg p-7" @submit.prevent="create">
          <h3 class="text-xl font-black">创建课程</h3>
          <div class="mt-5 space-y-4">
            <input
              v-model="form.courseName"
              class="field"
              placeholder="课程名称"
              maxlength="50"
              required
            /><textarea
              v-model="form.description"
              class="field min-h-24"
              placeholder="课程简介"
            /><input v-model="form.coverUrl" class="field" placeholder="封面 URL（可选）" /><input
              v-model.number="form.categoryId"
              type="number"
              class="field"
              placeholder="分类 ID（可选）"
            /><label class="flex items-center gap-2 text-sm"
              ><input
                v-model="form.isPublic"
                type="checkbox"
                :true-value="1"
                :false-value="0"
              />允许公开浏览目录</label
            >
          </div>
          <div class="mt-5 flex justify-end gap-2">
            <button type="button" class="btn-secondary" @click="showCreate = false">取消</button
            ><button class="btn-primary" :disabled="saving">
              {{ saving ? '创建中…' : '创建' }}
            </button>
          </div>
        </form>
      </div></Transition
    >
  </div>
</template>
