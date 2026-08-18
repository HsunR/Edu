<script setup lang="ts">
import { 
  ArrowLeft,
  Document,
  Check,
  Plus,
  DataLine,
  TrendCharts,
  Box,
  Tickets,
  List,
  Edit,
  DocumentCopy,
  CircleCheck,
  FolderOpened,
  EditPen,
  CircleClose
} from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router';
const router = useRouter()
const route = useRoute()
import { onMounted, ref } from 'vue'
import { reactive } from 'vue';

// 1: 作业, 2: 考试
const type = ref()
onMounted(() => {
  console.log(route.params.id)
  type.value = route.params.id
})

const ExamName = ref('')
const questionType = ref('1')
const changeQuestionType = (val) => {
  console.log(val)
}

const questionForm = ref({
  questionType: '1',
  score: '',
  title: '',
  answer: '',
  analysis: '',
  option: '',
  desc: ''
})

// 单选
const handleSingle = () => {
  questionForm.value.questionType = '1'
}
const singleRadio = ref('')
const options1 = reactive([
  { id: 'A', label: 'A', text: '' },
  { id: 'B', label: 'B', text: '' },
  { id: 'C', label: 'C', text: '' },
  { id: 'D', label: 'D', text: '' }
])

// 多选
const handleMultiple = () => {
  questionForm.value.questionType = '2'
}
const options2 = reactive([
  { id: 'A', label: 'A', text: '' },
  { id: 'B', label: 'B', text: '' },
  { id: 'C', label: 'C', text: '' },
  { id: 'D', label: 'D', text: '' }
])
const mutipleAnswer = ref([])
const mutipleChange = (val) => {
  console.log(val)
  questionForm.value.answer = val
}

// 填空
const handleFill = () => {
  questionForm.value.questionType = '3'
}
const options3 = reactive([
  { id: '1', label: '第一空', text: '' },
])

// 简答题
const handleShortAnswer = () => {
  questionForm.value.questionType = '4'
}

// 判断题
const handleJudgment = () => {
  questionForm.value.questionType = '5'
}


// 保存
const save = () => {
  if (questionForm.value.questionType === '1') {

  } else if (questionForm.value.questionType === '2') {

  } else if (questionForm.value.questionType === '3') {
    questionForm.value.answer = options3.map(item => {
      return item.label + "：" + item.text
    }).join(";")
  } else if (questionForm.value.questionType === '4') {

  } else if (questionForm.value.questionType === '5') {

  }
  console.log(questionForm.value)
  resetForm()
  router.back()
}

const resetForm = () => {
  questionForm.value = {
    questionType: '1',
    score: '',
    title: '',
    answer: '',
    analysis: '',
    option: '',
    desc: ''
  }
}
</script>

<template>
  <div class="create-exam-page">
    <div class="container">
      <div class="header-card">
        <el-page-header :icon="ArrowLeft" @back="router.back" class="page-header">
          <template #content>
            <div class="header-title">
              <el-icon :size="24" color="#626aef"><Document /></el-icon>
              <span class="title-text">{{ type === '1' ? '新建作业' : '新建考试' }}</span>
            </div>
          </template>
          <template #extra>
            <div class="header-actions">
              <el-button @click="save" class="btn-save">
                <el-icon><Check /></el-icon>
                {{ type === '1' ? '保存作业' : '保存试卷' }}
              </el-button>
              <el-button type="primary" class="btn-continue">
                <el-icon><Plus /></el-icon>
                继续出题
              </el-button>
            </div>
          </template>
        </el-page-header>
      </div>

      <div class="exam-name-card">
        <div class="exam-name-wrapper">
          <span class="exam-name-label">{{ type === '1' ? '📝 作业名称' : '📋 试卷名称' }}</span>
          <el-input v-model="ExamName" class="exam-name-input" placeholder="请输入名称" size="large"></el-input>
        </div>
      </div>

      <div class="main-content">
        <div class="preview-panel">
          <div class="panel-header">
            <el-icon><DataLine /></el-icon>
            <span>试卷预览</span>
          </div>
          <div class="panel-content">
            <div class="stat-item">
              <div class="stat-icon total-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-value">0</span>
                <span class="stat-label">总题量</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon score-icon">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-value">0</span>
                <span class="stat-label">总分数</span>
              </div>
            </div>
            <div class="divider"></div>
            <div class="empty-tip">
              <el-empty description="添加题目后将在这里显示" :image-size="80" />
            </div>
          </div>
        </div>

        <div class="editor-panel">
          <div class="type-selector-card">
            <div class="type-selector-header">
              <el-icon><Box /></el-icon>
              <span>选择题型</span>
            </div>
            <div class="type-buttons">
              <el-button 
                :type="questionForm.questionType === '1' ? 'primary' : ''"
                @click="handleSingle" 
                class="type-btn"
              >
                <el-icon><Tickets /></el-icon>
                单选题
              </el-button>
              <el-button 
                :type="questionForm.questionType === '2' ? 'primary' : ''"
                @click="handleMultiple" 
                class="type-btn"
              >
                <el-icon><List /></el-icon>
                多选题
              </el-button>
              <el-button 
                :type="questionForm.questionType === '3' ? 'primary' : ''"
                @click="handleFill" 
                class="type-btn"
              >
                <el-icon><Edit /></el-icon>
                填空题
              </el-button>
              <el-button 
                :type="questionForm.questionType === '4' ? 'primary' : ''"
                @click="handleShortAnswer" 
                class="type-btn"
              >
                <el-icon><DocumentCopy /></el-icon>
                简答题
              </el-button>
              <el-button 
                :type="questionForm.questionType === '5' ? 'primary' : ''"
                @click="handleJudgment" 
                class="type-btn"
              >
                <el-icon><CircleCheck /></el-icon>
                判断题
              </el-button>
              <el-button type="primary" class="btn-library">
                <el-icon><FolderOpened /></el-icon>
                从题库选题
              </el-button>
            </div>
          </div>

          <div class="question-editor-card">
            <div class="editor-header">
              <el-icon><EditPen /></el-icon>
              <span>编辑题目</span>
            </div>
            
            <div class="form-section">
              <div class="form-row">
                <div class="form-item type-label">
                  <span class="form-label">当前题型：</span>
                  <el-tag type="primary" effect="dark">
                    {{ 
                      questionForm.questionType === '1' ? '单选题' :
                      questionForm.questionType === '2' ? '多选题' :
                      questionForm.questionType === '3' ? '填空题' :
                      questionForm.questionType === '4' ? '简答题' : '判断题'
                    }}
                  </el-tag>
                </div>
                <div class="form-item score-input">
                  <span class="form-label">分值：</span>
                  <el-input v-model="questionForm.score" placeholder="请输入分值" style="width: 150px;"></el-input>
                </div>
              </div>

              <div class="form-item">
                <span class="form-label">题目内容：</span>
                <el-input 
                  v-model="questionForm.title" 
                  type="textarea" 
                  resize="none"
                  :rows="4"
                  placeholder="请输入题目内容"
                  class="textarea-input"
                ></el-input>
              </div>

              <div class="form-item">
                <span class="form-label">答案设置：</span>
                
                <div class="answer-section">
                  <div class="single-options" v-if="questionForm.questionType === '1'">
                    <el-radio-group v-model="questionForm.answer">
                      <div v-for="i in options1" :key="i.id" class="option-row">
                        <el-radio :label="i.label" class="option-radio">
                          <span class="option-letter">{{ i.label }}</span>
                        </el-radio>
                        <el-input v-model="i.text" class="option-text" placeholder="请输入选项内容"></el-input>
                      </div>
                    </el-radio-group>
                  </div>

                  <div class="multiple-options" v-else-if="questionForm.questionType === '2'">
                    <el-checkbox-group v-model="mutipleAnswer" @change="mutipleChange">
                      <div v-for="i in options2" :key="i.id" class="option-row">
                        <el-checkbox :value="i.label" class="option-checkbox">
                          <span class="option-letter">{{ i.label }}</span>
                        </el-checkbox>
                        <el-input v-model="i.text" class="option-text" placeholder="请输入选项内容"></el-input>
                      </div>
                    </el-checkbox-group>
                  </div>

                  <div class="fill-options" v-else-if="questionForm.questionType === '3'">
                    <div v-for="i in options3" :key="i.id" class="option-row">
                      <el-tag class="fill-tag">{{ i.label }}</el-tag>
                      <el-input v-model="i.text" class="option-text" placeholder="请输入答案"></el-input>
                    </div>
                  </div>

                  <div class="short-answer" v-else-if="questionForm.questionType === '4'">
                    <el-input 
                      v-model="questionForm.answer" 
                      type="textarea" 
                      resize="none" 
                      :rows="4"
                      placeholder="请输入参考答案"
                      class="textarea-input"
                    ></el-input>
                  </div>

                  <div class="judgment-options" v-else-if="questionForm.questionType === '5'">
                    <el-radio-group v-model="questionForm.answer">
                      <el-radio :label="1" class="judgment-radio">
                        <el-icon><CircleCheck /></el-icon>
                        对
                      </el-radio>
                      <el-radio :label="2" class="judgment-radio">
                        <el-icon><CircleClose /></el-icon>
                        错
                      </el-radio>
                    </el-radio-group>
                  </div>
                </div>
              </div>

              <div class="form-item">
                <span class="form-label">题目解析：</span>
                <el-input 
                  v-model="questionForm.analysis" 
                  type="textarea" 
                  resize="none" 
                  :rows="4"
                  placeholder="请输入题目解析（选填）"
                  class="textarea-input"
                ></el-input>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.create-exam-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%);
  padding: 20px;
}

.container {
  max-width: 1600px;
  margin: 0 auto;
}

.header-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  margin-bottom: 20px;
}

.page-header {
  :deep(.el-page-header__content) {
    display: flex;
    align-items: center;
  }
}

.header-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-text {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.btn-save {
  border-color: #626aef;
  color: #626aef;
  
  &:hover {
    background: rgba(98, 106, 239, 0.05);
  }
}

.btn-continue {
  background: linear-gradient(135deg, #626aef 0%, #8b5cf6 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(98, 106, 239, 0.3);
  
  &:hover {
    box-shadow: 0 6px 16px rgba(98, 106, 239, 0.4);
    transform: translateY(-1px);
  }
}

.exam-name-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  margin-bottom: 20px;
}

.exam-name-wrapper {
  display: flex;
  align-items: center;
  gap: 16px;
}

.exam-name-label {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  white-space: nowrap;
}

.exam-name-input {
  flex: 1;
  max-width: 600px;
  
  :deep(.el-input__wrapper) {
    border-radius: 8px;
    box-shadow: 0 0 0 1px #e4e7ed inset;
    transition: all 0.3s;
    
    &:hover {
      box-shadow: 0 0 0 1px #c0c4cc inset;
    }
    
    &.is-focus {
      box-shadow: 0 0 0 1px #626aef inset !important;
    }
  }
}

.main-content {
  display: flex;
  gap: 20px;
}

.preview-panel {
  width: 320px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 20px 24px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  border-bottom: 1px solid #f0f2f5;
  background: linear-gradient(135deg, #fafbff 0%, #f5f7fa 100%);
}

.panel-content {
  padding: 24px;
  flex: 1;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: linear-gradient(135deg, #fafbff 0%, #f5f7fa 100%);
  border-radius: 10px;
  margin-bottom: 12px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  
  &.total-icon {
    background: linear-gradient(135deg, #626aef 0%, #8b5cf6 100%);
    color: #fff;
  }
  
  &.score-icon {
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    color: #fff;
  }
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
}

.divider {
  height: 1px;
  background: #f0f2f5;
  margin: 20px 0;
}

.empty-tip {
  text-align: center;
}

.editor-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.type-selector-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.type-selector-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}

.type-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.type-btn {
  border-radius: 8px;
  padding: 10px 18px;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.btn-library {
  margin-left: auto;
  border-radius: 8px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
  
  &:hover {
    box-shadow: 0 6px 16px rgba(16, 185, 129, 0.4);
    transform: translateY(-1px);
  }
}

.question-editor-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.editor-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f2f5;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-row {
  display: flex;
  gap: 32px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 10px;
  
  &.type-label {
    flex-direction: row;
    align-items: center;
    gap: 10px;
  }
  
  &.score-input {
    flex-direction: row;
    align-items: center;
    gap: 10px;
  }
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.textarea-input {
  :deep(.el-textarea__inner) {
    border-radius: 8px;
    border-color: #e4e7ed;
    transition: all 0.3s;
    
    &:hover {
      border-color: #c0c4cc;
    }
    
    &:focus {
      border-color: #626aef;
      box-shadow: 0 0 0 3px rgba(98, 106, 239, 0.1);
    }
  }
}

.answer-section {
  padding: 16px;
  background: #fafbff;
  border-radius: 10px;
  border: 1px solid #f0f2f5;
}

.option-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.option-radio,
.option-checkbox {
  margin-right: 0;
}

.option-letter {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #626aef 0%, #8b5cf6 100%);
  color: #fff;
  border-radius: 6px;
  font-weight: 600;
  font-size: 13px;
}

.option-text {
  flex: 1;
  
  :deep(.el-input__wrapper) {
    border-radius: 8px;
  }
}

.fill-tag {
  background: linear-gradient(135deg, #f472b6 0%, #ec4899 100%);
  border: none;
  color: #fff;
}

.judgment-options {
  display: flex;
  gap: 20px;
}

.judgment-radio {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 24px;
  background: #fff;
  border: 2px solid #e4e7ed;
  border-radius: 10px;
  transition: all 0.3s;
  
  &:hover {
    border-color: #626aef;
  }
  
  :deep(.el-radio__label) {
    display: flex;
    align-items: center;
    gap: 6px;
    font-weight: 500;
  }
}
</style>
