<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { AlertTriangle, BarChart3, CheckCircle2, Target } from 'lucide-vue-next'
import { courseApi, learningApi } from '@/api/services'
import { useSessionStore } from '@/stores/session'
import type { Course, CourseClass, EntityId, MasteryPoint, WrongRecord } from '@/types'
const session = useSessionStore()
const classes = ref<CourseClass[]>([])
const classId = ref<EntityId>()
const mastery = ref<MasteryPoint[]>([])
const weak = ref<MasteryPoint[]>([])
const wrongs = ref<WrongRecord[]>([])
const loading = ref(false)
const error = ref('')
const level = (p: MasteryPoint) => Number(p.masteryLevel ?? p.avgMasteryLevel ?? 0)
const selected = computed(() => classes.value.find((c) => c.classId === classId.value))
const average = computed(() =>
  mastery.value.length
    ? Math.round(mastery.value.reduce((s, p) => s + level(p), 0) / mastery.value.length)
    : 0,
)
async function load() {
  if (!classId.value) return
  loading.value = true
  error.value = ''
  try {
    if (session.role === 'Student') {
      const [a, b, c] = await Promise.all([
        learningApi.mastery(classId.value),
        learningApi.weakPoints(classId.value),
        learningApi.wrongs({ current: 1, pageSize: 20, classId: classId.value }),
      ])
      mastery.value = a
      weak.value = b
      wrongs.value = c.records
    } else {
      mastery.value = await learningApi.teacherMastery(classId.value)
      weak.value = []
      wrongs.value = []
    }
  } catch (e) {
    error.value = e instanceof Error ? e.message : '学情加载失败'
  } finally {
    loading.value = false
  }
}
onMounted(async () => {
  try {
    if (session.role === 'Student') classes.value = await courseApi.mine()
    else {
      const teaching: Course[] = []
      let current = 1
      while (current <= 20) {
        const page = await courseApi.teaching({ current, pageSize: 50 })
        teaching.push(...page.records)
        if (page.records.length < 50) break
        current++
      }
      classes.value = (await Promise.all(teaching.map((c) => courseApi.classes(c.courseId)))).flat()
    }
    classId.value = classes.value[0]?.classId
  } catch (e) {
    error.value = e instanceof Error ? e.message : '班级加载失败'
  }
})
watch(classId, load)
async function resolve(item: WrongRecord) {
  try {
    await learningApi.resolveWrong(item.wrongId)
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '操作失败'
  }
}
</script>
<template>
  <div>
    <div class="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
      <div>
        <h2 class="text-3xl font-black">
          {{ session.role === 'Teacher' ? '班级学情' : '我的学情' }}
        </h2>
        <p class="mt-2 text-sm text-[#7d857f]">跟踪知识点掌握情况，及时处理薄弱项与错题。</p>
      </div>
      <select v-model="classId" class="field sm:w-72">
        <option v-for="c in classes" :key="c.classId" :value="c.classId">
          {{ c.courseName }} · {{ c.className }}
        </option>
      </select>
    </div>
    <p v-if="error" class="mt-5 rounded-2xl bg-red-50 p-4 text-sm text-red-600">{{ error }}</p>
    <div v-if="!classes.length && !error" class="card mt-6 py-20 text-center">
      <p class="font-bold">没有可查看的班级</p>
    </div>
    <template v-else
      ><section class="mt-6 grid gap-5 sm:grid-cols-3">
        <div class="card p-6">
          <Target :size="21" class="text-leaf" />
          <p class="mt-5 text-3xl font-black">{{ average }}%</p>
          <p class="text-xs text-[#8b938d]">平均掌握度</p>
        </div>
        <div class="card p-6">
          <BarChart3 :size="21" class="text-violet-600" />
          <p class="mt-5 text-3xl font-black">{{ mastery.length }}</p>
          <p class="text-xs text-[#8b938d]">已统计知识点</p>
        </div>
        <div class="card p-6">
          <AlertTriangle :size="21" class="text-orange-600" />
          <p class="mt-5 text-3xl font-black">
            {{
              session.role === 'Student'
                ? wrongs.length
                : mastery.reduce((s, p) => s + (p.weakStudentCount || 0), 0)
            }}
          </p>
          <p class="text-xs text-[#8b938d]">
            {{ session.role === 'Student' ? '当前页错题' : '薄弱学生人次' }}
          </p>
        </div>
      </section>
      <section class="card mt-6 p-6 sm:p-8">
        <h3 class="text-xl font-black">{{ selected?.courseName }} · 掌握度</h3>
        <div v-if="loading" class="py-16 text-center text-sm text-[#8b938d]">正在加载…</div>
        <div v-else class="mt-6 space-y-5">
          <div v-for="p in mastery" :key="p.pointId">
            <div class="mb-2 flex justify-between text-xs">
              <b>{{ p.pointName }}</b
              ><span>{{ level(p) }}%</span>
            </div>
            <div class="h-2 rounded-full bg-[#edf0ea]">
              <div
                class="h-full rounded-full bg-gradient-to-r from-leaf to-[#91b95b]"
                :style="{ width: `${Math.min(100, level(p))}%` }"
              />
            </div>
          </div>
          <p v-if="!mastery.length" class="py-12 text-center text-sm text-[#8b938d]">
            暂无掌握度记录
          </p>
        </div>
      </section>
      <section v-if="session.role === 'Student'" class="card mt-6 p-6 sm:p-8">
        <h3 class="text-xl font-black">错题记录</h3>
        <div class="mt-5 space-y-3">
          <div
            v-for="w in wrongs"
            :key="w.wrongId"
            class="flex items-center gap-4 rounded-2xl border p-4"
          >
            <AlertTriangle :size="18" class="text-orange-600" />
            <div class="flex-1">
              <b class="text-sm">题目 #{{ w.questionId }}</b>
              <p class="mt-1 text-xs text-[#8b938d]">
                错误 {{ w.wrongCount }} 次 · 得分 {{ w.earnedScore }} / {{ w.fullScore }}
              </p>
            </div>
            <button v-if="!w.isResolved" class="btn-secondary py-2" @click="resolve(w)">
              <CheckCircle2 :size="14" />标记解决</button
            ><span v-else class="pill bg-[#eef4e5] text-leaf">已解决</span>
          </div>
          <p v-if="!wrongs.length" class="py-8 text-center text-sm text-[#8b938d]">暂无错题记录</p>
        </div>
      </section></template
    >
  </div>
</template>
