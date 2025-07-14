<template>
  <el-form ref="pwdRef" :model="user" :rules="rules" label-width="80px">
    <el-form-item label="用户ID" prop="userId">
      <el-input v-model="user.userId" placeholder="请输入用户ID" maxlength="30" />
    </el-form-item>
    <el-form-item label="新密码" prop="password">
      <el-input v-model="user.password" placeholder="请输入新密码" type="password" show-password />
    </el-form-item>
    <el-form-item label="确认密码" prop="confirmPassword">
      <el-input v-model="user.confirmPassword" placeholder="请确认新密码" type="password" show-password />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submit">保存</el-button>
      <el-button type="danger" @click="close">关闭</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import api from '@/temp/index';
// import api from '@/services/user/user/index.js';
const { userController } = api;

import { reactive } from "vue"
const { proxy } = getCurrentInstance()

const user = reactive({
  userId: undefined,
  password: undefined,
  confirmPassword: undefined
})

const equalToPassword = (rule, value, callback) => {
  if (user.password !== value) {
    callback(new Error("两次输入的密码不一致"))
  } else {
    callback()
  }
}

const rules = ref({
  userId: [{ required: true, message: "用户ID不能为空", trigger: "blur" }],
  password: [{ required: true, message: "新密码不能为空", trigger: "blur" }, { min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" }],
  confirmPassword: [{ required: true, message: "确认密码不能为空", trigger: "blur" }, { required: true, validator: equalToPassword, trigger: "blur" }]
})

/** 提交按钮 */
function submit() {
  proxy.$refs.pwdRef.validate(valid => {
    if (valid) {
      console.log(user)
      userController.updateUserInfo(user).then(response => {
        console.log(response.data)
        if (response.data.data) {
          proxy.$modal.msgSuccess("修改成功")
        } else {
          proxy.$modal.msgError(response.data.message)
        }
      })
    }
  })
}

/** 关闭按钮 */
function close() {
  proxy.$tab.closePage()
}
</script>
