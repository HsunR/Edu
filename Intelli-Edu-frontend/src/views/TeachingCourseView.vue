<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Clipboard, Pencil, Plus, Trash2, UserMinus, Users } from 'lucide-vue-next'
import { courseApi } from '@/api/services'
import { useUiStore } from '@/stores/ui'
import type { Category, ClassMember, Course, CourseClass, EntityId } from '@/types'
import AppPagination from '@/components/AppPagination.vue'

const route = useRoute()
const router = useRouter()
const ui = useUiStore()
const course = ref<Course | null>(null)
const categories = ref<Category[]>([])
const classes = ref<CourseClass[]>([])
const members = ref<Record<string, ClassMember[]>>({})
const memberPages = ref<Record<string, number>>({})
const memberTotals = ref<Record<string, number>>({})
const memberPageSize = 12
const expandedClassId = ref<EntityId | null>(null)
const loading = ref(true)
const saving = ref(false)
const error = ref('')
const editMode = ref(false)
const showClassForm = ref(false)
const courseForm = ref({
  courseName: '',
  description: '',
  coverUrl: '',
  categoryId: undefined as EntityId | undefined,
  isPublic: 1,
})
const classForm = ref({ className: '', maxStudents: 100, startDate: '', endDate: '' })
const canDelete = computed(() => course.value?.status === 0)

async function load() {
  loading.value = true
  error.value = ''
  try {
    const courseId = String(route.params.id || '')
    if (!/^\d+$/.test(courseId)) throw new Error('无效的课程编号')
    const [detail, classList, categoryList] = await Promise.all([
      courseApi.detail(courseId),
      courseApi.classes(courseId),
      courseApi.categories().catch(() => []),
    ])
    course.value = detail
    classes.value = classList
    categories.value = categoryList
    courseForm.value = {
      courseName: detail.courseName,
      description: detail.description || '',
      coverUrl: detail.coverUrl || '',
      categoryId: detail.categoryId,
      isPublic: detail.isPublic,
    }
  } catch (e) {
    error.value = e instanceof Error ? e.message : '课程加载失败'
  } finally {
    loading.value = false
  }
}

async function saveCourse() {
  if (!course.value) return
  saving.value = true
  try {
    course.value = await courseApi.update(course.value.courseId, courseForm.value)
    editMode.value = false
    ui.notify('课程信息已更新', 'success')
  } catch (e) {
    ui.notify(e instanceof Error ? e.message : '更新失败', 'error')
  } finally {
    saving.value = false
  }
}

async function createClass() {
  if (!course.value) return
  if (classForm.value.endDate && classForm.value.startDate > classForm.value.endDate) {
    ui.notify('结课日期不能早于开课日期', 'error')
    return
  }
  saving.value = true
  try {
    await courseApi.createClass({ courseId: course.value.courseId, ...classForm.value })
    classes.value = await courseApi.classes(course.value.courseId)
    classForm.value = { className: '', maxStudents: 100, startDate: '', endDate: '' }
    showClassForm.value = false
    ui.notify('班级已创建', 'success')
  } catch (e) {
    ui.notify(e instanceof Error ? e.message : '班级创建失败', 'error')
  } finally {
    saving.value = false
  }
}

async function toggleMembers(item: CourseClass) {
  if (expandedClassId.value === item.classId) {
    expandedClassId.value = null
    return
  }
  expandedClassId.value = item.classId
  await loadMembers(item, memberPages.value[String(item.classId)] || 1)
}

async function loadMembers(item: CourseClass, page: number) {
  try {
    const result = await courseApi.members(item.classId, {
      current: page,
      pageSize: memberPageSize,
    })
    const key = String(item.classId)
    members.value[key] = result.records
    memberPages.value[key] = page
    memberTotals.value[key] = result.total
  } catch (e) {
    ui.notify(e instanceof Error ? e.message : '成员加载失败', 'error')
  }
}

async function removeMember(item: CourseClass, member: ClassMember) {
  const confirmed = await ui.confirm({
    title: '移除班级成员',
    message: `确定将“${member.studentName}”移出“${item.className}”吗？`,
    confirmLabel: '移除',
    danger: true,
  })
  if (!confirmed) return
  try {
    await courseApi.removeMember(item.classId, member.id)
    const key = String(item.classId)
    members.value[key] = (members.value[key] || []).filter((entry) => entry.id !== member.id)
    memberTotals.value[key] = Math.max(0, (memberTotals.value[key] || 1) - 1)
    item.currentStudents = Math.max(0, item.currentStudents - 1)
    ui.notify('成员已移除', 'success')
  } catch (e) {
    ui.notify(e instanceof Error ? e.message : '移除失败', 'error')
  }
}

async function copyInviteCode(code?: string) {
  if (!code) return
  try {
    await navigator.clipboard.writeText(code)
    ui.notify('邀请码已复制', 'success')
  } catch {
    ui.notify(`邀请码：${code}`, 'info')
  }
}

async function removeCourse() {
  if (!course.value) return
  const confirmed = await ui.confirm({
    title: '删除草稿课程',
    message: `确定永久删除“${course.value.courseName}”吗？此操作无法撤销。`,
    confirmLabel: '删除课程',
    danger: true,
  })
  if (!confirmed) return
  try {
    await courseApi.remove(course.value.courseId)
    ui.notify('课程已删除', 'success')
    await router.replace('/teaching')
  } catch (e) {
    ui.notify(e instanceof Error ? e.message : '删除失败', 'error')
  }
}

onMounted(load)
</script>

<template>
  <div>
    <button class="btn-secondary mb-5 py-2.5" @click="router.push('/teaching')">
      <ArrowLeft :size="16" />返回课程管理
    </button>
    <div v-if="loading" class="card h-96 animate-pulse" />
    <div v-else-if="error" class="card py-20 text-center">
      <p class="font-bold text-red-600">{{ error }}</p>
      <button class="btn-secondary mt-4" @click="load">重新加载</button>
    </div>
    <template v-else-if="course">
      <section class="card p-6 sm:p-8">
        <div class="flex flex-wrap items-start gap-4">
          <div class="min-w-0 flex-1">
            <p class="eyebrow">课程信息</p>
            <h2 class="mt-2 text-3xl font-black">{{ course.courseName }}</h2>
            <p class="mt-2 text-sm text-[#7d857f]">
              {{ ['草稿', '已发布', '已归档'][course.status] }}
            </p>
          </div>
          <button v-if="!editMode" class="btn-secondary" @click="editMode = true">
            <Pencil :size="16" />编辑信息
          </button>
          <button
            v-if="canDelete"
            class="icon-btn text-red-600"
            title="删除草稿课程"
            @click="removeCourse"
          >
            <Trash2 :size="17" />
          </button>
        </div>
        <form v-if="editMode" class="mt-7 grid gap-4 sm:grid-cols-2" @submit.prevent="saveCourse">
          <label class="sm:col-span-2"
            ><span class="mb-2 block text-xs font-bold">课程名称</span
            ><input v-model="courseForm.courseName" class="field" required maxlength="50"
          /></label>
          <label class="sm:col-span-2"
            ><span class="mb-2 block text-xs font-bold">课程简介</span
            ><textarea v-model="courseForm.description" class="field min-h-28" />
          </label>
          <label
            ><span class="mb-2 block text-xs font-bold">课程分类</span
            ><select v-model="courseForm.categoryId" class="field">
              <option :value="undefined">未分类</option>
              <option
                v-for="category in categories"
                :key="category.categoryId"
                :value="category.categoryId"
              >
                {{ category.categoryName }}
              </option>
            </select></label
          >
          <label
            ><span class="mb-2 block text-xs font-bold">封面 URL</span
            ><input v-model="courseForm.coverUrl" type="url" class="field" placeholder="https://…"
          /></label>
          <label class="flex items-center gap-2 text-sm"
            ><input
              v-model="courseForm.isPublic"
              type="checkbox"
              :true-value="1"
              :false-value="0"
            />允许公开浏览目录</label
          >
          <div class="flex justify-end gap-2 sm:col-span-2">
            <button type="button" class="btn-secondary" @click="editMode = false">取消</button
            ><button class="btn-primary" :disabled="saving">
              {{ saving ? '保存中…' : '保存' }}
            </button>
          </div>
        </form>
        <p v-else class="mt-6 max-w-3xl text-sm leading-7 text-[#68716b]">
          {{ course.description || '暂无课程简介' }}
        </p>
      </section>

      <section class="mt-6">
        <div class="flex flex-wrap items-end justify-between gap-3">
          <div>
            <h3 class="text-2xl font-black">班级管理</h3>
            <p class="mt-1 text-sm text-[#7d857f]">创建班级、复制邀请码并维护成员。</p>
          </div>
          <button class="btn-primary" @click="showClassForm = !showClassForm">
            <Plus :size="16" />创建班级
          </button>
        </div>
        <form
          v-if="showClassForm"
          class="card mt-5 grid gap-4 p-6 sm:grid-cols-2 lg:grid-cols-4"
          @submit.prevent="createClass"
        >
          <label class="sm:col-span-2"
            ><span class="mb-2 block text-xs font-bold">班级名称</span
            ><input v-model="classForm.className" class="field" maxlength="50" required
          /></label>
          <label
            ><span class="mb-2 block text-xs font-bold">人数上限</span
            ><input v-model.number="classForm.maxStudents" type="number" min="1" class="field"
          /></label>
          <span />
          <label
            ><span class="mb-2 block text-xs font-bold">开课日期</span
            ><input v-model="classForm.startDate" type="date" class="field"
          /></label>
          <label
            ><span class="mb-2 block text-xs font-bold">结课日期</span
            ><input v-model="classForm.endDate" type="date" class="field"
          /></label>
          <div class="flex items-end justify-end gap-2 sm:col-span-2">
            <button type="button" class="btn-secondary" @click="showClassForm = false">取消</button
            ><button class="btn-primary" :disabled="saving">
              {{ saving ? '创建中…' : '确认创建' }}
            </button>
          </div>
        </form>

        <div class="mt-5 space-y-4">
          <article v-for="item in classes" :key="item.classId" class="card overflow-hidden">
            <div class="flex flex-col gap-4 p-6 md:flex-row md:items-center">
              <div class="min-w-0 flex-1">
                <div class="flex flex-wrap items-center gap-2">
                  <h4 class="font-black">{{ item.className }}</h4>
                  <span class="pill bg-[#eef4e5] text-leaf">{{
                    ['招生中', '进行中', '已结束'][item.status]
                  }}</span>
                </div>
                <p class="mt-2 text-xs text-[#8b938d]">
                  {{ item.currentStudents }} / {{ item.maxStudents || '不限' }} 人<span
                    v-if="item.startDate"
                  >
                    · {{ item.startDate }} 至 {{ item.endDate || '未设置' }}</span
                  >
                </p>
              </div>
              <button
                v-if="item.inviteCode"
                class="btn-secondary py-2.5"
                @click="copyInviteCode(item.inviteCode)"
              >
                <Clipboard :size="15" />邀请码 {{ item.inviteCode }}
              </button>
              <button class="btn-secondary py-2.5" @click="toggleMembers(item)">
                <Users :size="15" />{{ expandedClassId === item.classId ? '收起成员' : '查看成员' }}
              </button>
            </div>
            <div v-if="expandedClassId === item.classId" class="border-t bg-[#fafbf7] p-5">
              <p v-if="!members[item.classId]" class="py-5 text-center text-sm text-[#8b938d]">
                正在加载成员…
              </p>
              <div
                v-else-if="members[item.classId].length"
                class="grid gap-2 sm:grid-cols-2 xl:grid-cols-3"
              >
                <div
                  v-for="member in members[item.classId]"
                  :key="member.id"
                  class="flex items-center gap-3 rounded-2xl border bg-white p-3"
                >
                  <span
                    class="grid size-9 place-items-center rounded-full bg-[#eaf2dd] text-xs font-black"
                    >{{ member.studentName.slice(0, 1) }}</span
                  >
                  <div class="min-w-0 flex-1">
                    <p class="truncate text-sm font-bold">{{ member.studentName }}</p>
                    <p class="text-[11px] text-[#8b938d]">
                      {{ new Date(member.joinedAt).toLocaleDateString() }} 加入
                    </p>
                  </div>
                  <button
                    class="icon-btn size-8 text-red-600"
                    title="移除成员"
                    @click="removeMember(item, member)"
                  >
                    <UserMinus :size="15" />
                  </button>
                </div>
              </div>
              <p v-else class="py-5 text-center text-sm text-[#8b938d]">班级暂无成员</p>
              <AppPagination
                :page="memberPages[String(item.classId)] || 1"
                :page-size="memberPageSize"
                :total="memberTotals[String(item.classId)] || 0"
                @change="loadMembers(item, $event)"
              />
            </div>
          </article>
          <div v-if="!classes.length" class="card py-16 text-center">
            <p class="font-bold">还没有班级</p>
            <p class="mt-1 text-sm text-[#8b938d]">创建班级后即可邀请学生加入</p>
          </div>
        </div>
      </section>
    </template>
  </div>
</template>
