<template>
  <div class="app-container home">
    首页
  </div>
</template>

<script setup name="Index">
import { onMounted, ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { getUserList } from '@/api/user/user'

const userStore = useUserStore()
const { userInfo } = storeToRefs(userStore)

onMounted(async () => {
  await userStore.fetchUserInfo()

  console.log(userInfo.value)

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

const getUsersList = async () => {
  const res = await getUserList(queryParam.value)
}
</script>

<style scoped lang="scss">

</style>
