<script setup>
import { CirclePlus, Setting, Search, MoreFilled, Grid } from '@element-plus/icons-vue'
import { ElLoading, ElMessage } from 'element-plus'
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()

// 查询
const queryParams = ref({
  current: 1,
  pagesize: 10,
  name: ''
})
const total = ref(0)
const pagesize = ref(0)

// 存储列表数据
const list = ref([])
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

const className = ref('')
const selectClass = (id) => {
  console.log(id)
  className.value = id
}

// 添加学生
const dialogFormVisible = ref(false)
const loading = ref(false)
// 对话框表单数据
const form = ref({
  'name': '',
  'id': '',
})
const formRef = ref()
const handleCancel = () => {
  dialogFormVisible.value = false
  resetForm()
}
const submitForm = async () => {
  try {
    loading.value = true

    console.log('提交表单数据:', JSON.parse(JSON.stringify(form)))

    const response = await addStudent(form)

    if (response.data.message === 'ok') {
      ElMessage.success('新增成功')
      dialogFormVisible.value = false
      // getList()
    } else {
      ElMessage.error(response.data.message || '新增失败')
    }
  } catch (error) {
    console.error('提交错误:', error)
    if (error !== 'validate') {
      ElMessage.error('新增失败')
    }
  } finally {
    loading.value = false
    // 只在成功提交后重置表单
    resetForm()
  }
}
const resetForm = () => {
  formRef.value?.resetFields()
  dialogFormVisible.value = false
  // getList()
}
</script>

<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/TeachingCourses' }">我教的课</el-breadcrumb-item>
            <el-breadcrumb-item>班级管理</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </template>

      <div class="content">
        <div class="left">
          <div class="button">
            <el-button :icon="CirclePlus" round>新建班级</el-button>
            <el-button :icon="Setting" round>班级管理</el-button>
          </div>
          <div class="search">
            <el-form ref="formData" :model="queryParams" size="large">
              <el-form-item prop="name" style="width: 100%;">
                <el-input v-model="queryParams.name" placeholder="搜索班级">
                  <template #append>
                    <el-button :icon="Search" @click="submitQuery" />
                  </template>
                </el-input>
              </el-form-item>
            </el-form>
          </div>
          <el-scrollbar height="65vh">
            <div v-for="item in 20" :key="item">
              <div class="scrollbar-demo-item">
                <div @click="selectClass(item)" style="cursor: pointer;">{{ item }}</div>
                <div class="more">
                  <el-dropdown>
                    <span class="el-dropdown-link" style="border: none; ">
                      <el-icon class="el-icon--right">
                        <MoreFilled />
                      </el-icon>
                    </span>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item>重命名</el-dropdown-item>
                        <el-dropdown-item>设置</el-dropdown-item>
                        <el-dropdown-item>删除</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>

              </div>
            </div>
          </el-scrollbar>
        </div>

        <div class="right">
          <div class="right-head" style="width:120vh;">
            <el-popover placement="top-start" :width="50" trigger="hover" content="邀请码">
              <template #reference>
                <el-button :icon="Grid" class="right-head-btn"></el-button>
              </template>
            </el-popover>
            <span style="margin-left: 20px;">{{ className }}</span>
          </div>

          <div class="right-students">
            <el-button class="gradient-btn" round style="color: #fff; margin-top: 20px;"
              @click="dialogFormVisible = true">添加学生</el-button>
          </div>

          <!-- 对话框 -->
          <el-dialog title="添加学生" v-model="dialogFormVisible" :before-close="handleCancel" center>
            <el-form ref="formRef" :model="form" label-width="200px">
              <el-row :gutter="20">
                <el-col :span="18">
                  <el-form-item label="姓名" prop="name">
                    <el-input v-model="form.name" placeholder="请输入姓名" clearable />
                  </el-form-item>
                  <el-form-item label="学号/工号" prop="id">
                    <el-input v-model="form.id" placeholder="请输入学号/工号" clearable />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
            <template #footer>
              <span class="dialog-footer">
                <el-button @click="handleCancel">取 消</el-button>
                <el-button type="primary" :loading="loading" @click="submitForm">确 定</el-button>
              </span>
            </template>
          </el-dialog>
        </div>
      </div>

    </el-card>
  </div>
</template>

<style scoped lang="scss">
.content {
  display: flex;
}

.left {
  width: 33vh;
  padding: 10px;
  border-right: 1px solid #eee;

  .button {
    margin-bottom: 10px;
  }
}

.scrollbar-demo-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 50px;
  margin: 10px;
  text-align: center;
  border-radius: 4px;
  // background-color: #eee;
  position: relative;

  .more {
    position: absolute;
    top: 15px;
    right: 10px;

    .el-dropdown-link {
      cursor: pointer;

      &:hover {
        color: #409eff;
        border: none;
      }
    }
  }
}

.right-head {
  padding: 10px;
  border-bottom: 1px solid #eee;

}
</style>
