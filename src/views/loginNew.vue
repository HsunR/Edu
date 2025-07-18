<template>
  <div class="loginBox">
    <div class="shell">
      <!-- 登录 -->
      <div class="container a-container" :class="{ 'is-txl': !isLoginVisible }">
        <el-form class="form" :model="loginForm" :rules="loginRules" ref="loginFormRef" @submit.prevent="handleLogin">
          <h2 class="form_title title">登录</h2>

          <!-- 手机号码登录 -->
          <el-form-item v-if="loginForm.loginType === 1" prop="mobile">
            <el-input v-model="loginForm.mobile" class="form_input" placeholder="手机号码" :prefix-icon="Iphone" />
          </el-form-item>

          <!-- 邮箱登录 -->
          <el-form-item v-else-if="loginForm.loginType === 2" prop="email">
            <el-input v-model="loginForm.email" class="form_input" placeholder="邮箱" :prefix-icon="Message" />
          </el-form-item>

          <!-- 微信登录 -->
          <el-form-item v-else-if="loginForm.loginType === 3" prop="openId">
            <el-input v-model="loginForm.openId" class="form_input" placeholder="微信" :prefix-icon="Message" />
          </el-form-item>

          <!-- 用户名登录 -->
          <el-form-item v-else-if="loginForm.loginType === 4" prop="username">
            <el-input v-model="loginForm.username" class="form_input" placeholder="用户名" :prefix-icon="User" />
          </el-form-item>

          <!-- 密码 -->
          <el-form-item v-if="loginForm.loginType === 4"  prop="password">
            <el-input v-model="loginForm.password" class="form_input" type="password" placeholder="密码"
              :prefix-icon="Lock" show-password />
          </el-form-item>

          <!-- 验证码 -->
          <el-form-item v-if="loginForm.loginType === 1 || loginForm.loginType === 2" prop="code">
            <el-input v-model="loginForm.code" class="form_input" placeholder="请输入验证码" style="width: 200px;" />
            <el-button type="primary" style="margin-left: 10px; width: 140px;" @click="LoginCode"
              :disabled="codeButtonDisabled">
              {{ codeButtonText }}
            </el-button>
          </el-form-item>

          <el-link type="primary" class="form_link">忘记密码？</el-link>

          <span class="form_span">选择登录方式</span>
          <div class="form_icons">
            <el-icon class="iconfont" @click="loginByPhone">
              <Iphone />
            </el-icon>
            <el-icon class="iconfont" @click="loginByEmail">
              <svg-icon icon-class="email" class="el-input__icon input-icon" />
            </el-icon>
            <el-icon class="iconfont" @click="loginByWeChat">
              <svg-icon icon-class="wechat" class="el-input__icon input-icon" />
            </el-icon>
            <el-icon class="iconfont" @click="loginByUser">
              <User />
            </el-icon>
          </div>
          <el-button class="form_button button submit" native-type="submit" :loading="loading">
            登录
          </el-button>
        </el-form>
      </div>

      <!-- 注册 -->
      <div class="container b-container" :class="{ 'is-txl': !isLoginVisible, 'is-z': !isLoginVisible }">
        <el-form class="form" :model="registerForm" :rules="registerRules" ref="registerFormRef"
          @submit.prevent="handleRegister">
          <h2 class="form_title title">注册</h2>

          <!-- 用户名 -->
          <el-form-item prop="name">
            <el-input v-model="registerForm.name" class="form_input" placeholder="用户名" :prefix-icon="User" />
          </el-form-item>

          <!-- 手机号码注册 v-if="registerForm.registerType === 1"-->
          <el-form-item prop="mobile" v-if="registerForm.registerType === 1">
            <el-input v-model="registerForm.mobile" class="form_input" placeholder="手机号码" :prefix-icon="Iphone" />
          </el-form-item>

          <!-- 邮箱注册  v-else-if="registerForm.registerType === 2"-->
          <el-form-item prop="email" v-else-if="registerForm.registerType === 2">
            <el-input v-model="registerForm.email" class="form_input" placeholder="邮箱" :prefix-icon="Message" />
          </el-form-item>

          <!-- 密码 -->
          <el-form-item prop="password">
            <el-input v-model="registerForm.password" class="form_input" type="password" placeholder="密码"
              :prefix-icon="Lock" show-password />
          </el-form-item>

          <!-- 验证码 -->
          <el-form-item prop="code" v-if="registerForm.registerType === 1 || registerForm.registerType === 2">
            <el-input v-model="registerForm.code" class="form_input" placeholder="请输入验证码" style="width: 200px;" />
            <el-button type="primary" style="margin-left: 10px;  width: 140px;" @click="RegisterCode"
              :disabled="codeButtonDisabledRegister">
              {{ codeButtonTextRegister }}
            </el-button>
          </el-form-item>

          <span class="form_span">选择注册方式</span>

          <div class="form_icons">
            <el-icon class="iconfont" @click="registerByPhone">
              <Iphone />
            </el-icon>
            <el-icon class="iconfont" @click="registerByEmail">
              <svg-icon icon-class="email" class="el-input__icon input-icon" />
            </el-icon>
            <el-icon class="iconfont" @click="registerByWechat">
              <svg-icon icon-class="wechat" class="el-input__icon input-icon" />
            </el-icon>
            <el-icon class="iconfont" @click="registerByUser">
              <User />
            </el-icon>
          </div>

          <el-button class="form_button button submit" native-type="submit" :loading="loading">
            注册
          </el-button>
        </el-form>
      </div>

      <div class="switch" :class="{ 'is-txr': !isLoginVisible }">
        <div class="switch_circle" :class="{ 'is-txr': !isLoginVisible }"></div>
        <div class="switch_circle switch_circle-t" :class="{ 'is-txr': !isLoginVisible }"></div>

        <div class="switch_container" :class="{ 'is-hidden': !isLoginVisible }">
          <p class="switch_description description">注册账号</p>
          <el-button class="switch_button button switch-btn" @click="toggleForm">点击注册</el-button>
        </div>

        <div class="switch_container" :class="{ 'is-hidden': isLoginVisible }">
          <p class="switch_description description">已有账号</p>
          <el-button class="switch_button button switch-btn" @click="toggleForm">点击登录</el-button>
        </div>
      </div>
    </div>
  </div>

</template>

<script setup>
import api from '@/services/user/user/index.js';
const { userController,authController } = api;
// import api from '@/temp/index';

import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import useUserStore from '@/store/modules/user'
import { onMounted } from 'vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

onMounted(() => {
  userStore.getInfo()
})

import {
  User,
  Message,
  Iphone,
  Lock
} from '@element-plus/icons-vue'

// 显示登录页面
const isLoginVisible = ref(true)

const loginFormRef = ref(null)
const registerFormRef = ref(null)

const loginForm = reactive({
  "loginType": 1,
  "username": "",
  "password": "",
  "mobile": "",
  "email": "",
  "code": "",
  "openId": ""
})

const registerForm = reactive({
  "name": "",
  "userId": "",
  "password": "",
  "registerType": 2,
  "mobile": "",
  "email": "",
  "code": "",
  "version": 1
})

// 加载状态
const loading = ref(false)

// 验证码按钮状态
const codeButtonDisabled = ref(false)
const codeButtonText = ref('获取验证码')
const countdown = ref(60)

// 验证码按钮状态
const codeButtonDisabledRegister = ref(false)
const codeButtonTextRegister = ref('获取验证码')
const countdownRegister = ref(60)

const loginByPhone = () => {
  loginForm.loginType = 1
}

const loginByEmail = () => {
  loginForm.loginType = 2
}


const loginByWeChat = () => {
  loginForm.loginType = 3
  ElMessage.info('微信登录功能尚未开发')
}

const loginByUser = () => {
  loginForm.loginType = 4
}

const registerByEmail = () => {
  registerForm.registerType = 2
}

const registerByPhone = () => {
  registerForm.registerType = 1
}

const registerByWechat = () => {
  registerForm.registerType = 3
  ElMessage.info('微信注册功能尚未开发')
}

const registerByUser = () => {
  registerForm.registerType = 4
}

// 确保切换函数正确切换状态
const toggleForm = () => {
  isLoginVisible.value = !isLoginVisible.value
}

// 登录表单校验
const loginRules = reactive({
  mobile: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号码格式',
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    {
      type: 'email',
      message: '请输入正确的邮箱格式',
      trigger: 'blur'
    }
  ],
  openId: [
    { required: true, message: '请输入微信OpenID', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    {
      min: 2,
      max: 20,
      message: '用户名长度在2到20个字符之间',
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    {
      min: 6,
      max: 20,
      message: '密码长度在6到20个字符之间',
      trigger: 'blur'
    }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
})

// 注册表单校验
const registerRules = reactive({
  name: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    {
      min: 2,
      max: 20,
      message: '用户名长度在2到20个字符之间',
      trigger: 'blur'
    }
  ],
  userId: [
    { required: true, message: '请输入用户ID', trigger: 'blur' }
  ],
  mobile: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号码格式',
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    {
      type: 'email',
      message: '请输入正确的邮箱格式',
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    {
      min: 6,
      max: 20,
      message: '密码长度在6到20个字符之间',
      trigger: 'blur'
    }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
})

// 发送登录验证码
const LoginCode = async () => {
  if (loginForm.loginType === 1) {
    ElMessage.info('通过手机号获取验证码还未实现')
  }
  // 获取验证码(邮箱)
  if (loginForm.loginType === 2) {
    const data = {
      "loginType": loginForm.loginType,
      "mobile": loginForm.mobile,
      "email": loginForm.email
    }
    const res = await authController.sendLoginCode(data)
    console.log(res)
    if (res.data === '') {
      // 倒计时
      codeButtonDisabled.value = true
      codeButtonText.value = `${countdown.value}秒后重新获取`

      const timer = setInterval(() => {
        countdown.value--
        codeButtonText.value = `${countdown.value}秒后重新获取`
        if (countdown.value <= 0) {
          clearInterval(timer)
          codeButtonDisabled.value = false
          codeButtonText.value = '获取验证码'
          countdown.value = 60
        }
      }, 1000)
    } else {
      ElMessage.error(res.data.message)
    }

    
  }

}

// 发送注册验证码
const RegisterCode = async () => {
  if (registerForm.registerType === 1) {
    ElMessage.info('通过手机号获取验证码还未实现')
  }
  // 获取验证码(邮箱)
  if (registerForm.registerType === 2) {
    const data = {
      "registerType": registerForm.registerType,
      "mobile": registerForm.mobile,
      "email": registerForm.email
    }
    const res = await authController.sendRegisterCode(data)

    if (!res.data || res.data === "") {
      // 倒计时
      codeButtonDisabledRegister.value = true
      codeButtonTextRegister.value = `${countdownRegister.value}秒后重新获取`

      const timer = setInterval(() => {
        countdownRegister.value--
        codeButtonTextRegister.value = `${countdownRegister.value}秒后重新获取`
        if (countdownRegister.value <= 0) {
          clearInterval(timer)
          codeButtonDisabledRegister.value = false
          codeButtonTextRegister.value = '获取验证码'
          countdownRegister.value = 60
        }
      }, 1000)
    } else {
      ElMessage.error(res.data.message)
    }
  }
  
}

// 登录
const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        console.log("登录表单提交：", loginForm)
        userStore.login(loginForm).then(() => {
          resetForm();
          router.push("/");
        })
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }

    } else {
      console.log('表单验证失败')
      ElMessage.error('请填写完整')
      return false
    }
  })
}

// 注册
const handleRegister = () => {
  // 这里添加注册逻辑
  registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await authController.register(registerForm)
        console.log('注册结果:', res)
        if (res.data.data) {
          ElMessage.success('注册成功')
          // 注册成功后，跳转到登录页
          isLoginVisible.value = true
          resetForm()
        } else {
          ElMessage.error(res.data.message)
        }
        
      } catch (error) {
        ElMessage.error(error.message || '注册失败')
      } finally {
        loading.value = false
      }
    } else {
      console.log('表单验证失败')
      return false
    }
  })
}

// 重置表单
const resetForm = () => {
  loginFormRef.value?.resetFields()
  registerFormRef.value?.resetFields()
}
</script>

<style scoped>
.loginBox {
  box-sizing: border-box;
  user-select: none;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 12px;
  background-color: #ecf0f3;
  color: #a0a5a8;
}

.shell {
  position: relative;
  width: 1000px;
  min-width: 1000px;
  min-height: 700px;
  height: 700px;
  padding: 25px;
  background-color: #ecf0f3;
  box-shadow: 10px 10px 10px 10px #d1d9e6;
  border-radius: 12px;
  overflow: hidden;
}

/* 响应式 */
@media (max-width: 1200px) {
  .shell {
    transform: scale(0.7);
  }
}

@media (max-width: 1000px) {
  .shell {
    transform: scale(0.6);
  }
}

@media (max-width: 800px) {
  .shell {
    transform: scale(0.5);
  }
}

@media (max-width: 600px) {
  .shell {
    transform: scale(0.4);
  }
}

.container {
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  top: 0;
  width: 600px;
  height: 100%;
  padding: 25px;
  background-color: #ecf0f3;
  transition: 1.25s;
}

.form {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  width: 100%;
  height: 100%;
}

.iconfont {
  margin: 0 5px;
  border: rgba(0, 0, 0, 0.5) 2px solid;
  border-radius: 50%;
  font-size: 40px;
  padding: 10px;
  opacity: 0.5;
  transition: 0.1s;
}

.iconfont:hover {
  opacity: 1;
  transition: 0.15s;
  cursor: pointer;
}

::v-deep .el-input__wrapper {
  padding-left: 25px;
  font-size: 16px;
  letter-spacing: 0.15px;
  background-color: #ecf0f3;
  border-radius: 8px;
  box-shadow: inset 2px 2px 4px #d1d9e6;
  border: none;
}

::v-deep .el-input__wrapper.is-focus {
  box-shadow: inset 4px 4px 4px #d1d9e6 !important;
  background-color: #ecf0f390;
}

::v-deep .el-input__inner {
  color: #333;
}

.form_input {
  width: 350px;
  height: 40px;
  margin: 4px 0;
  padding-left: 25px;
  border: none;
  outline: none;
  transition: 0.25s ease;
  color: #181818;
}

.form_span {
  margin-top: 30px;
  margin-bottom: 12px;
  font-size: 16px;
}

.form_link {
  color: #181818;
  font-size: 15px;
  margin-top: 25px;
  border-bottom: 1px solid #a0a5a8;
  line-height: 2;
}

.title {
  font-size: 26px;
  font-weight: 700;
  line-height: 2;
  color: #181818;
  letter-spacing: 10px;
}

.description {
  font-size: 14px;
  letter-spacing: 0.25px;
  line-height: 1.6;
  text-align: center;
}

.button {
  width: 180px;
  height: 50px;
  border-radius: 25px;
  margin-top: 30px;
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 1.15px;
  background-color: #4b70e2;
  color: #f9f9f9;
  box-shadow: 8px 8px 16px #d1d9e6;
  border: none;
  outline: none;
}

.a-container {
  z-index: 100;
  left: calc(100% - 600px);
}

.b-container {
  left: calc(100% - 600px);
  z-index: 0;
}

.switch {
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  width: 400px;
  padding: 50px;
  z-index: 200;
  transition: 1.25s;
  background-color: #ecf0f3;
  overflow: hidden;
  box-shadow: 4px 4px 10px #d1d9e6;
}

.switch_circle {
  position: absolute;
  width: 500px;
  height: 500px;
  border-radius: 50%;
  background-color: #ecf0f3;
  box-shadow: inset 8px 8px 12px #b8bec7;
  bottom: -60%;
  left: -60%;
  transition: 1.25s;
}

.switch_circle-t {
  top: -30%;
  left: 60%;
  width: 300px;
  height: 300px;
}

.switch_container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  position: absolute;
  width: 400px;
  padding: 50px 55px;
  transition: 1.25s;
}

.switch_button {
  cursor: pointer;
}

.switch_button:hover,
.submit:hover {
  box-shadow: 6px 6px 10px #d1d9e6;
  transform: scale(0.985);
  transition: 0.25s;
}

.switch_button:active,
.switch_button:focus {
  box-shadow: 2px 2px 6px #d1d9e6;
  transform: scale(0.97);
  transition: 0.25s;
}

.is-txr {
  left: calc(100% - 400px);
  transition: 1.25s;
  transform-origin: left;
}

.is-txl {
  left: 0;
  transition: 1.25s;
  transform-origin: right;
}

.is-z {
  z-index: 200;
  transition: 1.25s;
}

.is-hidden {
  visibility: hidden;
  opacity: 0;
  position: absolute;
  transition: 1.25s;
}

.is-gx {
  animation: is-gx 1.25s;
}

@keyframes is-gx {

  0%,
  10%,
  100% {
    width: 400px;
  }

  30%,
  50% {
    width: 500px;
  }
}
</style>