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
const exams = ref<ExamVO[]>([])
const examTotal = ref(0)
const examPage = ref(1)
const statusFilter = ref<ExamStatus | undefined>(undefined)

const examTypeMap: Record<number, { label: string; type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  [ExamType.Exam]: { label: '考试', type: 'danger' },
  [ExamType.Practice]: { label: '练习', type: 'primary' },
  [ExamType.Homework]: { label: '作业', type: 'success' }
}

const examStatusMap: Record<number, { label: string; type: 'primary' | 'success' | 'warning' | 'info' | 'danger' }> = {
  [ExamStatus.NotStarted]: { label: '未开始', type: 'info' },
  [ExamStatus.InProgress]: { label: '进行中', type: 'success' },
  [ExamStatus.Ended]: { label: '已结束', type: 'warning' },
  [ExamStatus.Graded]: { label: '已批阅', type: 'success' }
}

async function loadExams() {
  loading.value = true
  try {
    const result = await getExamList({
      current: examPage.value,
      pageSize: 20,
      courseId,
      examType: ExamType.Exam,
      status: statusFilter.value
    })
    exams.value = result.records
    examTotal.value = result.total
  } finally {
    loading.value = false
  }
}

function enterExamPage(examId: string) {
  router.push(`/course/learning/${courseId}/exam-answer/${examId}`)
}

function handlePageChange(page: number) {
  examPage.value = page
  loadExams()
}

function formatTime(time?: string) {
  if (!time) return ''
  return time.replace('T', ' ').split('.')[0]
}

onMounted(loadExams)
</script>

<template>
  <div class="learning-exams">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/learning' }">我学的课</el-breadcrumb-item>
            <el-breadcrumb-item>课程考试</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </template>

      <div class="toolbar">
        <el-radio-group v-model="statusFilter" size="small" @change="() => { examPage = 1; loadExams() }">
          <el-radio-button :value="undefined">全部</el-radio-button>
          <el-radio-button :value="ExamStatus.InProgress">进行中</el-radio-button>
          <el-radio-button :value="ExamStatus.Ended">已结束</el-radio-button>
          <el-radio-button :value="ExamStatus.Graded">已批阅</el-radio-button>
        </el-radio-group>
      </div>

      <div v-loading="loading" class="exam-cards">
        <el-empty v-if="!loading && exams.length === 0" description="暂无考试" />

        <el-card v-for="exam in exams" :key="exam.examId" class="exam-card" shadow="hover">
          <div class="exam-card-body">
            <div class="exam-info">
              <h3 class="exam-name">{{ exam.examName }}</h3>
              <div class="exam-meta">
                <el-tag :type="examTypeMap[exam.examType]?.type" size="small">
                  {{ examTypeMap[exam.examType]?.label }}
                </el-tag>
                <el-tag :type="examStatusMap[exam.status]?.type" size="small">
                  {{ examStatusMap[exam.status]?.label || ExamStatusLabels[exam.status] }}
                </el-tag>
                <span class="exam-time">{{ formatTime(exam.startTime) }} ~ {{ formatTime(exam.endTime) }}</span>
              </div>
              <div class="exam-detail">
                <span v-if="exam.durationMinutes">时长：{{ exam.durationMinutes }}分钟</span>
                <span>试卷：{{ exam.paperName }}</span>
              </div>
            </div>
            <div class="exam-action">
              <el-button
                v-if="exam.status === ExamStatus.InProgress"
                type="primary"
                @click="enterExamPage(exam.examId)"
              >
                进入考试
              </el-button>
              <el-button
                v-if="exam.status === ExamStatus.Ended || exam.status === ExamStatus.Graded"
                @click="enterExamPage(exam.examId)"
              >
                查看结果
              </el-button>
              <el-tag v-if="exam.status === ExamStatus.NotStarted" type="info">未开放</el-tag>
            </div>
          </div>
        </el-card>
      </div>

      <el-pagination
        v-if="examTotal > 20"
        v-model:current-page="examPage"
        :total="examTotal"
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

.exam-cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.exam-card {
  .exam-card-body {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .exam-info {
    .exam-name {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 8px;
    }

    .exam-meta {
      display: flex;
      gap: 8px;
      align-items: center;
      margin-bottom: 6px;

      .exam-time {
        font-size: 12px;
        color: #909399;
      }
    }

    .exam-detail {
      display: flex;
      gap: 16px;

      span {
        font-size: 13px;
        color: #606266;
      }
    }
  }

  .exam-action {
    flex-shrink: 0;
  }
}
</style>
