<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
    <el-form-item label="学号" prop="studentNo">
      <el-input v-model="form.studentNo" maxlength="30" />
    </el-form-item>
    <el-form-item label="年级" prop="grade">
      <el-input v-model="form.grade" maxlength="20" />
    </el-form-item>
    <el-form-item label="专业" prop="major">
      <el-input v-model="form.major" maxlength="50" />
    </el-form-item>
    <el-form-item label="入学年份" prop="enrollmentYear">
      <el-input v-model.number="form.enrollmentYear" maxlength="4" />
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
  studentNo: [{ required: true, message: '学号不能为空', trigger: 'blur' }]
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
  () => userStore.userInfo?.studentProfile,
  (newVal) => {
    if (newVal) {
      form.value = {
        studentNo: newVal.studentNo,
        grade: newVal.grade,
        major: newVal.major,
        enrollmentYear: newVal.enrollmentYear
      }
    }
  },
  { immediate: true, deep: true }
)
</script>
