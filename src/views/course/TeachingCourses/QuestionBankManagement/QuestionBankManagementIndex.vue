<script setup>
import { ElMessage } from "element-plus";
import { ref, reactive, onMounted } from "vue";
import { useRouter } from 'vue-router'
const router = useRouter()
// 查询
const queryParams = ref({
  current: 1,
  pagesize: 10,
  questionType: []
})
const total = ref(0)
const pagesize = ref(0)
const formLabelWidth = ref("50px")
// 存储列表数据
const list = ref([])
// 选中项
const multipleSelection = ref([])

const options = [
  {
    value: 'Option1',
    label: '单选题',
  },
  {
    value: 'Option2',
    label: '多选题',
  },
  {
    value: 'Option3',
    label: '填空题',
  },
  {
    value: 'Option4',
    label: '判断题',
  },
  {
    value: 'Option5',
    label: '简答题',
  },
  {
    value: 'Option6',
    label: '论述题',
  },
  {
    value: 'Option7',
    label: '计算题',
  },
  {
    value: 'Option8',
    label: '程序题',
  },
  {
    value: 'Option9',
    label: '其他',
  },
]

onMounted(() => {
  // getList()
})
// 获取列表
const getList = async () => {
  const { data } = await getList(queryParams.value)
  console.log('获取项目列表')
  console.log(data.data)
  list = data.data?.records
  total = Number(data.data?.total)
  pagesize = Number(data.data?.size)
}
// 切换页码
const changePage = (newPage) => {
  queryParams.value.current = newPage
  getList()
}

const submitQuery = () => {
  proxy.$refs.formData.validate(valid => {
    if (!valid) return
    // 提交表单 调用查询接口
    getList()
  })
}
const resetQueryForm = () => {
  formData.value?.resetFields()
  getList()
}
// 删除
const confirmDel = async (id) => {
  console.log('删除')
  // await delAchievementTransformation(id)
  if (list.value.length === 1 && queryParams.value.page > 1) queryParams.value.page--
  getList()
  ElMessage.success('删除成功')
}

const handleSelectionChange = (val) => {
  multipleSelection.value = val.map(item => item.id)
}
</script>

<template>
  <div >
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/TeachingCourses' }">我教的课</el-breadcrumb-item>
            <el-breadcrumb-item :to="{ path: '/course/TeachingCourses/CourseDetails/1005/QuestionBankManagementTeaching' }">题库管理</el-breadcrumb-item>
          </el-breadcrumb>
          <div class="header-buttons">
            <!-- <el-upload class="upload-demo" action="#" :on-change="handleChange" :show-file-list="false"
              :auto-upload="false">
              <el-button icon="el-icon-upload" type="warning" plain>导入</el-button>
            </el-upload> -->

            <el-button class="gradient-btn" @click="router.push('/course/createQuestion')">创建题目</el-button>
          </div>
          <!-- 查询 -->
          <div class="form">
            <el-form ref="formData" :model="queryParams" size="medium" label-width="80px">
              <el-row :gutter="18">
                <el-col :span="6">
                  <el-form-item label="题型" prop="achievementName" :label-width="formLabelWidth">
                    <el-select v-model="queryParams.questionType" multiple placeholder="请选择" style="width: 400px">
                      <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"
                        style="width: 100%;">
                        <div class="flex items-center">
                          <span>{{ item.label }}</span>
                        </div>
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item class="formButtons" size="large">
                <el-button type="primary" plain round @click="submitQuery">查询</el-button>
                <!-- <el-button round @click="resetQueryForm">重置</el-button> -->
              </el-form-item>
            </el-form>
          </div>

        </div>
      </template>

      <!-- 表格 -->
      <el-table ref="exportContent" :data="list" height="450" border style="width: 100%"
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" prop="index" label="序号" width="120" align="center" />
        <el-table-column prop="achievementName" label="题目" width="220" align="center" />
        <el-table-column prop="achievementType" label="题型" width="200" align="center" />
        <el-table-column prop="leader" label="难易" width="150" align="center" />
        <el-table-column prop="note" label="创建者" width="150" align="center" />
        <el-table-column prop="time" label="创建时间" width="150" align="center">
          <template #default="scope">{{ scope.row.date }}</template>
        </el-table-column>

        <el-table-column label="操作" min-width="120px" align="center" fixed="right">
          <template v-slot="{ row }">
            <el-button size="mini" type="text" @click="handleEdit(row.id)">编辑</el-button>
            <el-popconfirm title="确认删除该行数据吗？" @confirm="confirmDel(row.id)">
              <template #reference>
                <el-button style="margin-left: 10px;color: #F56C6C" size="mini" type="text">
                  删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-row style="height: 60px" align="middle" type="flex" justify="end">
        <el-pagination layout="total,prev, pager, next" :total="total" :current-page="queryParams.page"
          :page-size="queryParams.pagesize" @current-change="changePage" />
      </el-row>
    </el-card>
  </div>
</template>

<style scoped>
.gradient-btn{
  color: #fff;
  border-radius: 20px;
  height: 5vh;
  margin-bottom: 15px;
}

</style>
