<script setup lang="ts">
import { ArrowLeft } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
const router = useRouter()
const route = useRoute()

const type = ref()
onMounted(() => {
  type.value = ref(route.params.type)
  console.log(route.params.type)
  console.log(route.params.id)
})

const displayList = ref({

})

// 提交作业/考试
const SubmitFinish = () => {

}
</script>

<template>
  <div>
    <div class="head">
      <el-page-header :icon="ArrowLeft" @back="router.back">
        <template #content>
          <div class="flex items-center">
            <span class="text-large font-600 mr-3"> {{ type == '1' ? '作业详情' : '详情' }} </span>
          </div>
        </template>
        <template #extra>
          <div class="flex items-center">
            <el-popconfirm class="box-item" title="请检查是否完成，并确认提交" placement="left" @confirm="SubmitFinish">
              <template #reference> <el-button round class="gradient-btn"
                  style="color: #fff;width: 10vh;height: 5vh; font-size: 16px;">提交</el-button></template>
            </el-popconfirm>
          </div>
        </template>
      </el-page-header>
    </div>

    <div class="detail">
      <div class="left">
        <div class="left-head">
          <div class="title">xxx</div>
          <div>
            <span style="margin-right: 10px;color: gray;">题量：</span>
            <span style="color: gray;">总分：</span>
          </div>
        </div>
      </div>

      <div class="right">

      </div>
    </div>

  </div>
</template>

<style scoped lang="scss">
.head {
  background-color: #fff;
  padding: 20px 30px;
}

.detail {
  display: flex;
  justify-content: space-between;
  margin: 20px;

  .left {
    flex: 3;
    min-height: 80vh;
    background-color: #fff;
    padding: 20px;
    border-radius: 10px;

    .left-head {
      padding-bottom: 10px;
      border-bottom: 1px solid #eee;
    }

  }

  .right {
    margin-left: 20px;
    flex: 1;
    min-height: 80vh;
    background-color: #fff;
    padding: 20px;
    border-radius: 10px;
  }
}
</style>
