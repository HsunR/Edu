<template>
  <el-form ref="userRef" :model="form" :rules="rules" label-width="80px">
    <el-form-item label="用户ID" prop="userId">
      <el-input v-model="form.userId" maxlength="30" />
    </el-form-item>
    <el-form-item label="用户姓名" prop="name">
      <el-input v-model="form.name" maxlength="30" />
    </el-form-item>
    <el-form-item label="个性签名" prop="personalSignature">
      <el-input v-model="form.personalSignature" maxlength="30" />
    </el-form-item>
    <!-- <el-form-item label="手机号码" prop="mobile">
      <el-input v-model="form.mobile" maxlength="11" />
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model="form.email" maxlength="50" />
    </el-form-item> -->
    <el-form-item label="学校" prop="school">
      <el-input v-model="form.school" maxlength="50" />
    </el-form-item>
    <el-form-item label="性别">
      <el-radio-group v-model="form.sex">
        <el-radio :value="0">男</el-radio>
        <el-radio :value="1">女</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submit">保存</el-button>
      <el-button type="danger" @click="close">关闭</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import api from '@/api/index.js';
const { userController } = api;

import { ElMessage } from "element-plus"
import { toRefs } from 'vue'


const props = defineProps({
  user: {
    type: Object
  }
})

const { user } = toRefs(props)
const { proxy } = getCurrentInstance()

const form = ref({})
const rules = ref({
  userId: [{ required: true, message: "用户ID不能为空", trigger: "blur" }],
  name: [{ required: true, message: "用户昵称不能为空", trigger: "blur" }],
  email: [ { type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] }],
  mobile: [{ pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输入正确的手机号码", trigger: "blur" }],
})

/** 提交按钮 */
function submit() {
  proxy.$refs.userRef.validate(valid => {
    if (valid) {
      userController.updateUserInfo(form.value).then(response => {
        if (response.data.data) {
          ElMessage.success("修改成功")
        } else {
          ElMessage.error(response.data.message)
        }
      })
    }
  })
}

/** 关闭按钮 */
function close() {
  proxy.$tab.closePage()
}

// 回显当前登录用户信息
watch(user, (newVal) => {
  if (newVal) {
    form.value = {
      userId: newVal.userId,
      name: newVal.name,
      personalSignature: newVal.personalSignature,
      school: newVal.school,
      mobile: newVal.mobile,
      email: newVal.email,
      sex: newVal.sex
    }
  }
}, { immediate: true, deep: true })
</script>
