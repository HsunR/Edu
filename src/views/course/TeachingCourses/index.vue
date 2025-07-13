<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()
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

// 添加课程
const addCourse = () => {

}

// 跳转课程详情页
const toCourse = (id) => {
  router.push(`/course/TeachingCourses/CourseDetails/${id}/ClassManagementTeaching`)
}
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
                justify-items: space-between;
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
              class="courseCard">
              <img :src="item.imgUrl" class="image multi-content" style="max-width: 100%" />
              <div style="padding: 14px">
                <span>{{ item.name }}</span>
                <div class="bottom card-header">
                  <el-button class="button gradient-btn" @click="toCourse(item.id)">进入课程</el-button>
                </div>
              </div>
            </el-card>
          </div>

        </template>
      </el-skeleton>
    </el-space>
    <router-view>
      <keep-alive></keep-alive>
    </router-view>
  </div>
</template>

<style scoped>
.gradient-btn {
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(to right, #67a2ff, #606bff);
  color: white;
  border: none;
  text-align: center;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 0.8rem;
  cursor: pointer;
  transition: all 0.3s ease;
  margin: 2vh 0 0 5vh;
}

.gradient-btn:hover {
  background: linear-gradient(to right, #606bff, #67a2ff);
  box-shadow: 0 4px 15px rgba(138, 43, 226, 0.4);
}

.courses-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.courseCard {
  flex: 0 0 calc(20% - 10px);
  box-sizing: border-box;
  margin-left: 50px;
}
</style>
