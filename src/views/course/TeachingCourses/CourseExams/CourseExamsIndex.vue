<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, View } from '@element-plus/icons-vue'
import moment from 'moment'
import {
  getExamList,
  createExam,
  updateExam,
  deleteExam
} from '@/api/exam/index'
import { getPaperList } from '@/api/exam/index'
import { getCourseClasses } from '@/api/course/course'
import type { ExamVO, ExamCreateRequest, ExamUpdateRequest, PaperVO } from '@/api/exam/types'
import type { ClassVO } from '@/api/course/types'
import { ExamType, ExamStatus, PaperStatus } from '@/types/enums'
import type { FormInstance, FormRules } from 'element-plus'

const route = useRoute()
const router = useRouter()
const courseId = route.params.id as string

const loading = ref(false)
const exams = ref<ExamVO[]>([])
const examTotal = ref(0)
const examPage = ref(1)
const examPageSize = ref(10)
const examStatusFilter = ref<ExamStatus | undefined>(undefined)
const examKeyword = ref('')

const createDialogVisible = ref(false)
const createFormRef = ref<FormInstance>()
const createForm = reactive<ExamCreateRequest>({
  examName: '',
  paperId: '',
  classId: '',
  examType: ExamType.Exam,
  startTime: '',
  endTime: '',
  durationMinutes: undefined,
  allowLateSubmit: false
})
const createRules = reactive<FormRules>({
  examName: [{ required: true, message: '请输入考试名称', trigger: 'blur' }],
  paperId: [{ required: true, message: '请选择试卷', trigger: 'change' }],
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }]
})

const publishedPapers = ref<PaperVO[]>([])
const classes = ref<ClassVO[]>([])

const examStatusMap: Record<string, { label: string; type: string }> = {
  'NOT_STARTED': { label: '未开始', type: 'info' },
  'IN_PROGRESS': { label: '进行中', type: 'success' },
  'ENDED': { label: '已结束', type: 'warning' },
  'GRADED': { label: '已批阅', type: 'success' }
}

async function loadExams() {
  loading.value = true
  try {
    const result = await getExamList({
      current: examPage.value,
      pageSize: examPageSize.value,
      courseId,
      examType: ExamType.Exam,
      status: examStatusFilter.value,
      keyword: examKeyword.value || undefined
    })
    console.log('Exam list result:', result)
    exams.value = result.records
    examTotal.value = result.total
  } finally {
    loading.value = false
  }
}

async function loadFormData() {
  try {
    const [paperResult, classResult] = await Promise.all([
      getPaperList({ current: 1, pageSize: 100, courseId, status: PaperStatus.Published }),
      getCourseClasses(courseId)
    ])
    publishedPapers.value = paperResult.records
    classes.value = classResult
  } catch {
    // ignore
  }
}

function openCreateDialog() {
  createForm.examName = ''
  createForm.paperId = 0
  createForm.classId = 0
  createForm.examType = ExamType.Exam
  createForm.startTime = ''
  createForm.endTime = ''
  createForm.durationMinutes = undefined
  createForm.allowLateSubmit = false
  createDialogVisible.value = true
  loadFormData()
}

function toOffsetDateTime(value: string): string {
  if (!value) return value
  return moment(value).format('YYYY-MM-DDTHH:mm:ss+08:00')
}

async function handleCreate() {
  if (!createFormRef.value) return
  await createFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      const payload = {
        ...createForm,
        startTime: toOffsetDateTime(createForm.startTime),
        endTime: toOffsetDateTime(createForm.endTime)
      }
      await createExam(payload)
      ElMessage.success('创建考试成功')
      createDialogVisible.value = false
      await loadExams()
    } catch (error) {
      const msg = error instanceof Error ? error.message : '创建失败'
      ElMessage.error(msg)
    }
  })
}

async function handleDelete(exam: ExamVO) {
  if (exam.status !== 'NOT_STARTED') {
    ElMessage.warning('仅未开始的考试可删除')
    return
  }
  try {
    await ElMessageBox.confirm('确认删除该考试吗？', '删除确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteExam(exam.examId)
    ElMessage.success('删除成功')
    await loadExams()
  } catch {
    // cancelled
  }
}

function goToStats(examId: string) {
  router.push(`/course/teaching/${courseId}/exam-stats/${examId}`)
}

function handlePageChange(page: number) {
  examPage.value = page
  loadExams()
}

function handleFilterSearch() {
  examPage.value = 1
  loadExams()
}

onMounted(loadExams)
</script>

<template>
  <div class="exam-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/teaching' }">我教的课</el-breadcrumb-item>
            <el-breadcrumb-item>考试管理</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </template>

      <div class="toolbar">
        <div class="toolbar-left">
          <el-select v-model="examStatusFilter" placeholder="状态" clearable size="small" style="width: 120px" @change="handleFilterSearch">
            <el-option label="未开始" :value="'NOT_STARTED'" />
            <el-option label="进行中" :value="'IN_PROGRESS'" />
            <el-option label="已结束" :value="'ENDED'" />
            <el-option label="已批阅" :value="'GRADED'" />
          </el-select>
          <el-input v-model="examKeyword" placeholder="搜索考试名称" clearable size="small" style="width: 200px" @keyup.enter="handleFilterSearch" @clear="handleFilterSearch" />
        </div>
        <el-button type="primary" :icon="Plus" @click="openCreateDialog">创建考试</el-button>
      </div>

      <el-table v-loading="loading" :data="exams" stripe style="width: 100%">
        <el-table-column prop="examName" label="考试名称" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="examStatusMap[row.status]?.type || 'info'" size="small">
              {{ examStatusMap[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paperName" label="试卷" min-width="180" show-overflow-tooltip />
        <el-table-column label="时间" width="200">
          <template #default="{ row }">
            <div style="font-size: 12px;">
              <div>{{ row.startTime?.split(' ')[0] }}</div>
              <div style="color: #909399">~ {{ row.endTime?.split(' ')[0] }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="时长" width="90">
          <template #default="{ row }">
            {{ row.durationMinutes ? row.durationMinutes + '分钟' : '不限' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link :icon="View" @click="goToStats(row.examId)">统计</el-button>
            <el-button v-if="row.status === 'NOT_STARTED'" size="small" link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="examTotal > examPageSize"
        v-model:current-page="examPage"
        :total="examTotal"
        :page-size="examPageSize"
        layout="prev, pager, next"
        style="margin-top: 12px"
        @current-change="handlePageChange"
      />
    </el-card>

    <el-dialog v-model="createDialogVisible" title="创建考试" width="550px">
      <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="100px">
        <el-form-item label="考试名称" prop="examName">
          <el-input v-model="createForm.examName" placeholder="请输入考试名称" />
        </el-form-item>
        <el-form-item label="选择试卷" prop="paperId">
          <el-select v-model="createForm.paperId" placeholder="请选择已发布试卷" style="width: 100%">
            <el-option
              v-for="p in publishedPapers"
              :key="p.paperId"
              :label="`${p.paperName} (${p.questionCount}题/${p.totalScore}分)`"
              :value="p.paperId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择班级" prop="classId">
          <el-select v-model="createForm.classId" placeholder="请选择班级" style="width: 100%">
            <el-option v-for="c in classes" :key="c.classId" :label="c.className" :value="c.classId" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" required>
          <el-date-picker v-model="createForm.startTime" type="datetime" placeholder="选择开始时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间" required>
          <el-date-picker v-model="createForm.endTime" type="datetime" placeholder="选择结束时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="答题时长">
          <el-input-number v-model="createForm.durationMinutes" :min="1" placeholder="分钟，留空不限时" />
          <span style="margin-left: 8px; color: #909399">分钟，留空不限时</span>
        </el-form-item>
        <el-form-item label="允许迟交">
          <el-switch v-model="createForm.allowLateSubmit" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确认创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.toolbar-left {
  display: flex;
  gap: 8px;
  align-items: center;
}
</style>
