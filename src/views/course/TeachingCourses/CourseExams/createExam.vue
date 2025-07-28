<script setup lang="ts">
import { ArrowLeft } from '@element-plus/icons-vue'
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
  <div>
    <div class="box">
      <div class="card-header">
        <el-page-header :icon="ArrowLeft" @back="router.back">
          <template #content>
            <div class="flex items-center">
              <span class="text-large font-600 mr-3"> {{ type === '1' ? '新建作业' : '新建考试' }} </span>
            </div>
          </template>
          <template #extra>
            <div class="flex items-center">
              <el-button round @click="save" style="color: black;"> {{ type === '1' ? '保存作业' : '保存试卷' }}</el-button>
              <el-button type="primary" round class="ml-2" style="color: #fff;">继续出题</el-button>
            </div>
          </template>
        </el-page-header>
      </div>

      <div class="ExamName">
       {{ type === '1' ? '新建作业：' : '新建试卷：' }} <el-input v-model="ExamName" style="width: 80vh; margin-left: 10px;" placeholder="名字: "></el-input>
      </div>

      <div class="content">
        <!-- 左侧预览 -->
        <div class="left">
          <div class="head"><span class="">总题量：</span> <span>总分数：</span></div>
        </div>

        <div class="right">
          <!-- 题型部分 -->
          <div class="questiongType">
            <span style="vertical-align: middle; margin-right: 20px;">添加题目：</span>
            <el-button round @click="handleSingle">单选题</el-button>
            <el-button round @click="handleMultiple">多选题</el-button>
            <el-button round @click="handleFill">填空题</el-button>
            <el-button round @click="handleShortAnswer">简答题</el-button>
            <el-button round @click="handleJudgment">判断题</el-button>
            <el-button type="primary" round class="ml-2 gradient-btn"
              style="color: #fff; margin: 0 0 0 80px;">从题库选题</el-button>
          </div>

          <div class="questionPart">
            <div class="title">
              <span class="label" style="margin-top: 0;" v-if="questionForm.questionType === '1'">单选题：</span>
              <span class="label" style="margin-top: 0;" v-else-if="questionForm.questionType === '2'">多选题：</span>
              <span class="label" style="margin-top: 0;" v-else-if="questionForm.questionType === '3'">填空题：</span>
              <span class="label" style="margin-top: 0;" v-else-if="questionForm.questionType === '4'">简答题：</span>
              <span class="label" style="margin-top: 0;" v-else-if="questionForm.questionType === '5'">判断题：</span>
              <el-input v-model="questionForm.score" style="width: 20vh;" placeholder="分值: "></el-input>
            </div>
            <div class="title">
              <span class="label">题目：</span>
              <el-input v-model="questionForm.title" style="width: 70vh;" type="textarea" resize="none"
                :rows="4"></el-input>
            </div>

            <div class="question">
              <!-- 单选题 -->
              <div class="single" v-if="questionForm.questionType === '1'">
                <el-radio-group v-model="questionForm.answer" class="option" text-color="#626aef"
                  fill="rgb(239, 240, 253)">
                  <div v-for="i in options1" :key="i.id">
                    <el-radio-button :label=i.label :value=i.label>{{ i.label }}</el-radio-button>
                    <el-input v-model="i.text" class="option-input" style="width: 70vh;"></el-input>
                  </div>
                </el-radio-group>
              </div>

              <!-- 多选题 -->
              <div class="mutiple" v-else-if="questionForm.questionType === '2'">
                <el-checkbox-group v-model="mutipleAnswer" text-color="#626aef" fill="rgb(239, 240, 253)"
                  @change="mutipleChange">
                  <div v-for="i in options2" :key="i.id" style="margin-bottom: 20px;">
                    <el-checkbox-button :value="i.label"> {{ i.label }} </el-checkbox-button>
                    <el-input v-model="i.text" class="option-input" style="width: 70vh; margin-left: 17px;"></el-input>
                  </div>
                </el-checkbox-group>
              </div>

              <!-- 填空题 -->
              <div class="mutiple" v-else-if="questionForm.questionType === '3'">
                <div v-for="i in options3" :key="i.id">
                  <span>{{ i.label }}</span>
                  <el-input v-model="i.text" class="option-input" style="width: 70vh; margin-left: 17px;"
                    placeholder="输入答案"></el-input>
                </div>
              </div>

              <!-- 简答题 -->
              <div class="mutiple" v-else-if="questionForm.questionType === '4'">
                <el-input v-model="questionForm.answer" style="width: 70vh;" type="textarea" resize="none" :rows="4"
                  placeholder="输入答案"></el-input>
              </div>

              <!-- 判断题 -->
              <div class="mutiple" v-else-if="questionForm.questionType === '5'">
                <el-radio-group v-model="questionForm.answer" style="margin-left: -10px;" text-color="#626aef"
                  fill="rgb(239, 240, 253)">
                  <el-radio-button label="对" :value='1'></el-radio-button>
                  <el-radio-button label="错" :value='2'></el-radio-button>
                </el-radio-group>
              </div>
            </div>

            <div class="analysis title" style="margin-top: 30px;">
              <span class="label">解析：</span>
              <el-input v-model="questionForm.analysis" style="width: 70vh;" type="textarea" resize="none" :rows="4"
                placeholder="输入答案解析"></el-input>
            </div>
          </div>
        </div>

      </div>


    </div>
  </div>
</template>

<style scoped lang="scss">
.card-header {
  background-color: #fff;
  padding: 15px;
}

.ExamName{
  background-color: #fff;
  padding: 20px;
  margin: 20px 120px 0 90px;
  text-align: center;
  border-radius: 5px;
}

.questiongType {
  background-color: #fff;
  margin: 10px 40px;
  padding: 25px;
  border-radius: 5px;
}

.content {
  margin: 20px 80px;
  display: flex;
  height: 110vh;


  .left {
    flex: 1;
    width: 50vh;
    background-color: #fff;
    border-radius: 5px;
    padding: 20px;
    margin: 5px;

    .head{
      display: flex;

      span{
        flex: 1;
      }
    }
  }

  .right {
    flex: 3;
  }
}

.questionPart {
  background-color: #fff;
  padding: 5vh 10vh;
  border-radius: 5px;
  margin: 10px 40px;
}

.el-radio-button {
  margin: 10px;
  border: 1px solid #ccc;
}

.title {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.label {
  margin-top: 3vh;
  line-height: 40px;
  white-space: nowrap;
  font-weight: normal;
  font-size: medium;
}

.option {
  display: flex;
  align-items: center;
}
</style>
