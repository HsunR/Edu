<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getExamStats, getExamSheets, getSheetDetail, gradeRecord, finishGrading } from '@/api/exam/index'
import type { ExamStatsVO, AnswerSheetVO, AnswerSheetDetailVO, AnswerRecordVO, GradeRequest } from '@/api/exam/types'
import { QuestionType, GradingStatus, SheetStatus } from '@/types/enums'

const route = useRoute()
const router = useRouter()
const courseId = route.params.id as string
const examId = route.params.examId as string

function goBack() {
  router.push(`/course/teaching/${courseId}/exams`)
}

const loading = ref(false)
const stats = ref<ExamStatsVO | null>(null)
const sheets = ref<AnswerSheetVO[]>([])

const gradingDrawerVisible = ref(false)
const sheetDetail = ref<AnswerSheetDetailVO | null>(null)
const sheetDetailLoading = ref(false)
const gradingMap = ref<Map<string, GradeRequest>>(new Map())

const questionTypeMap: Record<number, string> = {
  [QuestionType.SingleChoice]: '单选题',
  [QuestionType.MultipleChoice]: '多选题',
  [QuestionType.TrueFalse]: '判断题',
  [QuestionType.FillBlank]: '填空题',
  [QuestionType.ShortAnswer]: '简答题'
}

type TagType = 'success' | 'primary' | 'warning' | 'info' | 'danger' | undefined

const gradingStatusMap: Record<string, { label: string; type: TagType }> = {
  'NOT_GRADED': { label: '未批改', type: 'info' },
  'GRADED': { label: '已批改', type: 'success' },
  'AI_GRADING': { label: 'AI批改中', type: 'warning' }
}

const sheetStatusMap: Record<string, { label: string; type: TagType }> = {
  'NOT_STARTED': { label: '未开始', type: 'info' },
  'IN_PROGRESS': { label: '进行中', type: 'primary' },
  'ENDED': { label: '已交卷', type: 'warning' },
  'GRADED': { label: '已批阅', type: 'success' }
}

async function loadStats() {
  loading.value = true
  try {
    const [statsResult, sheetsResult] = await Promise.all([
      getExamStats(examId),
      getExamSheets(examId)
    ])
    stats.value = statsResult
    sheets.value = sheetsResult
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

async function openGrading(sheet: AnswerSheetVO) {
  gradingDrawerVisible.value = true
  sheetDetailLoading.value = true
  gradingMap.value.clear()
  try {
    sheetDetail.value = await getSheetDetail(sheet.sheetId)
    if (sheetDetail.value?.records) {
      for (const record of sheetDetail.value.records) {
        gradingMap.value.set(record.recordId, {
          score: record.score,
          comment: record.comment || ''
        })
      }
    }
  } catch {
    sheetDetail.value = null
  } finally {
    sheetDetailLoading.value = false
  }
}

async function handleGrade(recordId: string) {
  const gradeData = gradingMap.value.get(recordId)
  if (!gradeData) return
  try {
    await gradeRecord(recordId, gradeData)
    ElMessage.success('批改成功')
    if (sheetDetail.value) {
      const record = sheetDetail.value.records.find(r => r.recordId === recordId)
      if (record) {
        record.score = gradeData.score
        record.gradingStatus = 'GRADED'
        record.comment = gradeData.comment || ''
      }
    }
  } catch (error) {
    const msg = error instanceof Error ? error.message : '批改失败'
    ElMessage.error(msg)
  }
}

async function handleFinishGrading() {
  if (!sheetDetail.value) return
  try {
    await ElMessageBox.confirm('确认完成批阅？完成后将汇总得分。', '完成批阅确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    })
    await finishGrading(sheetDetail.value.sheetId)
    ElMessage.success('批阅完成')
    gradingDrawerVisible.value = false
    await loadStats()
  } catch {
    // cancelled
  }
}

function isObjectiveQuestion(type: number): boolean {
  return type === QuestionType.SingleChoice || type === QuestionType.MultipleChoice || type === QuestionType.TrueFalse
}

onMounted(loadStats)
</script>

<template>
  <div v-loading="loading" class="exam-stats">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button :icon="ArrowLeft" @click="goBack">返回</el-button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/teaching' }">我教的课</el-breadcrumb-item>
            <el-breadcrumb-item>考试统计</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </template>

      <template v-if="stats">
        <div class="stats-cards">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-value">{{ stats.totalStudents }}</div>
            <div class="stat-label">应考人数</div>
          </el-card>
          <el-card shadow="hover" class="stat-card">
            <div class="stat-value">{{ stats.submittedCount }}</div>
            <div class="stat-label">已交卷</div>
          </el-card>
          <el-card shadow="hover" class="stat-card">
            <div class="stat-value">{{ stats.answeringCount }}</div>
            <div class="stat-label">答题中</div>
          </el-card>
          <el-card shadow="hover" class="stat-card">
            <div class="stat-value">{{ stats.gradedCount }}</div>
            <div class="stat-label">已批阅</div>
          </el-card>
        </div>

        <el-divider />

        <div class="score-stats">
          <el-card shadow="hover" class="score-card">
            <div class="score-value">{{ stats.maxScore }}</div>
            <div class="score-label">最高分</div>
          </el-card>
          <el-card shadow="hover" class="score-card">
            <div class="score-value">{{ stats.minScore }}</div>
            <div class="score-label">最低分</div>
          </el-card>
          <el-card shadow="hover" class="score-card">
            <div class="score-value">{{ stats.avgScore }}</div>
            <div class="score-label">平均分</div>
          </el-card>
        </div>

        <el-divider />

        <h3 style="margin: 0 0 12px">答卷列表</h3>
        <el-table :data="sheets" stripe>
          <el-table-column prop="studentName" label="学生" width="120" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="sheetStatusMap[row.status]?.type" size="small">
                {{ sheetStatusMap[row.status]?.label }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="totalScore" label="总分" width="80" />
          <el-table-column prop="objectiveScore" label="客观题" width="80" />
          <el-table-column prop="subjectiveScore" label="主观题" width="80" />
          <el-table-column label="提交时间" width="180">
            <template #default="{ row }">
              {{ row.submitTime?.split(' ')[0] || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button
                v-if="row.status === 'ENDED' || row.status === 'IN_PROGRESS'"
                size="small"
                link
                type="primary"
                @click="openGrading(row)"
              >
                批阅
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </el-card>

    <el-drawer v-model="gradingDrawerVisible" title="批阅答卷" size="65%">
      <div v-loading="sheetDetailLoading">
        <template v-if="sheetDetail">
          <div class="sheet-header">
            <span>学生：{{ sheetDetail.studentId }}</span>
            <span>总分：{{ sheetDetail.totalScore }}</span>
            <span>客观题：{{ sheetDetail.objectiveScore }}</span>
            <span>主观题：{{ sheetDetail.subjectiveScore }}</span>
            <el-button type="primary" @click="handleFinishGrading">完成批阅</el-button>
          </div>

          <div class="records-list">
            <div v-for="(record, idx) in sheetDetail.records" :key="record.recordId" class="record-item">
              <div class="record-header">
                <span class="record-index">{{ idx + 1 }}.</span>
                <el-tag :type="questionTypeMap[record.questionType] ? 'primary' : 'info'" size="small">
                  {{ questionTypeMap[record.questionType] }}
                </el-tag>
                <span class="record-score">题目分值：{{ record.questionScore }}</span>
                <el-tag :type="gradingStatusMap[record.gradingStatus]?.type" size="small">
                  {{ gradingStatusMap[record.gradingStatus]?.label }}
                </el-tag>
              </div>
              <div class="record-stem">{{ record.stem }}</div>
              <div class="record-correct">正确答案：{{ record.correctAnswer }}</div>
              <div class="record-answer">学生答案：{{ record.answerContent || '未作答' }}</div>

              <template v-if="isObjectiveQuestion(record.questionType)">
                <div class="record-result">
                  <el-tag :type="record.isCorrect ? 'success' : 'danger'" size="small">
                    {{ record.isCorrect ? '正确' : '错误' }}
                  </el-tag>
                  <span>得分：{{ record.score }}</span>
                </div>
              </template>

              <template v-else>
                <div class="grading-form">
                  <div class="grading-row">
                    <span>打分：</span>
                    <el-input-number
                      :model-value="gradingMap.get(record.recordId)?.score || 0"
                      :min="0"
                      :max="record.questionScore"
                      size="small"
                      style="width: 120px"
                      @change="(val: number | undefined) => {
                        const g = gradingMap.get(record.recordId)
                        if (g) g.score = val ?? 0
                      }"
                    />
                    <span style="margin-left: 8px">/ {{ record.questionScore }}</span>
                  </div>
                  <div class="grading-row">
                    <span>评语：</span>
                    <el-input
                      :model-value="gradingMap.get(record.recordId)?.comment || ''"
                      size="small"
                      style="flex: 1"
                      placeholder="可选评语"
                      @input="(val: string) => {
                        const g = gradingMap.get(record.recordId)
                        if (g) g.comment = val
                      }"
                    />
                  </div>
                  <el-button
                    size="small"
                    type="primary"
                    :disabled="record.gradingStatus === 'GRADED'"
                    @click="handleGrade(record.recordId)"
                  >
                    {{ record.gradingStatus === 'GRADED' ? '已批改' : '批改' }}
                  </el-button>
                </div>
              </template>
            </div>
          </div>
        </template>
      </div>
    </el-drawer>
  </div>
</template>

<style scoped lang="scss">
.card-header {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stats-cards {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.stat-card {
  flex: 1;
  text-align: center;

  .stat-value {
    font-size: 28px;
    font-weight: 700;
    color: #409eff;
  }

  .stat-label {
    font-size: 13px;
    color: #909399;
    margin-top: 4px;
  }
}

.score-stats {
  display: flex;
  gap: 16px;
}

.score-card {
  flex: 1;
  text-align: center;

  .score-value {
    font-size: 24px;
    font-weight: 600;
    color: #67c23a;
  }

  .score-label {
    font-size: 13px;
    color: #909399;
    margin-top: 4px;
  }
}

.sheet-header {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.records-list {
  .record-item {
    padding: 12px;
    border: 1px solid #ebeef5;
    border-radius: 6px;
    margin-bottom: 12px;
  }

  .record-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;

    .record-index {
      font-weight: 600;
    }

    .record-score {
      font-size: 13px;
      color: #909399;
      margin-left: auto;
    }
  }

  .record-stem {
    font-size: 14px;
    color: #303133;
    margin-bottom: 6px;
  }

  .record-correct {
    font-size: 13px;
    color: #67c23a;
    margin-bottom: 4px;
  }

  .record-answer {
    font-size: 13px;
    color: #606266;
    margin-bottom: 8px;
  }

  .record-result {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .grading-form {
    .grading-row {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;
    }
  }
}
</style>
