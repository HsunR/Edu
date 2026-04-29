<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue'
import { Edit, Delete } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router'
const router = useRouter()

import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { UploadProps } from 'element-plus'

interface ListItem {
  id: string
  imgUrl: string
  name: string
}

import api from '@/api';
import axios from "axios";
const { CourseController, coResourceController } = api;

const loading = ref(true)
const lists = ref<ListItem[]>([])
onMounted(() => {
  loading.value = false
  lists.value = [
    {
      id: "1005",
      imgUrl: '/src/assets/images/test.png',
      name: '操作系统',
    },
    {
      id: "1004",
      imgUrl: '/src/assets/images/test.png',
      name: '高等数学',
    },
    {
      id: "1001",
      imgUrl: '/src/assets/images/test.png',
      name: '离散数学',
    },
    {
      id: "1002",
      imgUrl: '/src/assets/images/test.png',
      name: '数据库',
    },
    {
      id: "1003",
      imgUrl: '/src/assets/images/test.png',
      name: '概率论',
    },
  ]
})

const dialogFormVisible = ref(false)
const isEdit = ref(false)
const formLabelWidth = '120px'
const courseFormRef = ref();

const CourseForm = reactive({
  courseName: '',
  description: '',
  coverUrl: '',
  categoryId: null,
  isPublic: null
})

const addCourse = () => {
  isEdit.value = false
  dialogFormVisible.value = true
}
const editCourse = async (id) => {
  console.log("编辑：", id);
  isEdit.value = true
  dialogFormVisible.value = true

  // 获取课程详情
  const res = await CourseController.courseDetail(id)
  console.log(res.data)
  const detail = res.data.data
  if(res.data){
    CourseForm.courseName = detail?.courseName
    CourseForm.description = detail?.description
    CourseForm.coverUrl = detail?.coverUrl
    CourseForm.categoryId = detail?.categoryId
    CourseForm.isPublic = detail?.isPublic
    imageUrl.value = detail?.coverUrl
  }else {
    ElMessage.error("获取课程详情失败")
  }
}

// 上传图片
const imageUrl = ref('')

const selectedFile = ref(null)
const uploadStatus = ref('idle') // idle, uploading, confirming, success, error
const errorMessage = ref('')
const finalResource = ref({})

const resourceForm = reactive({
  fileName: '',
  fileSize: 0
})
const handleFileChange = async (file) => {
  resourceForm.fileName = file.raw.name
  resourceForm.fileSize = file.size
  imageUrl.value = URL.createObjectURL(file.raw)
  console.log("上传图片")
  console.log(resourceForm.fileName, resourceForm.fileSize)

  selectedFile.value = file.raw
}


const handleUpload = ()=>{
  uploadFileProcess(selectedFile.value)
}

// 完整上传流程
const uploadFileProcess = async (file) => {
  uploadStatus.value = 'uploading'
  errorMessage.value = ''

  try {
    // 1: 调用预签名接口
    const presignResponse = await coResourceController.uploadImagePresign(resourceForm)
    console.log(presignResponse.data)

    const presignData = presignResponse.data

    if (!presignData || !presignData.uploadUrl) {
      throw new Error('获取预签名URL失败')
    }

    const { resourceId, uploadUrl, accessUrl } = presignData
    // 赋值给 创建课程表 中的封面url
    CourseForm.coverUrl = accessUrl

    // 2: 直传腾讯云 COS
    await axios.put(uploadUrl, file, {
      headers: {
        'Content-Type': file.type
      }
    })

    // 3: 确认上传完成
    uploadStatus.value = 'confirming'

    const confirmResponse = await coResourceController.uploadConfirm({
      resourceId: resourceId,
      accessUrl: accessUrl,
      fileSize: file.size
    })

    // 上传成功
    finalResource.value = confirmResponse.data
    uploadStatus.value = 'success'

  } catch (err) {
    console.error(err)
    errorMessage.value = err.response?.data?.message || err.message || '未知错误'
    uploadStatus.value = 'error'
  }
}


// 移除封面
const handleRemove = () => {
  imageUrl.value = "";
  CourseForm.coverUrl = '';
  selectedFile.value = null
  uploadStatus.value = 'idle'
  errorMessage.value = ''
  finalResource.value = {}
}

// 跳转课程详情页
const toCourse = (id) => {
  router.push(`/course/TeachingCourses/CourseDetails/${id}/ClassManagementTeaching`)
}


// 遮罩层
const activeOverlayIndex = ref(-1);
const toggleOverlay = (index, isShow) => {
  activeOverlayIndex.value = isShow ? index : -1;
}

// 删除操作
const handleDelete = (id) => {
  console.log("删除：", id);
};
const open = (id) => {
  ElMessageBox.confirm(
      '确认删除该课程吗?',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(() => {
        handleDelete(id)
        ElMessage({
          type: 'success',
          message: '删除成功',
        })
      })
      .catch(() => {
        ElMessage({
          type: 'info',
          message: '删除失败',
        })
      })
}

const handleCancle = () => {
  dialogFormVisible.value = false
  resetForm()
}

const handleSubmit = () => {
  courseFormRef.value.validate(async (valid) => {
    if (valid) {
      const res = isEdit.value ? await CourseController.updateCourse(CourseForm) : await CourseController.createCourse(CourseForm)
      console.log(res)
      if(res.data.data){
        ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
        dialogFormVisible.value = false
        resetForm()
      }else {
        ElMessage.error(isEdit.value ? '修改失败' : '新增失败')
      }

    } else {
      ElMessage.error('请填写完整')
      return false
    }
  })
}

// 清空表单
const resetForm = () => {
  courseFormRef.value?.resetFields();
  imageUrl.value = '';
  CourseForm.coverUrl = '';
  selectedFile.value = null
  uploadStatus.value = 'idle'
  errorMessage.value = ''
  finalResource.value = {}
};


</script>

<template>
  <div>
    <el-space style="width: 100%" fill>
      <div>
        <el-button @click="addCourse" class="gradient-btn" style="margin: 20px;">添加课程</el-button>
      </div>
      <el-skeleton style="display: flex; gap: 8px" :loading="loading" animated :count="3">
        <template #template>
          <div style="flex: 1">
            <el-skeleton-item variant="image" style="height: 240px" />
            <div style="padding: 14px">
              <el-skeleton-item variant="h3" style="width: 50%" />
              <div style="
                display: flex;
                align-items: center;
                margin-top: 16px;
                height: 16px;
              ">
                <el-skeleton-item variant="text" style="margin-right: 16px" />
                <el-skeleton-item variant="text" style="width: 30%" />
              </div>
            </div>
          </div>
        </template>
        <!-- 课程列表 -->
        <template #default>
          <div class="courses-container">
            <el-card v-for="item in lists" :key="item.name" :body-style="{ padding: '20px', margin: '0px' }"
              class="courseCard" @mouseenter="toggleOverlay(item.id, true)" @mouseleave="toggleOverlay(item.id, false)">
              <img :src="item.imgUrl" class="image multi-content" style="max-width: 100%" />
              <div style="padding: 14px">
                <span>{{ item.name }}</span>
                <div class="bottom card-header">
                  <el-button class="gradient-btn" @click="toCourse(item.id)">进入课程</el-button>
                </div>
              </div>

              <!-- 遮罩层 -->
              <div class="card-overlay" v-show="activeOverlayIndex === item.id" @click.stop>
                <el-button type="primary" plain round @click="editCourse(item.id)" style="width: 6vh;">
                  <el-icon>
                    <Edit />
                  </el-icon>编辑
                </el-button>
                <el-button type="danger" @click="open(item.id)" round style="width: 6vh;">
                  <el-icon>
                    <Delete />
                  </el-icon>删除
                </el-button>

              </div>
            </el-card>
          </div>

        </template>
      </el-skeleton>
    </el-space>

    <!-- 新增课程对话框   -->
    <el-dialog v-model="dialogFormVisible" :title="isEdit ? '编辑课程' : '新增课程'" width="700"
      style="padding: 20px 60px 20px 10px;">
      <el-form ref="courseFormRef" :model="CourseForm">
        <el-form-item label="课程名字" prop="courseName" :label-width="formLabelWidth">
          <el-input v-model="CourseForm.courseName" autocomplete="off" />
        </el-form-item>
        <el-form-item label="课程封面" prop="coverUrl" :label-width="formLabelWidth">
          <el-upload
              class="avatar-uploader"
              action=""
              :auto-upload="false"
              :on-change="handleFileChange"
              :show-file-list="false"
              :limit="1">
            <img v-if="imageUrl" :src="imageUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon">
              <Plus />
            </el-icon>
            <el-button v-if="imageUrl" @click.stop="handleRemove">删除封面</el-button>
            <el-button v-if="imageUrl" @click.stop="handleUpload">上传封面</el-button>
          </el-upload>
          <!-- 状态显示 -->
          <div v-if="uploadStatus === 'uploading'" class="status">
            正在上传到云端...
          </div>
          <div v-if="uploadStatus === 'confirming'" class="status">
            正在确认资源...
          </div>
          <div v-if="uploadStatus === 'success'" class="status">
            上传成功！
          </div>
          <div v-if="uploadStatus === 'error'" class="error">
            上传失败: {{ errorMessage }}
          </div>
        </el-form-item>


        <el-form-item label="课程详细描述" prop="description" :label-width="formLabelWidth">
          <el-input v-model="CourseForm.description" autocomplete="off" type="textarea" />
        </el-form-item>

        <el-form-item label="课程分类ID" prop="categoryId" :label-width="formLabelWidth">
          <el-input v-model="CourseForm.categoryId" autocomplete="off" />
        </el-form-item>

        <el-form-item label="课程是否公开" prop="isPublic" :label-width="formLabelWidth">
          <el-select v-model="CourseForm.isPublic" filterable placeholder="请选择">
            <el-option label="私有" value="0" />
            <el-option label="公开" value="1" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCancle">取 消</el-button>
          <el-button type="primary" @click="handleSubmit">
            确 认
          </el-button>
        </div>
      </template>
    </el-dialog>

    <router-view v-slot="{ Component }">
      <keep-alive>
        <component :is="Component" />
      </keep-alive>
    </router-view>
  </div>
</template>

<style scoped>
.courses-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.courseCard {
  flex: 0 0 calc(20% - 10px);
  box-sizing: border-box;
  margin-left: 50px;
  margin-bottom: 20px;
  position: relative;
  padding-bottom: 30px;
}

.courseCard:hover {
  box-shadow: 0 4px 15px rgba(39, 116, 232, 0.6);
}

.card-overlay {
  position: absolute;
  top: 20px;
  right: 10px;
}

.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.gradient-btn {
  color: #fff;
}

.status {
  margin-top: 15px;
  color: #007bff;
  font-weight: bold;
}
.error {
  margin-top: 15px;
  color: #dc3545;
}
</style>
