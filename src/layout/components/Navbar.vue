<template>
  <div class="navbar">
    <div class="navbar-left">
      <div class="logo-area" @click="goHome">
        <svg-icon icon-class="education" class="logo-icon" />
        <span class="logo-text">Intelli-Edu</span>
      </div>
    </div>

    <div class="right-menu">
      <template v-if="appStore.device !== 'mobile'">
        <div class="invite right-menu-item">
          <span @click="inviteFormVisible = true">输入邀请码</span>
        </div>

        <el-tooltip content="主题模式" effect="dark" placement="bottom">
          <div class="right-menu-item hover-effect theme-switch-wrapper" @click="toggleTheme">
            <svg-icon v-if="settingsStore.isDarkMode" icon-class="sunny" />
            <svg-icon v-if="!settingsStore.isDarkMode" icon-class="moon" />
          </div>
        </el-tooltip>
      </template>

      <el-dropdown @command="handleCommand" class="avatar-container right-menu-item hover-effect" trigger="hover">
        <div class="avatar-wrapper">
          <el-avatar v-if="userStore.userInfo?.nickname" class="user-avatar"> {{ userStore.userInfo?.nickname }} </el-avatar>
          <el-avatar v-else class="user-avatar" :icon="UserFilled"/>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <router-link to="/user/profile">
              <el-dropdown-item>个人中心</el-dropdown-item>
            </router-link>
            <el-dropdown-item divided command="logout">
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <el-dialog v-model="inviteFormVisible" title="请输入邀请码" width="45%" center :before-close="handleCancel" style="border-radius: 20px;">
      <el-form :model="inviteForm">
        <el-form-item>
          <el-input v-model="inviteForm.inviteCode" autocomplete="off" placeholder="请输入课程邀请码" style="margin: 10px 50px;height: 40px;line-height: 40px; border-radius: 20px;"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button color="#626aef" plain round @click="handleCancel" style="margin-right: 50px;width: 10vw;">取 消</el-button>
          <el-button color="#626aef" round @click="submitForm" style="width: 10vw;">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { useSettingsStore } from '@/stores/settings'
import { joinClass } from '@/api/course/class'
import { UserFilled } from '@element-plus/icons-vue'

const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()
const settingsStore = useSettingsStore()

function goHome() {
  router.push('/index')
}

function handleCommand(command) {
  switch (command) {
    case "setLayout":
      setLayout()
      break
    case "logout":
      logout()
      break
    default:
      break
  }
}

function logout() {
  ElMessageBox.confirm('确定退出系统吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout().then(() => {
      location.href = '/login'
    })
  }).catch(() => { })
}

const emits = defineEmits(['setLayout'])
function setLayout() {
  emits('setLayout')
}

function toggleTheme() {
  settingsStore.toggleTheme()
}

const inviteFormVisible = ref(false)
const inviteForm = ref({
  inviteCode: ""
})

async function submitForm() {
  try {
    const res = await joinClass({ inviteCode: inviteForm.value.inviteCode })
    if (res) {
      ElMessage.success('加入成功')
      inviteFormVisible.value = false
      inviteForm.value = { inviteCode: "" }
    }
  } catch {
    ElMessage.error('加入失败')
  }
}

function handleCancel() {
  inviteFormVisible.value = false
  inviteForm.value = { inviteCode: "" }
}
</script>

<style lang='scss' scoped>
.navbar {
  height: 56px;
  overflow: hidden;
  position: relative;
  background: var(--navbar-bg);
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}

.navbar-left {
  display: flex;
  align-items: center;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: opacity 0.3s;

  &:hover {
    opacity: 0.8;
  }
}

.logo-icon {
  font-size: 26px;
  color: var(--el-color-primary);
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: var(--el-text-color-primary);
  letter-spacing: 0.5px;
}

.right-menu {
  height: 100%;
  line-height: 56px;
  display: flex;
  align-items: center;

  &:focus {
    outline: none;
  }

  .invite.right-menu-item {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;

    span{
      font-size: 14px;
      font-weight: 600;
      height: 20px;
      line-height: 20px;
    }
  }

  .invite:hover {
    cursor: pointer;
    background-color: #eeeeee4b;
    border-radius:10px ;
  }

  .right-menu-item {
    display: inline-block;
    padding: 0 8px;
    height: 100%;
    font-size: 18px;
    color: #5a5e66;
    vertical-align: text-bottom;

    &.hover-effect {
      cursor: pointer;
      transition: background 0.3s;

      &:hover {
        background: rgba(0, 0, 0, 0.025);
      }
    }

    &.theme-switch-wrapper {
      display: flex;
      align-items: center;

      svg {
        transition: transform 0.3s;

        &:hover {
          transform: scale(1.15);
        }
      }
    }
  }

  .avatar-container {
    margin-right: 0px;
    padding-right: 0px;

    .avatar-wrapper {
      margin-top: 10px;
      right: 5px;
      position: relative;

      .user-avatar {
        cursor: pointer;
        width: 30px;
        height: 30px;
        border-radius: 50%;
      }

      i {
        cursor: pointer;
        position: absolute;
        right: -20px;
        top: 25px;
        font-size: 12px;
      }
    }
  }
}
</style>
