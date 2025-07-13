<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
const router = useRouter()
const route = useRoute()

import { ref, watch, computed, onMounted } from 'vue'

import {
  AppstoreOutlined,
  FileTextOutlined,
  FileDoneOutlined,
  FundProjectionScreenOutlined,
  FolderOpenOutlined,
  BookOutlined,
  RadarChartOutlined
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


const selectedKeys = ref(['1']);
const handleClick = menuInfo => {
  console.log('click ', menuInfo);
  router.push(`/course/TeachingCourses/CourseDetails/${route.params.id}` + menuInfo.key )
};
</script>

<template>
  <div class="courseContainer">
    <div class="courseSider">
      <div class="logo" />
      <a-menu v-model:selectedKeys="selectedKeys" style="width: 100%" mode="vertical" @click="handleClick">
        <a-menu-item key="/ClassManagementTeaching" style="height: 50px; line-height: 50px;">
          <AppstoreOutlined />
          <span>班级管理</span>
        </a-menu-item>
        <a-menu-item key="/ChapterStudyTeaching" style="height: 50px; line-height: 50px;">
          <FileTextOutlined />
          <span>章节学习</span>
        </a-menu-item>
        <a-menu-item key="/CourseWorkTeaching" style="height: 50px; line-height: 50px;">
          <FileDoneOutlined />
          <span>课程作业</span>
        </a-menu-item>
        <a-menu-item key="/CourseExamsTeaching" style="height: 50px; line-height: 50px;">
          <FundProjectionScreenOutlined />
          <span>课程考试</span>
        </a-menu-item>
        <a-menu-item key="/CourseMaterialsTeaching" style="height: 50px; line-height: 50px;">
          <FolderOpenOutlined />
          <span>课程资料</span>
        </a-menu-item>
        <a-menu-item key="/QuestionBankManagementTeaching" style="height: 50px; line-height: 50px;">
          <BookOutlined />
          <span>题库管理</span>
        </a-menu-item>
        <a-menu-item key="/KnowledgePointsTeaching" style="height: 50px; line-height: 50px;">
          <RadarChartOutlined />
          <span>知识点</span>
        </a-menu-item>
      </a-menu>
    </div>

    <div class="courseContent">
      <router-view v-slot="{ Component }">
        <keep-alive>
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </keep-alive>
      </router-view>
    </div>
  </div>
</template>

<style scoped>
.courseContainer {
  display: flex;
  height: 100%;
}

.courseSider {
  flex: 1;
  height: 90vh;
}

.el-menu {
  height: 100%;
}

.courseContent {
  flex: 5;
  margin: 10px;
}

.ant-menu{
  height: 100%;
}
</style>