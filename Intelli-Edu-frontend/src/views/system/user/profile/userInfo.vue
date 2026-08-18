<template>
  <el-form ref="userRef" :model="form" :rules="rules" label-width="80px">
    <el-form-item label="用户姓名" prop="name">
      <el-input v-model="form.name" maxlength="30" />
    </el-form-item>
    <el-form-item label="个性签名" prop="personalSignature">
      <el-input v-model="form.personalSignature" maxlength="100" type="textarea" />
    </el-form-item>
    <el-form-item label="学校" prop="school">
      <el-input v-model="form.school" maxlength="50" />
    </el-form-item>
    <el-form-item label="性别">
      <el-radio-group v-model="form.sex">
        <el-radio :value="Sex.Male">男</el-radio>
        <el-radio :value="Sex.Female">女</el-radio>
        <el-radio :value="Sex.Unknown">未知</el-radio>
      </el-radio-group>
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
import type { UserUpdateRequest } from '@/api/user/types'
import { Sex } from '@/types/enums'

const userStore = useUserStore()
const userRef = ref()

const form = ref<UserUpdateRequest>({
  name: '',
  personalSignature: '',
  school: '',
  sex: Sex.Unknown
})

const rules = ref({
  name: [{ required: true, message: '用户姓名不能为空', trigger: 'blur' }]
})

async function submit() {
  const valid = await userRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    await userStore.updateUserInfo(form.value)
    ElMessage.success('修改成功')
  } catch (error: any) {
    ElMessage.error(error?.message || '修改失败')
  }
}

watch(
  () => userStore.userInfo,
  (newVal) => {
    if (newVal) {
      form.value = {
        name: newVal.name,
        personalSignature: newVal.personalSignature,
        school: newVal.school,
        sex: newVal.sex
      }
    }
  },
  { immediate: true, deep: true }
)
</script>
