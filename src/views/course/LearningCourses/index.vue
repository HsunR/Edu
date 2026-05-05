<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Reading } from '@element-plus/icons-vue'
import { getMyClasses, joinClass } from '@/api/course/class'
import type { ClassVO } from '@/api/course/types'

const router = useRouter()

const loading = ref(false)
const myClasses = ref<ClassVO[]>([])
const joinDialogVisible = ref(false)
const inviteCode = ref('')

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

function goToCourse(courseId: number) {
  router.push(`/course/learning/${courseId}/chapters`)
}

function goToCourseDetail(courseId: number) {
  router.push(`/course/detail/${courseId}`)
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

      <el-card
        v-for="cls in myClasses"
        :key="cls.classId"
        class="course-card"
        shadow="hover"
        @click="goToCourse(cls.courseId)"
      >
        <div class="card-body">
          <h3 class="card-title" :title="cls.courseName">{{ cls.courseName }}</h3>
          <p class="card-class">班级：{{ cls.className }}</p>
          <div class="card-meta">
            <span class="card-teacher">教师：{{ cls.teacherName }}</span>
            <span class="card-students">{{ cls.currentStudents }} 名同学</span>
          </div>
          <div class="card-actions">
            <el-button type="primary" size="small" @click.stop="goToCourse(cls.courseId)">
              进入学习
            </el-button>
            <el-button size="small" @click.stop="goToCourseDetail(cls.courseId)">
              课程详情
            </el-button>
          </div>
        </div>
      </el-card>
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
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  min-height: 200px;
}

.course-card {
  cursor: pointer;
  border-radius: 8px;
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
}

.card-body {
  padding: 4px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-class {
  font-size: 13px;
  color: #606266;
  margin: 0 0 8px;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;

  span {
    font-size: 12px;
    color: #909399;
  }
}

.card-actions {
  display: flex;
  gap: 8px;
}
</style>
