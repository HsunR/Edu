<template>
  <div class="loginBox">
    <div class="shell">
      <!-- 登录 -->
      <div class="container a-container" :class="{ 'is-txl': !isLoginVisible }">
        <el-form class="form" :model="loginForm" :rules="loginRules" ref="loginFormRef" @submit.prevent="handleLogin">
          <h2 class="form_title title">登录</h2>

          <!-- 手机号码登录 -->
          <el-form-item v-if="loginForm.loginType === LoginType.Mobile" prop="mobile">
            <el-input v-model="loginForm.mobile" class="form_input" placeholder="手机号码" :prefix-icon="Iphone" />
          </el-form-item>

          <!-- 邮箱登录 -->
          <el-form-item v-else-if="loginForm.loginType === LoginType.Email" prop="email">
            <el-input v-model="loginForm.email" class="form_input" placeholder="邮箱" :prefix-icon="Message" />
          </el-form-item>

          <!-- 微信登录 -->
          <el-form-item v-else-if="loginForm.loginType === LoginType.WeChat" prop="openId">
            <el-input v-model="loginForm.openId" class="form_input" placeholder="微信" :prefix-icon="Message" />
          </el-form-item>

          <!-- 用户名登录 -->
          <el-form-item v-else-if="loginForm.loginType === LoginType.Username" prop="username">
            <el-input v-model="loginForm.username" class="form_input" placeholder="用户名" :prefix-icon="User" />
          </el-form-item>

          <!-- 密码 -->
          <el-form-item v-if="loginForm.loginType === LoginType.Username"  prop="password">
            <el-input v-model="loginForm.password" class="form_input" type="password" placeholder="密码"
              :prefix-icon="Lock" show-password />
          </el-form-item>

          <!-- 验证码 -->
          <el-form-item v-if="loginForm.loginType === LoginType.Mobile || loginForm.loginType === LoginType.Email" prop="code">
            <el-input v-model="loginForm.code" class="form_input" placeholder="请输入验证码" style="width: 230px;" />
            <el-button type="primary" style="margin-left: 10px; width: 160px;" @click="LoginCode"
              :disabled="isCountingLogin">
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

          <el-form-item prop="name">
            <el-input v-model="registerForm.name" class="form_input" placeholder="姓名" :prefix-icon="User" />
          </el-form-item>

          <!-- 密码 -->
          <el-form-item prop="password">
            <el-input v-model="registerForm.password" class="form_input" type="password" placeholder="密码"
                      :prefix-icon="Lock" show-password />
          </el-form-item>

          <!--    学号      -->
          <el-form-item prop="studentNo">
            <el-input v-model="registerForm.studentNo" class="form_input" placeholder="学号" :prefix-icon="Tickets" />
          </el-form-item>

          <!-- 手机号码注册 v-if="registerForm.registerType === 1"-->
          <el-form-item prop="mobile" v-if="registerForm.registerType === RegisterType.Mobile">
            <el-input v-model="registerForm.mobile" class="form_input" placeholder="手机号码" :prefix-icon="Iphone" />
          </el-form-item>

          <!-- 邮箱注册 -->
          <el-form-item prop="email" v-else-if="registerForm.registerType === RegisterType.Email">
            <el-input v-model="registerForm.email" class="form_input" placeholder="邮箱" :prefix-icon="Message" />
          </el-form-item>

          <!-- 验证码 -->
          <el-form-item prop="code" v-if="registerForm.registerType === RegisterType.Mobile || registerForm.registerType === RegisterType.Email">
            <el-input v-model="registerForm.code" class="form_input" placeholder="请输入验证码" style="width: 230px;" />
            <el-button type="primary" style="margin-left: 10px;  width: 160px;" @click="RegisterCode"
              :disabled="isCountingRegister">
              {{ codeButtonTextRegister }}
            </el-button>
          </el-form-item>

          <el-form-item prop="major">
            <el-input v-model="registerForm.major" class="form_input" placeholder="专业" />
          </el-form-item>

          <el-form-item prop="grade">
            <el-input v-model="registerForm.grade" class="form_input" placeholder="年级" />
          </el-form-item>

          <el-form-item prop="school">
            <el-input v-model="registerForm.school" class="form_input" placeholder="学校" />
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

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { sendLoginCode, sendRegisterCode, register as registerApi } from '@/api/user/auth'
import { LoginType, RegisterType } from '@/types/enums'
import { useCountdown } from '@/composables/useCountdown'
import {
  User,
  Message,
  Iphone,
  Lock,
  Tickets
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isLoginVisible = ref(true)

const loginFormRef = ref(null)
const registerFormRef = ref(null)

const loginForm = reactive({
  loginType: LoginType.Username as LoginType,
  username: '',
  password: '',
  mobile: '',
  email: '',
  code: '',
  openId: ''
})

const registerForm = reactive({
  name: '',
  studentNo: '',
  password: '',
  registerType: RegisterType.Mobile as RegisterType,
  mobile: '',
  email: '',
  code: '',
  grade: '',
  major: '',
  enrollmentYear: '',
  school: ''
})

const loading = ref(false)

const { isCounting: isCountingLogin, buttonText: codeButtonText, start: startLoginCountdown } = useCountdown(60)
const { isCounting: isCountingRegister, buttonText: codeButtonTextRegister, start: startRegisterCountdown } = useCountdown(60)

const loginByPhone = () => {
  loginForm.loginType = LoginType.Mobile
  ElMessage.info('已切换为手机号登录')
}

const loginByEmail = () => {
  loginForm.loginType = LoginType.Email
  ElMessage.info('已切换为邮箱登录')
}

const loginByWeChat = () => {
  loginForm.loginType = LoginType.WeChat
  ElMessage.info('微信登录功能尚未开发')
}

const loginByUser = () => {
  loginForm.loginType = LoginType.Username
  ElMessage.info('已切换为用户名密码登录')
}

const registerByPhone = () => {
  registerForm.registerType = RegisterType.Mobile
  ElMessage.info('已切换为手机号注册')
}

const registerByEmail = () => {
  registerForm.registerType = RegisterType.Email
  ElMessage.info('已切换为邮箱注册')
}

const registerByWechat = () => {
  registerForm.registerType = RegisterType.Wechat
  ElMessage.info('微信注册功能尚未开发')
}

const toggleForm = () => {
  isLoginVisible.value = !isLoginVisible.value
}

const loginRules = reactive({
  mobile: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码格式', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  openId: [
    { required: true, message: '请输入微信OpenID', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在2到20个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20之间', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
})

const registerRules = reactive({
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在2到20个字符之间', trigger: 'blur' }
  ],
  studentNo: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  mobile: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码格式', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20之间', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
})

const LoginCode = async () => {
  if (loginForm.loginType === LoginType.Mobile || loginForm.loginType === LoginType.Email) {
    try {
      await sendLoginCode({
        loginType: loginForm.loginType,
        mobile: loginForm.mobile,
        email: loginForm.email
      }, { showError: false })
      ElMessage.success('验证码已发送')
      startLoginCountdown()
    } catch (error) {
      const msg = error instanceof Error ? error.message : '发送验证码失败'
      ElMessage.error(msg)
    }
  }
}

const RegisterCode = async () => {
  if (registerForm.registerType === RegisterType.Mobile || registerForm.registerType === RegisterType.Email) {
    try {
      await sendRegisterCode({
        registerType: registerForm.registerType,
        mobile: registerForm.mobile,
        email: registerForm.email
      }, { showError: false })
      ElMessage.success('验证码已发送')
      startRegisterCountdown()
    } catch (error) {
      const msg = error instanceof Error ? error.message : '发送验证码失败'
      ElMessage.error(msg)
    }
  }
}

const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.error('请填写完整')
      return
    }
    loading.value = true
    try {
      await userStore.login(loginForm, { showError: false })
      const redirect = route.query.redirect || '/'
      router.push(redirect)
    } catch (error) {
      const msg = error instanceof Error ? error.message : '登录失败'
      ElMessage.error(msg)
    } finally {
      loading.value = false
    }
  })
}

const handleRegister = () => {
  registerFormRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }
    loading.value = true
    try {
      await registerApi(registerForm, { showError: false })
      ElMessage.success('注册成功，请登录')
      isLoginVisible.value = true
      resetForm()
    } catch (error) {
      const msg = error instanceof Error ? error.message : '注册失败'
      ElMessage.error(msg)
    } finally {
      loading.value = false
    }
  })
}

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
  width: 1100px;
  min-width: 1100px;
  min-height: 850px;
  height: 700px;
  padding: 25px;
  background-color: #ecf0f3;
  box-shadow: 10px 10px 10px 10px #d1d9e6;
  border-radius: 12px;
  overflow: hidden;
  transform: scale(0.8);
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
  transition: 0.25s;
  cursor: pointer;
  transform: scale(1.2);
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
  width: 400px;
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
  color: #2d2d2d;
  font-weight: 600;
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
  font-size: 18px;
  letter-spacing: 0.25px;
  line-height: 1.6;
  text-align: center;
  color: #2d2d2d;
  font-weight: 600;
}

.button {
  width: 200px;
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