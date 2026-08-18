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
                        <div class="pull-right">{{ userStore.userInfo?.name }}</div>
                     </li>
                     <li class="list-group-item">
                        <svg-icon icon-class="phone" style="margin-right: 10px;" />手机号码
                        <div class="pull-right">{{ userStore.userInfo?.mobile }}</div>
                     </li>
                     <li class="list-group-item">
                        <svg-icon icon-class="email" style="margin-right: 10px;" />用户邮箱
                        <div class="pull-right">{{ userStore.userInfo?.email }}</div>
                     </li>
                     <li class="list-group-item">
                        <svg-icon icon-class="peoples" style="margin-right: 10px;" />用户ID
                        <div class="pull-right">{{ userStore.userInfo?.userId }}</div>
                     </li>
                     <li class="list-group-item">
                        <svg-icon icon-class="tree" style="margin-right: 10px;" />学校
                        <div class="pull-right">{{ userStore.userInfo?.school }}</div>
                     </li>
                     <li class="list-group-item">
                        <svg-icon icon-class="peoples" style="margin-right: 10px;" />角色
                        <div class="pull-right">{{ userStore.userInfo?.userType }}</div>
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
                     <userInfo />
                  </el-tab-pane>
                  <el-tab-pane label="修改密码" name="resetPwd">
                     <resetPwd />
                  </el-tab-pane>
                  <el-tab-pane v-if="userStore.isStudent" label="学生档案" name="studentProfile">
                     <studentProfile />
                  </el-tab-pane>
                  <el-tab-pane v-if="userStore.isTeacher" label="教师档案" name="teacherProfile">
                     <teacherProfile />
                  </el-tab-pane>
               </el-tabs>
            </el-card>
         </el-col>
      </el-row>
   </div>
</template>

<script setup lang="ts">
import userAvatar from "./userAvatar"
import userInfo from "./userInfo"
import resetPwd from "./resetPwd"
import studentProfile from "./studentProfile"
import teacherProfile from "./teacherProfile"
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const selectedTab = ref("userinfo")
</script>
