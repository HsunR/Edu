<template>
  <div class="app-container home">
    首页
  </div>
</template>

<script setup name="Index">
import { onMounted } from 'vue'
import useUserStore from '@/store/modules/user.js'
// 1. 引入 storeToRefs
import { storeToRefs } from 'pinia'

const userStore = useUserStore()
// 2. 使用 storeToRefs 解构
const { loginUserInfo, userInfo } = storeToRefs(userStore)

import api from '@/temp/index.js';
const { userController } = api;

onMounted(async () => {
  await userStore.getInfo()
  await userStore.getLoginUser()

  console.log(userInfo.value)
  console.log(loginUserInfo.value)

  await getUsersList()
})

const queryParam = ref({
  "current": 1,
  "pageSize": 10,
  "sortField": "",
  "name": "",
  "userType": "",
  "email": "",
  "mobile": "",
  "school": ""
})

const getUsersList = async ()=>{
  const res = await userController.listUsers(queryParam.value)
  // console.log(res)
}
</script>

<style scoped lang="scss">

</style>

