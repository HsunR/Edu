<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
    <el-form-item label="工号" prop="teacherNo">
      <el-input v-model="form.teacherNo" maxlength="30" />
    </el-form-item>
    <el-form-item label="职称" prop="title">
      <el-input v-model="form.title" maxlength="20" />
    </el-form-item>
    <el-form-item label="院系" prop="department">
      <el-input v-model="form.department" maxlength="50" />
    </el-form-item>
    <el-form-item label="简介" prop="bio">
      <el-input v-model="form.bio" type="textarea" maxlength="200" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submit">保存</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { updateProfile } from '@/api/user/user'
import type { ProfileUpdateRequest } from '@/api/user/types'

const userStore = useUserStore()
const formRef = ref()

const form = ref<ProfileUpdateRequest>({})

const rules = ref({
  teacherNo: [{ required: true, message: '工号不能为空', trigger: 'blur' }]
})

async function submit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    await updateProfile(form.value)
    await userStore.fetchUserInfo()
    ElMessage.success('修改成功')
  } catch (error: any) {
    ElMessage.error(error?.message || '修改失败')
  }
}

watch(
  () => userStore.userInfo?.teacherProfile,
  (newVal) => {
    if (newVal) {
      form.value = {
        teacherNo: newVal.teacherNo,
        title: newVal.title,
        department: newVal.department,
        bio: newVal.bio
      }
    }
  },
  { immediate: true, deep: true }
)
</script>
