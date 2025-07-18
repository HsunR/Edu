<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue'
import { Edit, Delete } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router'
const router = useRouter()

import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { UploadProps } from 'element-plus'

interface ListItem {
  id: string
  imgUrl: string
  name: string
}

const loading = ref(true)
const lists = ref<ListItem[]>([])
onMounted(() => {
  loading.value = false
  lists.value = [
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
    {
      id: "1004",
      imgUrl: '/src/assets/images/test.png',
      name: '高等数学',
    },
    {
      id: "1005",
      imgUrl: '/src/assets/images/test.png',
      name: '操作系统',
    },
  ]
})

const dialogFormVisible = ref(false)
const isEdit = ref(false)
const formLabelWidth = '120px'
const courseFormRef = ref();

const CourseForm = reactive({
  courseName: '',
  courseDescribe: '',
  courseCover:'',
})
const addCourse = () =>{
  isEdit.value = false
  dialogFormVisible.value = true
}
const editCourse = (id) =>{
  console.log("编辑：", id);
  isEdit.value = true
  dialogFormVisible.value = true
}

// 上传图片
const imageUrl = ref('')
// 上传成功返回url地址  response.url
const handleUploadSuccess: UploadProps['onSuccess'] = (
    response,
    uploadFile
) => {
  imageUrl.value = URL.createObjectURL(uploadFile.raw!)
}

const beforeUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('图片大小不能超过2MB!')
    return false
  }
  return true
}

const handleRemove = () => {
  imageUrl.value = "";
  CourseForm.courseCover = '';
}

// 跳转课程详情页
const toCourse = (id) => {
  router.push(`/course/LearningCourses/CourseDetails/${id}/AITeachingAssistantLearning`)
}

// 遮罩层
const activeOverlayIndex = ref(-1);
const toggleOverlay = (index, isShow)=>{
  activeOverlayIndex.value = isShow ? index : -1;
}

// 删除操作
const handleDelete = (id) => {
  console.log("删除：", id);
};

const handleCancle = () => {
  dialogFormVisible.value = false
  resetForm()
}

const handleSubmit = () => {
  courseFormRef.value.validate(async (valid) => {
    if (valid) {
      // const res = isEdit.value ? await editCourse() : await addCourse()
      // console.log(res)
      // if(res.data.data){
      //   ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      //   dialogFormVisible.value = false
      //   resetForm()
      // }else {
      //   ElMessage.error(isEdit.value ? '修改失败' : '新增失败')
      // }

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
                /*justify-items: space-between;*/
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
              class="courseCard"
              @mouseenter="toggleOverlay(item.id, true)"
              @mouseleave="toggleOverlay(item.id, false)">
              <img :src="item.imgUrl" class="image multi-content" style="max-width: 100%" />
              <div style="padding: 14px">
                <span>{{ item.name }}</span>
                <div class="bottom card-header">
                  <el-button class="button gradient-btn" @click="toCourse(item.id)">进入课程</el-button>
                </div>
              </div>

              <!-- 遮罩层 -->
              <div
                  class="card-overlay"
                  v-show="activeOverlayIndex === item.id"
                  @click.stop
              >
                <el-button type="primary" plain round @click="editCourse(item.id)" style="width: 6vh;">
                  <el-icon><Edit /></el-icon>编辑
                </el-button>
                <el-button type="danger" round @click="handleDelete(item.id)"  style="width: 6vh;">
                  <el-icon><Delete /></el-icon>删除
                </el-button>
              </div>
            </el-card>
          </div>

        </template>
      </el-skeleton>
    </el-space>

    <!-- 新增课程对话框   -->
    <el-dialog v-model="dialogFormVisible" :title="isEdit ? '编辑课程' : '新增课程' " width="700" style="padding: 20px 60px 20px 10px;">
      <el-form ref="courseFormRef" :model="CourseForm">
        <el-form-item label="课程名字" prop="courseName" :label-width="formLabelWidth">
          <el-input v-model="CourseForm.courseName" autocomplete="off" />
        </el-form-item>
        <el-form-item label="课程封面" prop="courseCover" :label-width="formLabelWidth">
          <el-upload
              class="avatar-uploader"
              action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :before-upload="beforeUpload"
          >
            <img v-if="imageUrl" :src="imageUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            <el-button v-if="imageUrl" @click.stop="handleRemove">删除图片</el-button>
          </el-upload>
        </el-form-item>

        <el-form-item label="课程详细描述" prop="courseDescribe" :label-width="formLabelWidth">
          <el-input v-model="CourseForm.courseDescribe " autocomplete="off" type="textarea"/>
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

    <router-view>
      <keep-alive></keep-alive>
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
.courseCard:hover{
  box-shadow: 0 4px 15px rgba(39, 116, 232, 0.6);
}

.card-overlay{
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
</style>
