<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
const router = useRouter()
const route = useRoute()

import { ref, watch, onMounted } from 'vue'

import {
  StarTwoTone,
  FileTextOutlined,
  FileDoneOutlined,
  FundProjectionScreenOutlined,
  FolderOpenOutlined,
  ClusterOutlined,
  FileExcelOutlined
} from '@ant-design/icons-vue';

onMounted(() => {
  console.log(route.params.id)
})

watch(
  () => route.path,
  (newPath, oldPath) => {
    console.log(`路径变化: ${oldPath} → ${newPath}`)
  }
)


const selectedKeys = ref(['/']);
const handleClick = menuInfo => {
  console.log('click ', menuInfo);
  selectedKeys.value = menuInfo.key
  router.push(`/course/LearningCourses/CourseDetails/${route.params.id}` + menuInfo.key )
};
</script>

<template>
  <div class="courseContainer">
    <div class="courseSider">
      <a-menu v-model:selectedKeys="selectedKeys" style="width: 100%;" mode="vertical" @click="handleClick">
        <div class="logo">
          <img src="" alt="">
          <span></span>
        </div>
        <a-menu-item key="/AITeachingAssistantLearning" style="height: 50px; line-height: 50px;">
          <StarTwoTone />
          <span>AI助教</span>
        </a-menu-item>
        <a-menu-item key="/ChapterStudyLearning" style="height: 50px; line-height: 50px;">
          <FileTextOutlined />
          <span>章节学习</span>
        </a-menu-item>
        <a-menu-item key="/CourseWorkLearning" style="height: 50px; line-height: 50px;">
          <FileDoneOutlined />
          <span>课程作业</span>
        </a-menu-item>
        <a-menu-item key="/CourseExamsLearning" style="height: 50px; line-height: 50px;">
          <FundProjectionScreenOutlined />
          <span>课程考试</span>
        </a-menu-item>
        <a-menu-item key="/CourseMaterialsLearning" style="height: 50px; line-height: 50px;">
          <FolderOpenOutlined />
          <span>课程资料</span>
        </a-menu-item>
        <a-menu-item key="/CurriculumMapLearning" style="height: 50px; line-height: 50px;">
          <ClusterOutlined />
          <span>课程图谱</span>
        </a-menu-item>
        <a-menu-item key="/ErrorSetLearning" style="height: 50px; line-height: 50px;">
          <FileExcelOutlined />
          <span>错题集</span>
        </a-menu-item>
      </a-menu>
    </div>

    <div class="courseContent">
      <router-view v-slot="{ Component }">
        <keep-alive>
          <component :is="Component" />
        </keep-alive>
      </router-view>
    </div>
  </div>
</template>

<style scoped lang="scss">
.courseContainer {
  display: flex;
  height: 100%;
}

.courseSider {
  flex: 1;
  height: 93vh;
}

.el-menu {
  height: 100%;
}

.courseContent {
  flex: 5;
  margin: 10px;
}
.ant-menu {
  height: 100%;

  .ant-menu-item{
    height: 50px !important;
  }

}
</style>
