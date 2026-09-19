<script setup lang="ts">
import { computed, ref } from 'vue'
import { LockKeyhole, Save, UserRound } from 'lucide-vue-next'
import { userApi } from '@/api/services'
import { useSessionStore } from '@/stores/session'
const session = useSessionStore()
const tab = ref<'info' | 'password'>('info')
const saving = ref(false)
const message = ref('')
const error = ref('')
const form = ref({
  name: session.user?.name || '',
  sex: session.user?.sex ?? 0,
  school: session.user?.school || '',
  personalSignature: session.user?.personalSignature || '',
  email: session.user?.email || '',
  mobile: session.user?.mobile || '',
  studentNo: session.user?.studentProfile?.studentNo || '',
  grade: session.user?.studentProfile?.grade || '',
  major: session.user?.studentProfile?.major || '',
  enrollmentYear: session.user?.studentProfile?.enrollmentYear || new Date().getFullYear(),
  teacherNo: session.user?.teacherProfile?.teacherNo || '',
  title: session.user?.teacherProfile?.title || '',
  department: session.user?.teacherProfile?.department || '',
  bio: session.user?.teacherProfile?.bio || '',
})
const password = ref({ oldPassword: '', newPassword: '' })
const isStudent = computed(() => session.role === 'Student')
const isTeacher = computed(() => session.role === 'Teacher')
async function save() {
  saving.value = true
  error.value = ''
  message.value = ''
  try {
    await userApi.update({
      name: form.value.name,
      sex: form.value.sex,
      school: form.value.school,
      personalSignature: form.value.personalSignature,
    })
    if (isStudent.value)
      await userApi.updateProfile({
        studentNo: form.value.studentNo,
        grade: form.value.grade,
        major: form.value.major,
        enrollmentYear: form.value.enrollmentYear,
      })
    if (isTeacher.value)
      await userApi.updateProfile({
        teacherNo: form.value.teacherNo,
        title: form.value.title,
        department: form.value.department,
        bio: form.value.bio,
      })
    await session.loadUser()
    message.value = '资料已保存'
  } catch (e) {
    error.value = e instanceof Error ? e.message : '保存失败'
  } finally {
    saving.value = false
  }
}
async function changePassword() {
  saving.value = true
  error.value = ''
  message.value = ''
  try {
    await userApi.updatePassword(password.value)
    password.value = { oldPassword: '', newPassword: '' }
    message.value = '密码修改成功'
  } catch (e) {
    error.value = e instanceof Error ? e.message : '修改失败'
  } finally {
    saving.value = false
  }
}
</script>
<template>
  <div class="mx-auto max-w-4xl">
    <div class="grid gap-6 lg:grid-cols-[220px_1fr]">
      <aside class="card h-fit p-4">
        <div class="flex flex-col items-center py-5">
          <div
            class="grid size-20 place-items-center overflow-hidden rounded-full bg-[#f2c79b] text-2xl font-black"
          >
            <img
              v-if="session.user?.avatarUrl"
              :src="session.user.avatarUrl"
              class="h-full w-full object-cover"
            /><span v-else>{{ session.user?.name?.slice(0, 1) }}</span>
          </div>
          <h2 class="mt-4 font-extrabold">{{ session.user?.name }}</h2>
          <p class="mt-1 text-xs text-[#8b938d]">{{ session.roleLabel }}</p>
        </div>
        <nav class="space-y-1">
          <button
            :class="[
              'flex w-full items-center gap-2 rounded-xl px-3 py-2.5 text-sm font-bold',
              tab === 'info' ? 'bg-ink text-white' : '',
            ]"
            @click="tab = 'info'"
          >
            <UserRound :size="16" />基本资料</button
          ><button
            :class="[
              'flex w-full items-center gap-2 rounded-xl px-3 py-2.5 text-sm font-bold',
              tab === 'password' ? 'bg-ink text-white' : '',
            ]"
            @click="tab = 'password'"
          >
            <LockKeyhole :size="16" />修改密码
          </button>
        </nav>
      </aside>
      <section class="card p-6 sm:p-8">
        <p class="eyebrow">PROFILE SERVICE</p>
        <h3 class="mt-2 text-2xl font-black">{{ tab === 'info' ? '基本资料' : '修改密码' }}</h3>
        <p v-if="error" class="mt-5 rounded-xl bg-red-50 p-3 text-sm text-red-600">{{ error }}</p>
        <p v-if="message" class="mt-5 rounded-xl bg-green-50 p-3 text-sm text-green-700">
          {{ message }}
        </p>
        <form v-if="tab === 'info'" class="mt-7 grid gap-5 sm:grid-cols-2" @submit.prevent="save">
          <label
            ><span class="mb-2 block text-xs font-bold">姓名</span
            ><input v-model="form.name" class="field" /></label
          ><label
            ><span class="mb-2 block text-xs font-bold">性别</span
            ><select v-model.number="form.sex" class="field">
              <option :value="0">未知</option>
              <option :value="1">男</option>
              <option :value="2">女</option>
            </select></label
          ><label
            ><span class="mb-2 block text-xs font-bold">学校</span
            ><input v-model="form.school" class="field" /></label
          ><label
            ><span class="mb-2 block text-xs font-bold">邮箱</span
            ><input v-model="form.email" class="field bg-[#f4f5f1]" disabled /></label
          ><template v-if="isStudent"
            ><label
              ><span class="mb-2 block text-xs font-bold">学号</span
              ><input v-model="form.studentNo" class="field" /></label
            ><label
              ><span class="mb-2 block text-xs font-bold">年级</span
              ><input v-model="form.grade" class="field" /></label
            ><label
              ><span class="mb-2 block text-xs font-bold">专业</span
              ><input v-model="form.major" class="field" /></label
            ><label
              ><span class="mb-2 block text-xs font-bold">入学年份</span
              ><input
                v-model.number="form.enrollmentYear"
                type="number"
                class="field" /></label></template
          ><template v-if="isTeacher"
            ><label
              ><span class="mb-2 block text-xs font-bold">工号</span
              ><input v-model="form.teacherNo" class="field" /></label
            ><label
              ><span class="mb-2 block text-xs font-bold">职称</span
              ><input v-model="form.title" class="field" /></label
            ><label
              ><span class="mb-2 block text-xs font-bold">院系</span
              ><input v-model="form.department" class="field" /></label
            ><label
              ><span class="mb-2 block text-xs font-bold">简介</span
              ><input v-model="form.bio" class="field" /></label></template
          ><label class="sm:col-span-2"
            ><span class="mb-2 block text-xs font-bold">个人签名</span
            ><textarea v-model="form.personalSignature" class="field min-h-24" />
          </label>
          <div class="sm:col-span-2 flex justify-end">
            <button class="btn-primary" :disabled="saving">
              <Save :size="16" />{{ saving ? '保存中…' : '保存资料' }}
            </button>
          </div>
        </form>
        <form v-else class="mt-7 max-w-md space-y-5" @submit.prevent="changePassword">
          <label
            ><span class="mb-2 block text-xs font-bold">原密码</span
            ><input v-model="password.oldPassword" type="password" class="field" required /></label
          ><label
            ><span class="mb-2 block text-xs font-bold">新密码</span
            ><input
              v-model="password.newPassword"
              type="password"
              class="field"
              minlength="6"
              required /></label
          ><button class="btn-primary" :disabled="saving">确认修改</button>
        </form>
      </section>
    </div>
  </div>
</template>
