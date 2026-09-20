<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ArrowRight, BookOpen, ClipboardCheck, Library, Users } from 'lucide-vue-next'
import { courseApi, examApi, resourceApi, userApi } from '@/api/services'
import { useSessionStore } from '@/stores/session'
import type { Course, CourseClass, Exam, ResourceItem } from '@/types'
const session = useSessionStore()
const courses = ref<Course[]>([])
const classes = ref<CourseClass[]>([])
const exams = ref<Exam[]>([])
const resources = ref<ResourceItem[]>([])
const courseTotal = ref(0)
const secondTotal = ref(0)
const examTotal = ref(0)
const resourceTotal = ref(0)
const error = ref('')
const greeting = computed(() => `${session.user?.name || '你好'}，欢迎回来`)
const secondCard = computed(() =>
  session.role === 'Student'
    ? { l: '我的班级', v: classes.value.length, i: Users, to: '/my-classes' }
    : session.role === 'Teacher'
      ? { l: '我的课程', v: secondTotal.value, i: Users, to: '/teaching' }
      : { l: '平台用户', v: secondTotal.value, i: Users, to: '/users' },
)
onMounted(async () => {
  const secondaryRequest =
    session.role === 'Student'
      ? courseApi.mine()
      : session.role === 'Teacher'
        ? courseApi.teaching({ current: 1, pageSize: 1 })
        : userApi.list({ current: 1, pageSize: 1 })
  const results = await Promise.allSettled([
    courseApi.list({ current: 1, pageSize: 3, status: 1 }),
    examApi.list({ current: 1, pageSize: 5 }),
    resourceApi.list({ current: 1, pageSize: 5 }),
    secondaryRequest,
  ])
  const [courseResult, examResult, resourceResult, secondaryResult] = results
  if (courseResult.status === 'fulfilled') {
    courses.value = courseResult.value.records
    courseTotal.value = courseResult.value.total
  }
  if (examResult.status === 'fulfilled') {
    exams.value = examResult.value.records
    examTotal.value = examResult.value.total
  }
  if (resourceResult.status === 'fulfilled') {
    resources.value = resourceResult.value.records
    resourceTotal.value = resourceResult.value.total
  }
  if (secondaryResult.status === 'fulfilled') {
    if (session.role === 'Student') classes.value = secondaryResult.value as CourseClass[]
    else secondTotal.value = (secondaryResult.value as { total: number }).total
  }
  const failed = results.filter((result) => result.status === 'rejected')
  if (failed.length) error.value = `有 ${failed.length} 项数据暂时无法加载，其余内容仍可使用。`
})
</script>
<template>
  <div class="space-y-7">
    <section class="rounded-[34px] bg-ink p-8 text-white sm:p-10">
      <p class="eyebrow text-lime">{{ session.roleLabel }}工作台</p>
      <h2 class="mt-4 text-4xl font-black sm:text-5xl">{{ greeting }}</h2>
      <p class="mt-3 text-sm text-white/55">集中查看你的课程、考试安排与学习资源。</p>
    </section>
    <p v-if="error" class="rounded-2xl bg-red-50 p-4 text-sm text-red-600">{{ error }}</p>
    <section class="grid gap-5 sm:grid-cols-2 xl:grid-cols-4">
      <RouterLink
        v-for="s in [
          { l: '公开课程', v: courseTotal, i: BookOpen, to: '/courses' },
          secondCard,
          { l: '考试与作业', v: examTotal, i: ClipboardCheck, to: '/exams' },
          { l: '我的资源', v: resourceTotal, i: Library, to: '/resources' },
        ]"
        :key="s.l"
        :to="s.to"
        class="card p-6 transition hover:-translate-y-1"
        ><component :is="s.i" :size="21" class="text-leaf" />
        <p class="mt-5 text-3xl font-black">{{ s.v }}</p>
        <div class="mt-1 flex items-center justify-between text-xs text-[#7d857f]">
          {{ s.l }}<ArrowRight :size="14" /></div
      ></RouterLink>
    </section>
    <section class="grid gap-6 xl:grid-cols-2">
      <div class="card p-6">
        <h3 class="text-xl font-black">最新公开课程</h3>
        <div class="mt-5 space-y-3">
          <RouterLink
            v-for="c in courses"
            :key="c.courseId"
            :to="`/courses/${c.courseId}`"
            class="flex items-center gap-3 rounded-2xl border p-4 hover:bg-[#f7f8f4]"
            ><span
              class="grid size-10 place-items-center rounded-xl bg-[#eaf2dd] font-black text-leaf"
              >课</span
            >
            <div class="min-w-0">
              <p class="truncate text-sm font-bold">{{ c.courseName }}</p>
              <p class="text-xs text-[#8b938d]">{{ c.teacherName || '授课教师' }}</p>
            </div>
            <ArrowRight class="ml-auto" :size="15"
          /></RouterLink>
          <p v-if="!courses.length && !error" class="py-8 text-center text-sm text-[#8b938d]">
            暂无课程
          </p>
        </div>
      </div>
      <div class="card p-6">
        <h3 class="text-xl font-black">近期考试与作业</h3>
        <div class="mt-5 space-y-3">
          <div v-for="e in exams" :key="e.examId" class="rounded-2xl border p-4">
            <div class="flex items-center justify-between">
              <b class="text-sm">{{ e.examName }}</b
              ><span class="pill bg-[#f0f2ed]">{{ ['考试', '练习', '作业'][e.examType] }}</span>
            </div>
            <p class="mt-2 text-xs text-[#8b938d]">
              {{ new Date(e.startTime).toLocaleString() }} · {{ e.durationMinutes }} 分钟
            </p>
          </div>
          <p v-if="!exams.length && !error" class="py-8 text-center text-sm text-[#8b938d]">
            暂无考试
          </p>
        </div>
      </div>
    </section>
  </div>
</template>
