<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Reading } from '@element-plus/icons-vue'

export interface CourseCardData {
  courseId: string
  courseName: string
  coverUrl?: string
  description?: string
  teacherName?: string
  teacherAvatar?: string
  categoryName?: string
}

const props = defineProps<{
  course: CourseCardData
  mode?: 'browse' | 'learning' | 'teaching'
}>()

const router = useRouter()

function handleClick() {
  if (props.mode === 'teaching') {
    router.push(`/course/teaching/${props.course.courseId}/chapters`)
  } else {
    router.push(`/course/learning/${props.course.courseId}/chapters`)
  }
}
</script>

<template>
  <el-card class="course-card" shadow="hover" @click="handleClick">
    <div class="card-cover">
      <el-image
        :src="course.coverUrl || '/src/assets/images/test.png'"
        fit="cover"
        class="cover-image"
      >
        <template #error>
          <div class="cover-fallback">
            <el-icon :size="40"><Reading /></el-icon>
          </div>
        </template>
      </el-image>
    </div>
    <div class="card-body">
      <h3 class="card-title" :title="course.courseName">{{ course.courseName }}</h3>
      <p class="card-desc" :title="course.description">{{ course.description || '暂无简介' }}</p>
      <div class="card-meta">
        <div v-if="course.teacherName" class="meta-teacher">
          <el-avatar :size="24" :src="course.teacherAvatar">
            {{ course.teacherName?.charAt(0) }}
          </el-avatar>
          <span class="teacher-name">{{ course.teacherName }}</span>
        </div>
        <el-tag v-if="course.categoryName" size="small" type="info">{{ course.categoryName }}</el-tag>
      </div>
    </div>
  </el-card>
</template>

<style scoped lang="scss">
.course-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  border-radius: 8px;
  overflow: hidden;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }

  :deep(.el-card__body) {
    padding: 0;
  }
}

.card-cover {
  height: 160px;
  overflow: hidden;

  .cover-image {
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

.card-body {
  padding: 12px 16px 16px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-desc {
  font-size: 13px;
  color: #909399;
  margin: 0 0 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.meta-teacher {
  display: flex;
  align-items: center;
  gap: 6px;

  .teacher-name {
    font-size: 13px;
    color: #606266;
  }
}
</style>
