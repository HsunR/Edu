<script setup>
import { ElLoading, ElMessage } from 'element-plus'
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router'
import { Search, UploadFilled } from '@element-plus/icons-vue'
import moment from 'moment'
const route = useRoute()
const router = useRouter()

// 查询
const queryParams = ref({
  current: 1,
  pagesize: 10,
  questionType: [],
  name: ''
})
const total = ref(0)
const pagesize = ref(0)
const formLabelWidth = ref("50px")
// 存储列表数据
const list = ref([])
// 选中项
const multipleSelection = ref([])
const formData = ref({
  name: ''
})

onMounted(() => {
  // getList()
})
// 获取列表
const getList = async () => {
  const { data } = await getList(queryParams.value)
  console.log('获取项目列表')
  console.log(data.data)
  list.value = data.data?.records
  total.value = Number(data.data?.total)
  pagesize.value = Number(data.data?.size)
}
// 切换页码
const changePage = (newPage) => {
  queryParams.value.current = newPage
  getList()
}

const submitQuery = () => {
  formData.value?.validate((valid) => {
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
  // if (list.value.length === 1 && queryParams.value.page > 1) queryParams.value.page--
  getList()
  ElMessage.success('删除成功')
}

const handleSelectionChange = (val) => {
  multipleSelection.value = val.map(item => item.id)
}

// 下载
const downloadacademicPaper = async (supportiveMaterial) => {
  try {
    const response = await downloadAcademicPaper(supportiveMaterial)

    const currentDate = moment().format('YYYYMMDD')
    const originalFileName = supportiveMaterial
    const newFileName = `${currentDate}_${originalFileName}`

    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', newFileName)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (err) {
    console.error('下载失败:', err)
  }
}
// 导入
const handleChange = (file) => {
  const fileType = file.name.substring(file.name.lastIndexOf('.') + 1)

  if (fileType === 'xlsx' || fileType === 'xls') {
    importFile(file.raw)
  } else {
    ElMessage.warning('请上传xlsx或者xls格式的文件！')
  }
}
const importFile = async (file) => {
  const loading = ElLoading.service({
    lock: true,
    text: '正在导入...',
    background: 'rgba(0, 0, 0, 0.7)'
  })
  try {
    const data = new FormData()
    data.append('file', file)
    // const res = await uploadExcel(data)
    // console.log(res)
    ElMessage.success('导入成功！')
    window.location.reload()
  } catch (error) {
    ElMessage.error('导入失败！')
    console.error('导入失败:', error)
  } finally {
    loading.close()
  }
}

// 导出
const exportToWord = () => {

}

const handlePublish = (id) => {

}
const handleEdit = (id) => {

}
</script>

<template>
  <div class="JobLibrary">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-page-header :icon="ArrowLeft" @back="router.back">
            <template #content>
              <div class="flex items-center">
                <span class="text-large font-600 mr-3"> 作业库 </span>
              </div>
            </template>
          </el-page-header>

          <div class="header-buttons">
            <el-button class="gradient-btn" style="color: #fff; margin-top: 0px;"
              @click="router.push(`/course/create-exam/${1}`)">新建作业</el-button>
            <el-upload class="upload-demo" action="#" :on-change="handleChange" :show-file-list="false"
              :auto-upload="false">
              <el-button :icon="UploadFilled" type="warning" plain>导入作业</el-button>
            </el-upload>
            <el-button @click="exportToWord">导出作业</el-button>

            <!-- 查询 -->
            <div class="form" style="margin-left: 580px;">
              <el-form ref="formData" :model="queryParams" size="large" label-width="80px">
                <el-form-item prop="name" label-width="80px">
                  <el-input v-model="queryParams.name" style="max-width: 100vh;" placeholder="搜索">
                    <template #append>
                      <el-button :icon="Search" @click="submitQuery" />
                    </template>
                  </el-input>
                </el-form-item>
              </el-form>
            </div>
          </div>

        </div>
      </template>

      <!-- 表格 -->
      <el-table ref="exportContent" :data="list" height="500" border style="width: 100%"
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="achievementName" label="文件名" width="220" align="center" />
        <el-table-column prop="achievementType" label="题量" width="130" align="center" />
        <el-table-column prop="achievementType" label="总分" width="130" align="center" />
        <el-table-column prop="note" label="创建者" width="200" align="center" />
        <el-table-column prop="time" label="创建日期" width="200" align="center">
          <template #default="scope">{{ scope.row.date }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="150px" align="center" fixed="right">
          <template v-slot="{ row }">
            <el-button size="mini" type="text" @click="handlePublish(row.id)">发布</el-button>
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
.JobLibrary{
  margin: 20px;
  border-radius: 10px;
}
</style>
