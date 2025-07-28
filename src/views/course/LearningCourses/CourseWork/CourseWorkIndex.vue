<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()
const status = ref(1)

interface ListItem {
  id:string
  imgUrl: string
  name: string
  status: string
}

const loading = ref(true)
// 作业列表
const lists = ref<ListItem[]>([])
const currentDate = new Date().toDateString()

const setLoading = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 2000)
}

onMounted(() => {
  loading.value = false
  lists.value = [
    {
      id: "1001",
      imgUrl:'/src/assets/images/homework.png',
      name: '作业一',
      status: 'success'
    },
    {
      id: "1002",
      imgUrl: '/src/assets/images/homework.png',
      name: '作业二',
      status: 'success'
    },
    {
      id: "1003",
      imgUrl: '/src/assets/images/homework.png',
      name: '作业三',
      status: 'success'
    },
  ]
})
</script>

<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/LearningCourses' }">我学的课</el-breadcrumb-item>
            <el-breadcrumb-item>课程作业</el-breadcrumb-item>
          </el-breadcrumb>

          <div class="status">
            <span style="margin: 10px 10px 0 0;">筛选：</span>
            <el-radio-group v-model="status">
              <el-radio :value="1">全部</el-radio>
              <el-radio :value="2">已完成</el-radio>
              <el-radio :value="3">未完成</el-radio>
            </el-radio-group>
          </div>

        </div>
      </template>

      <!-- 作业列表 -->
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
        <template #default>
          <el-card v-for="item in lists" :key="item.name" :body-style="{ padding: '0px', marginBottom: '1px' }"
            class="homework-card" @click="router.push({ path: `/course/homeworkWorkDetail/1/${item.id}` })">
            <span>
              <img :src="item.imgUrl" class="image multi-content" style="width: 50px;border-radius: 10px;" />
            </span>
            <span style=" display: flex; flex-direction: column;">
              <span>{{ item.name }}</span>
              <span>{{ item.status }}</span>
            </span>
          </el-card>
        </template>
      </el-skeleton>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.status {
  margin: 15px 0 0 10px;
}

.homework-card {
  margin: 10px;
  padding: 10px;
  cursor: pointer;

  span {
    float: left;
    margin-right: 20px;
  }

  &:hover{
    background-color: #f5f5f5;
  }
}
</style>
