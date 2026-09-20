<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { GraduationCap, Search, Trash2, Users } from 'lucide-vue-next'
import { userApi } from '@/api/services'
import { useSessionStore } from '@/stores/session'
import { useUiStore } from '@/stores/ui'
import type { User } from '@/types'
import AppPagination from '@/components/AppPagination.vue'
const session = useSessionStore()
const ui = useUiStore()
const users = ref<User[]>([])
const loading = ref(true)
const error = ref('')
const query = ref('')
const total = ref(0)
const page = ref(1)
const pageSize = 15
const showAssign = ref(false)
const selected = ref<User | null>(null)
const form = ref({ teacherNo: '', title: '', department: '', bio: '' })
async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await userApi.list({
      current: page.value,
      pageSize,
      name: query.value || undefined,
    })
    users.value = result.records
    total.value = result.total
  } catch (e) {
    error.value = e instanceof Error ? e.message : '加载失败'
  } finally {
    loading.value = false
  }
}
async function remove(user: User) {
  if (user.userId === session.user?.userId) {
    ui.notify('不能删除当前登录账号', 'error')
    return
  }
  const confirmed = await ui.confirm({
    title: '删除用户',
    message: `确定永久删除用户“${user.name}”吗？此操作无法撤销。`,
    confirmLabel: '删除用户',
    danger: true,
  })
  if (!confirmed) return
  try {
    await userApi.remove(user.userId)
    if (users.value.length === 1 && page.value > 1) page.value--
    await load()
    ui.notify('用户已删除', 'success')
  } catch (e) {
    error.value = e instanceof Error ? e.message : '删除失败'
  }
}
function openAssign(user: User) {
  selected.value = user
  showAssign.value = true
}
async function assign() {
  if (!selected.value) return
  try {
    await userApi.assignTeacher({ userId: selected.value.userId, ...form.value })
    showAssign.value = false
    await load()
    ui.notify('教师角色已分配', 'success')
  } catch (e) {
    error.value = e instanceof Error ? e.message : '分配失败'
  }
}
onMounted(load)
function search() {
  page.value = 1
  load()
}
function changePage(nextPage: number) {
  page.value = nextPage
  load()
}
</script>
<template>
  <div>
    <div class="flex items-end justify-between">
      <div>
        <h2 class="text-3xl font-black">用户管理</h2>
        <p class="mt-2 text-sm text-[#7d857f]">查询平台用户、分配教师角色并维护账号。</p>
      </div>
      <span class="pill bg-[#eaf2dd] text-leaf"
        ><Users :size="14" class="mr-1" />{{ total }} 用户</span
      >
    </div>
    <form class="relative mt-7 max-w-lg" @submit.prevent="search">
      <Search :size="17" class="absolute left-4 top-1/2 -translate-y-1/2 text-[#8b938d]" /><input
        v-model="query"
        class="field pl-11"
        placeholder="按姓名搜索…"
      />
    </form>
    <p v-if="error" class="mt-5 rounded-2xl bg-red-50 p-4 text-sm text-red-600">{{ error }}</p>
    <div class="card mt-5 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full min-w-[700px] text-left">
          <thead class="bg-[#f8f9f5] text-[10px] tracking-wider text-[#8b938d]">
            <tr>
              <th class="px-6 py-4">用户</th>
              <th>角色</th>
              <th>学校</th>
              <th>状态</th>
              <th class="pr-6 text-right">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="5" class="p-12 text-center text-sm">正在加载…</td>
            </tr>
            <tr v-for="user in users" v-else :key="user.userId" class="border-t text-sm">
              <td class="px-6 py-4">
                <b>{{ user.name }}</b>
                <p class="text-[11px] text-[#8b938d]">
                  {{ user.email || user.mobile || `ID ${user.userId}` }}
                </p>
              </td>
              <td>{{ { Student: '学生', Teacher: '教师', Admin: '管理员' }[user.userType] }}</td>
              <td>{{ user.school || '—' }}</td>
              <td>
                <span
                  :class="[
                    'pill py-1',
                    user.status === 1 || user.status === '正常'
                      ? 'bg-[#eef4e5] text-leaf'
                      : user.status === 0 || user.status === '禁止'
                        ? 'bg-red-50 text-red-600'
                        : 'bg-[#f0f2ed] text-[#68716b]',
                  ]"
                  >{{
                    user.status === 1 || user.status === '正常'
                      ? '正常'
                      : user.status === 0 || user.status === '禁止'
                        ? '停用'
                        : '—'
                  }}</span
                >
              </td>
              <td class="pr-6">
                <div class="flex justify-end gap-1">
                  <button
                    v-if="user.userType === 'Student'"
                    class="icon-btn size-8"
                    title="分配教师"
                    @click="openAssign(user)"
                  >
                    <GraduationCap :size="15" /></button
                  ><button
                    class="icon-btn size-8 text-red-500"
                    title="删除"
                    :disabled="user.userId === session.user?.userId"
                    @click="remove(user)"
                  >
                    <Trash2 :size="15" />
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!loading && !users.length">
              <td colspan="5" class="p-12 text-center text-sm text-[#8b938d]">
                暂无符合条件的用户
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <AppPagination
      :page="page"
      :page-size="pageSize"
      :total="total"
      :disabled="loading"
      @change="changePage"
    />
    <Transition name="fade"
      ><div
        v-if="showAssign"
        class="fixed inset-0 z-50 grid place-items-center bg-ink/35 p-5"
        @click.self="showAssign = false"
      >
        <form class="card w-full max-w-md p-7" @submit.prevent="assign">
          <h3 class="text-xl font-black">将 {{ selected?.name }} 分配为教师</h3>
          <div class="mt-5 space-y-3">
            <input v-model="form.teacherNo" class="field" placeholder="教师工号" required /><input
              v-model="form.title"
              class="field"
              placeholder="职称"
            /><input v-model="form.department" class="field" placeholder="所属院系" /><textarea
              v-model="form.bio"
              class="field"
              placeholder="教师简介"
            />
          </div>
          <div class="mt-5 flex justify-end gap-2">
            <button type="button" class="btn-secondary" @click="showAssign = false">取消</button
            ><button class="btn-primary">确认分配</button>
          </div>
        </form>
      </div></Transition
    >
  </div>
</template>
