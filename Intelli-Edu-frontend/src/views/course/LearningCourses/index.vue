<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyClasses, joinClass } from '@/api/course/class'
import type { ClassVO } from '@/api/course/types'
import CourseCard from '../components/CourseCard.vue'
import type { CourseCardData } from '../components/CourseCard.vue'

const loading = ref(false)
const myClasses = ref<ClassVO[]>([])
const joinDialogVisible = ref(false)
const inviteCode = ref('')

function toCardData(cls: ClassVO): CourseCardData {
  return {
    courseId: cls.courseId,
    courseName: cls.courseName,
    description: `班级：${cls.className}`,
    teacherName: cls.teacherName
  }
}

async function loadMyClasses() {
  loading.value = true
  try {
    myClasses.value = await getMyClasses()
  } catch {
    myClasses.value = []
  } finally {
    loading.value = false
  }
}

async function handleJoinClass() {
  if (!inviteCode.value.trim()) {
    ElMessage.warning('请输入邀请码')
    return
  }
  try {
    await joinClass({ inviteCode: inviteCode.value.trim() })
    ElMessage.success('加入班级成功')
    joinDialogVisible.value = false
    inviteCode.value = ''
    await loadMyClasses()
  } catch (error) {
    const msg = error instanceof Error ? error.message : '加入失败'
    ElMessage.error(msg)
  }
}

onMounted(loadMyClasses)
</script>

<template>
  <div class="learning-courses">
    <div class="page-header">
      <h2 class="page-title">我学的课</h2>
      <el-button type="primary" @click="joinDialogVisible = true">加入班级</el-button>
    </div>

    <div v-loading="loading" class="courses-grid">
      <el-empty v-if="!loading && myClasses.length === 0" description="暂无课程，点击上方按钮加入班级" />

      <CourseCard
        v-for="cls in myClasses"
        :key="cls.classId"
        :course="toCardData(cls)"
        mode="learning"
      />
    </div>

    <el-dialog v-model="joinDialogVisible" title="加入班级" width="400px">
      <el-input
        v-model="inviteCode"
        placeholder="请输入班级邀请码"
        clearable
        @keyup.enter="handleJoinClass"
      />
      <template #footer>
        <el-button @click="joinDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleJoinClass">确认加入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.learning-courses {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
  min-height: 200px;
}
</style>
