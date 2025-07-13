<template>
   <div class="app-container">
      <el-row :gutter="20">
         <el-col :span="6" :xs="24">
            <el-card class="box-card">
               <template v-slot:header>
                  <div class="clearfix">
                     <span>个人信息</span>
                  </div>
               </template>
               <div>
                  <div class="text-center">
                     <userAvatar />
                  </div>
                  <ul class="list-group list-group-striped">
                     <li class="list-group-item">
                        <svg-icon icon-class="user" style="margin-right: 10px;" />用户名称
                        <div class="pull-right">{{ user?.value?.name }}</div>
                     </li>
                     <li class="list-group-item">
                        <svg-icon icon-class="phone" style="margin-right: 10px;" />手机号码
                        <div class="pull-right">{{ user?.value?.mobile }}</div>
                     </li>
                     <li class="list-group-item">
                        <svg-icon icon-class="email" style="margin-right: 10px;" />用户邮箱
                        <div class="pull-right">{{ user?.value?.email }}</div>
                     </li>
                     <li class="list-group-item">
                        <svg-icon icon-class="peoples" style="margin-right: 10px;" />用户ID
                        <div class="pull-right">{{ user?.value?.userId }}</div>
                     </li>
                     <li class="list-group-item">
                        <svg-icon icon-class="tree" style="margin-right: 10px;" />学校
                        <div class="pull-right">{{ user?.value?.school }}</div>
                     </li>
                  </ul>
               </div>
            </el-card>
         </el-col>
         <el-col :span="18" :xs="24">
            <el-card>
               <template v-slot:header>
                  <div class="clearfix">
                     <span>基本资料</span>
                  </div>
               </template>
               <el-tabs v-model="selectedTab">
                  <el-tab-pane label="基本资料" name="userinfo">
                     <userInfo :user="user.value" />
                  </el-tab-pane>
                  <el-tab-pane label="修改密码" name="resetPwd">
                     <resetPwd />
                  </el-tab-pane>
               </el-tabs>
            </el-card>
         </el-col>
      </el-row>
   </div>
</template>

<script setup name="Profile">
import userAvatar from "./userAvatar"
import userInfo from "./userInfo"
import resetPwd from "./resetPwd"

import useUserStore from '@/store/modules/user'
import { onMounted } from 'vue'

const userStore = useUserStore()

const selectedTab = ref("userinfo")
const user = reactive({
   userId: "",
   name: "",
   avatar: "",
   email: "",
   mobile: "",
   school: "",
   sex: "",
})

onMounted(async () => {
   console.log("个人中心")
   const { data } = await userStore.getInfo()
   user.value = data.data
   console.log(user.value)
})


</script>
