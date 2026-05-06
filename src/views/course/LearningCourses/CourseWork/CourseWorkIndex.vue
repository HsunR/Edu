<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getExamList } from '@/api/exam/index'
import type { ExamVO } from '@/api/exam/types'
import { ExamType, ExamStatus, ExamStatusLabels } from '@/types/enums'

const route = useRoute()
const router = useRouter()
const courseId = route.params.id as string

const loading = ref(false)
const homeworkList = ref<ExamVO[]>([])
const homeworkTotal = ref(0)
const homeworkPage = ref(1)
const statusFilter = ref<ExamStatus | undefined>(undefined)

const examStatusMap: Record<number, { label: string; type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  [ExamStatus.NotStarted]: { label: '未开始', type: 'info' },
  [ExamStatus.InProgress]: { label: '进行中', type: 'success' },
  [ExamStatus.Ended]: { label: '已结束', type: 'warning' },
  [ExamStatus.Graded]: { label: '已批阅', type: 'success' }
}

async function loadHomework() {
  loading.value = true
  try {
    const result = await getExamList({
      current: homeworkPage.value,
      pageSize: 20,
      courseId,
      examType: ExamType.Homework,
      status: statusFilter.value
    })
    homeworkList.value = result.records
    homeworkTotal.value = result.total
  } finally {
    loading.value = false
  }
}

function enterHomework(examId: string) {
  router.push(`/course/learning/${courseId}/exam-answer/${examId}`)
}

function handlePageChange(page: number) {
  homeworkPage.value = page
  loadHomework()
}

function formatTime(time?: string) {
  if (!time) return ''
  return time.replace('T', ' ').split('.')[0]
}

onMounted(loadHomework)
</script>

<template>
  <div class="learning-homework">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/learning' }">我学的课</el-breadcrumb-item>
            <el-breadcrumb-item>课程作业</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </template>

      <div class="toolbar">
        <el-radio-group v-model="statusFilter" size="small" @change="() => { homeworkPage = 1; loadHomework() }">
          <el-radio-button :value="undefined">全部</el-radio-button>
          <el-radio-button :value="ExamStatus.InProgress">进行中</el-radio-button>
          <el-radio-button :value="ExamStatus.Ended">已结束</el-radio-button>
          <el-radio-button :value="ExamStatus.Graded">已批阅</el-radio-button>
        </el-radio-group>
      </div>

      <div v-loading="loading" class="homework-cards">
        <el-empty v-if="!loading && homeworkList.length === 0" description="暂无作业" />

        <el-card v-for="hw in homeworkList" :key="hw.examId" class="homework-card" shadow="hover">
          <div class="homework-card-body">
            <div class="homework-info">
              <h3 class="homework-name">{{ hw.examName }}</h3>
              <div class="homework-meta">
                <el-tag type="success" size="small">作业</el-tag>
                <el-tag :type="examStatusMap[hw.status]?.type" size="small">
                  {{ examStatusMap[hw.status]?.label || ExamStatusLabels[hw.status] }}
                </el-tag>
                <span class="homework-time">{{ formatTime(hw.startTime) }} ~ {{ formatTime(hw.endTime) }}</span>
              </div>
              <div class="homework-detail">
                <span v-if="hw.durationMinutes">时长：{{ hw.durationMinutes }}分钟</span>
                <span>试卷：{{ hw.paperName }}</span>
                <span v-if="hw.allowLateSubmit" style="color: #e6a23c">允许迟交</span>
              </div>
            </div>
            <div class="homework-action">
              <el-button
                v-if="hw.status === ExamStatus.InProgress"
                type="primary"
                @click="enterHomework(hw.examId)"
              >
                做作业
              </el-button>
              <el-button
                v-if="hw.status === ExamStatus.Ended || hw.status === ExamStatus.Graded"
                @click="enterHomework(hw.examId)"
              >
                查看结果
              </el-button>
              <el-tag v-if="hw.status === ExamStatus.NotStarted" type="info">未开放</el-tag>
            </div>
          </div>
        </el-card>
      </div>

      <el-pagination
        v-if="homeworkTotal > 20"
        v-model:current-page="homeworkPage"
        :total="homeworkTotal"
        :page-size="20"
        layout="prev, pager, next"
        style="margin-top: 12px"
        @current-change="handlePageChange"
      />
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.toolbar {
  margin-bottom: 16px;
}

.homework-cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.homework-card {
  .homework-card-body {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .homework-info {
    .homework-name {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 8px;
    }

    .homework-meta {
      display: flex;
      gap: 8px;
      align-items: center;
      margin-bottom: 6px;

      .homework-time {
        font-size: 12px;
        color: #909399;
      }
    }

    .homework-detail {
      display: flex;
      gap: 16px;

      span {
        font-size: 13px;
        color: #606266;
      }
    }
  }

  .homework-action {
    flex-shrink: 0;
  }
}
</style>
