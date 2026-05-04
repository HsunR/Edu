<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCourseStore } from '@/stores/course'
import { useUserStore } from '@/stores/user'
import { getCourseClasses } from '@/api/course/course'
import { joinClass } from '@/api/course/class'
import type { ClassVO } from '@/api/course/types'
import { CourseStatus, YesNo, ClassStatus } from '@/types/enums'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const courseStore = useCourseStore()
const userStore = useUserStore()

const loading = ref(false)
const classes = ref<ClassVO[]>([])
const joinDialogVisible = ref(false)
const inviteCode = ref('')

const courseId = Number(route.params.courseId)

const statusMap: Record<number, { label: string; type: 'info' | 'success' | 'warning' }> = {
  [CourseStatus.Draft]: { label: '草稿', type: 'info' },
  [CourseStatus.Published]: { label: '已发布', type: 'success' },
  [CourseStatus.Archived]: { label: '已归档', type: 'warning' }
}

async function loadCourseDetail() {
  loading.value = true
  try {
    await courseStore.fetchCourseDetail(courseId)
    await loadClasses()
  } finally {
    loading.value = false
  }
}

async function loadClasses() {
  try {
    classes.value = await getCourseClasses(courseId)
  } catch {
    classes.value = []
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
    await loadClasses()
  } catch (error) {
    const msg = error instanceof Error ? error.message : '加入失败'
    ElMessage.error(msg)
  }
}

function goToTeaching() {
  router.push(`/course/TeachingCourses/CourseDetails/${courseId}/ChapterStudyTeaching`)
}

function goToLearning() {
  router.push(`/course/LearningCourses/CourseDetails/${courseId}/ChapterStudyLearning`)
}

onMounted(loadCourseDetail)
onUnmounted(() => courseStore.clearCurrentCourse())
</script>

<template>
  <div v-loading="loading" class="course-detail">
    <template v-if="courseStore.currentCourse">
      <div class="detail-header">
        <div class="header-cover">
          <el-image
            :src="courseStore.currentCourse.coverUrl || '/src/assets/images/test.png'"
            fit="cover"
            class="cover-img"
          >
            <template #error>
              <div class="cover-fallback">
                <el-icon :size="60"><Reading /></el-icon>
              </div>
            </template>
          </el-image>
        </div>
        <div class="header-info">
          <div class="info-top">
            <el-tag
              :type="statusMap[courseStore.currentCourse.status]?.type || 'info'"
              size="small"
            >
              {{ statusMap[courseStore.currentCourse.status]?.label || '未知' }}
            </el-tag>
            <el-tag v-if="courseStore.currentCourse.isPublic === YesNo.Yes" type="success" size="small">公开</el-tag>
            <el-tag v-else type="info" size="small">私有</el-tag>
          </div>
          <h1 class="info-title">{{ courseStore.currentCourse.courseName }}</h1>
          <p class="info-desc">{{ courseStore.currentCourse.description || '暂无课程简介' }}</p>
          <div class="info-teacher">
            <el-avatar :size="40" :src="courseStore.currentCourse.teacherAvatar">
              {{ courseStore.currentCourse.teacherName?.charAt(0) }}
            </el-avatar>
            <div class="teacher-info">
              <span class="teacher-name">{{ courseStore.currentCourse.teacherName }}</span>
              <span class="teacher-label">授课教师</span>
            </div>
          </div>
          <div class="info-actions">
            <el-button
              v-if="courseStore.isCourseOwner"
              type="primary"
              @click="goToTeaching"
            >
              管理课程
            </el-button>
            <el-button
              v-else
              type="primary"
              @click="goToLearning"
            >
              进入学习
            </el-button>
            <el-button
              v-if="!courseStore.isCourseOwner"
              @click="joinDialogVisible = true"
            >
              加入班级
            </el-button>
          </div>
        </div>
      </div>

      <div class="detail-body">
        <div class="body-chapters">
          <h2 class="section-title">课程目录</h2>
          <el-empty
            v-if="!courseStore.currentCourse.chapters?.length"
            description="暂无章节内容"
          />
          <el-collapse v-else>
            <el-collapse-item
              v-for="chapter in courseStore.currentCourse.chapters"
              :key="chapter.chapterId"
              :title="chapter.title"
              :name="chapter.chapterId"
            >
              <div
                v-for="section in chapter.sections"
                :key="section.sectionId"
                class="section-item"
              >
                <span class="section-title-text">
                  <el-tag size="small" type="warning" v-if="section.isFree === YesNo.Yes">免费</el-tag>
                  {{ section.title }}
                </span>
                <span class="section-resources">
                  {{ section.resources?.length || 0 }} 个资源
                </span>
              </div>
              <el-empty
                v-if="!chapter.sections?.length"
                description="暂无小节"
                :image-size="60"
              />
            </el-collapse-item>
          </el-collapse>
        </div>

        <div v-if="classes.length > 0" class="body-classes">
          <h2 class="section-title">相关班级</h2>
          <el-table :data="classes" stripe>
            <el-table-column prop="className" label="班级名称" />
            <el-table-column prop="teacherName" label="教师" width="120" />
            <el-table-column label="人数" width="100">
              <template #default="{ row }">
                {{ row.currentStudents }} / {{ row.maxStudents }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag
                  :type="row.status === ClassStatus.Enrolling ? 'success' : row.status === ClassStatus.InProgress ? '' : 'info'"
                  size="small"
                >
                  {{ row.status === ClassStatus.Enrolling ? '招生中' : row.status === ClassStatus.InProgress ? '进行中' : '已结束' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </template>

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

<script lang="ts">
import { Reading } from '@element-plus/icons-vue'
export default {
  components: { Reading }
}
</script>

<style scoped lang="scss">
.course-detail {
  padding: 20px;
}

.detail-header {
  display: flex;
  gap: 24px;
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 20px;
}

.header-cover {
  width: 320px;
  height: 200px;
  flex-shrink: 0;
  border-radius: 8px;
  overflow: hidden;

  .cover-img {
    width: 100%;
    height: 100%;
  }

  .cover-fallback {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
  }
}

.header-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.info-top {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.info-title {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 8px;
}

.info-desc {
  font-size: 14px;
  color: #606266;
  margin: 0 0 16px;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.info-teacher {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.teacher-info {
  display: flex;
  flex-direction: column;

  .teacher-name {
    font-size: 15px;
    font-weight: 500;
    color: #303133;
  }

  .teacher-label {
    font-size: 12px;
    color: #909399;
  }
}

.info-actions {
  margin-top: auto;
  display: flex;
  gap: 12px;
}

.detail-body {
  display: flex;
  gap: 20px;
}

.body-chapters {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.body-classes {
  width: 400px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.section-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  border-radius: 4px;
  transition: background 0.2s;

  &:hover {
    background: #f5f7fa;
  }
}

.section-title-text {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #303133;
}

.section-resources {
  font-size: 12px;
  color: #909399;
}
</style>
