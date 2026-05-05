<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, View } from '@element-plus/icons-vue'
import moment from 'moment'
import {
  getExamList,
  createExam,
  deleteExam,
  createPaper,
  publishPaper,
  addPaperQuestions
} from '@/api/exam/index'
import { getCourseClasses } from '@/api/course/course'
import type { ExamVO, ExamCreateRequest, PaperVO, QuestionItem } from '@/api/exam/types'
import type { ClassVO } from '@/api/course/types'
import { ExamType, ExamStatus } from '@/types/enums'
import type { FormInstance, FormRules } from 'element-plus'
import QuestionSelector from '../PaperManagement/QuestionSelector.vue'

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
const createForm = reactive({
  examName: '',
  classId: '',
  startTime: '',
  endTime: '',
  durationMinutes: undefined as number | undefined,
  allowLateSubmit: false
})
const createRules = reactive<FormRules>({
  examName: [{ required: true, message: '请输入作业名称', trigger: 'blur' }],
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }]
})

const classes = ref<ClassVO[]>([])

const questionSelectorVisible = ref(false)
const selectedQuestions = ref<QuestionItem[]>([])
const creating = ref(false)

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
      examType: ExamType.Homework,
      status: examStatusFilter.value,
      keyword: examKeyword.value || undefined
    })
    exams.value = result.records
    examTotal.value = result.total
  } finally {
    loading.value = false
  }
}

async function loadClasses() {
  try {
    classes.value = await getCourseClasses(courseId)
  } catch {
    // ignore
  }
}

function openCreateDialog() {
  createForm.examName = ''
  createForm.classId = ''
  createForm.startTime = ''
  createForm.endTime = ''
  createForm.durationMinutes = undefined
  createForm.allowLateSubmit = false
  selectedQuestions.value = []
  createDialogVisible.value = true
  loadClasses()
}

function openQuestionSelector() {
  questionSelectorVisible.value = true
}

function handleQuestionsSelected(items: QuestionItem[]) {
  selectedQuestions.value = items
}

function removeSelectedQuestion(idx: number) {
  selectedQuestions.value.splice(idx, 1)
}

function toOffsetDateTime(value: string): string {
  if (!value) return value
  return moment(value).format('YYYY-MM-DDTHH:mm:ss+08:00')
}

async function handleCreate() {
  if (!createFormRef.value) return
  
  if (selectedQuestions.value.length === 0) {
    ElMessage.warning('请选择至少一道题目')
    return
  }

  await createFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    creating.value = true
    try {
      const paper = await createPaper({
        paperName: createForm.examName,
        courseId,
        sections: [{ index: 1, title: '作业题目' }]
      })
      
      await addPaperQuestions(paper.paperId, {
        questions: selectedQuestions.value.map((q, idx) => ({
          ...q,
          sectionIndex: 1
        }))
      })
      
      await publishPaper(paper.paperId)
      
      const exam = await createExam({
        examName: createForm.examName,
        paperId: paper.paperId,
        classId: createForm.classId,
        examType: ExamType.Homework,
        startTime: toOffsetDateTime(createForm.startTime) || moment().format('YYYY-MM-DDTHH:mm:ss+08:00'),
        endTime: toOffsetDateTime(createForm.endTime) || moment().add(7, 'days').format('YYYY-MM-DDTHH:mm:ss+08:00'),
        durationMinutes: createForm.durationMinutes,
        allowLateSubmit: createForm.allowLateSubmit
      })
      
      ElMessage.success('作业创建成功')
      createDialogVisible.value = false
      await loadExams()
    } catch (error) {
      const msg = error instanceof Error ? error.message : '创建失败'
      ElMessage.error(msg)
    } finally {
      creating.value = false
    }
  })
}

async function handleDelete(exam: ExamVO) {
  if (exam.status !== 'NOT_STARTED') {
    ElMessage.warning('仅未开始的作业可删除')
    return
  }
  try {
    await ElMessageBox.confirm('确认删除该作业吗？', '删除确认', {
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

const totalScore = computed(() => {
  return selectedQuestions.value.reduce((sum, q) => sum + q.score, 0)
})

onMounted(loadExams)
</script>

<template>
  <div class="homework-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/teaching' }">我教的课</el-breadcrumb-item>
            <el-breadcrumb-item>课程作业</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </template>

      <div class="toolbar">
        <div class="toolbar-left">
          <el-select v-model="examStatusFilter" placeholder="状态筛选" clearable size="small" style="width: 120px" @change="handleFilterSearch">
            <el-option label="未开始" :value="'NOT_STARTED'" />
            <el-option label="进行中" :value="'IN_PROGRESS'" />
            <el-option label="已结束" :value="'ENDED'" />
            <el-option label="已批阅" :value="'GRADED'" />
          </el-select>
          <el-input v-model="examKeyword" placeholder="搜索作业名称" clearable size="small" style="width: 200px" @keyup.enter="handleFilterSearch" @clear="handleFilterSearch" />
        </div>
        <el-button type="primary" :icon="Plus" @click="openCreateDialog">发布作业</el-button>
      </div>

      <el-table v-loading="loading" :data="exams" stripe>
        <el-table-column prop="examName" label="作业名称" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="examStatusMap[row.status]?.type || 'info'" size="small">
              {{ examStatusMap[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paperName" label="关联试卷" min-width="180" show-overflow-tooltip />
        <el-table-column label="截止时间" width="180">
          <template #default="{ row }">
            <span style="font-size: 13px">{{ row.endTime?.split('T')[0] || row.endTime?.split(' ')[0] }}</span>
          </template>
        </el-table-column>
        <el-table-column label="时长" width="100">
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

    <el-dialog v-model="createDialogVisible" title="发布作业" width="600px">
      <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="100px">
        <el-form-item label="作业名称" prop="examName">
          <el-input v-model="createForm.examName" placeholder="请输入作业名称" />
        </el-form-item>
        <el-form-item label="选择班级" prop="classId">
          <el-select v-model="createForm.classId" placeholder="请选择班级" style="width: 100%">
            <el-option v-for="c in classes" :key="c.classId" :label="c.className" :value="c.classId" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="createForm.startTime" type="datetime" placeholder="不填则立即开始" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="截止时间">
          <el-date-picker v-model="createForm.endTime" type="datetime" placeholder="不填则7天后截止" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="答题时长">
          <el-input-number v-model="createForm.durationMinutes" :min="1" placeholder="分钟" />
          <span style="margin-left: 8px; color: #909399; font-size: 13px">分钟，留空不限时</span>
        </el-form-item>
        <el-form-item label="允许迟交">
          <el-switch v-model="createForm.allowLateSubmit" />
        </el-form-item>
        
        <el-divider content-position="left">题目选择</el-divider>
        
        <el-form-item label="已选题目">
          <div class="question-select-area">
            <div v-if="selectedQuestions.length === 0" class="empty-hint">
              <span>暂未选择题目</span>
            </div>
            <div v-else class="selected-questions">
              <div class="selected-header">
                <span>共 <strong>{{ selectedQuestions.length }}</strong> 题，合计 <strong>{{ totalScore }}</strong> 分</span>
              </div>
              <div class="selected-list">
                <div v-for="(q, idx) in selectedQuestions" :key="q.questionId" class="selected-item">
                  <span class="item-index">{{ idx + 1 }}.</span>
                  <span class="item-id">ID: {{ q.questionId }}</span>
                  <el-input-number v-model="q.score" :min="1" size="small" style="width: 80px" />
                  <span class="item-score">分</span>
                  <el-button size="small" link type="danger" @click="removeSelectedQuestion(idx)">移除</el-button>
                </div>
              </div>
            </div>
            <el-button type="primary" plain size="small" style="margin-top: 8px" @click="openQuestionSelector">
              {{ selectedQuestions.length > 0 ? '继续选择' : '从题库选择' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="creating" :disabled="selectedQuestions.length === 0" @click="handleCreate">
          确认发布
        </el-button>
      </template>
    </el-dialog>

    <QuestionSelector
      v-model="questionSelectorVisible"
      :course-id="courseId"
      simple
      title="选择作业题目"
      @confirm="handleQuestionsSelected"
    />
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

.question-select-area {
  width: 100%;
  
  .empty-hint {
    color: #909399;
    font-size: 13px;
    padding: 8px 0;
  }
  
  .selected-questions {
    .selected-header {
      font-size: 13px;
      color: #606266;
      margin-bottom: 8px;
      
      strong {
        color: #409eff;
      }
    }
    
    .selected-list {
      max-height: 200px;
      overflow-y: auto;
      border: 1px solid #ebeef5;
      border-radius: 4px;
      padding: 8px;
    }
    
    .selected-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 4px 0;
      font-size: 13px;
      
      .item-index {
        color: #606266;
        font-weight: 500;
      }
      
      .item-id {
        color: #909399;
        font-size: 12px;
      }
      
      .item-score {
        color: #909399;
        font-size: 12px;
      }
    }
  }
}
</style>
