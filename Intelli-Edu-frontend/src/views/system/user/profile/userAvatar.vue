<template>
  <div class="user-info-head" @click="editCropper()">
    <img :src="avatarUrl" title="点击更改头像" class="img-circle img-lg" />
    <el-dialog :title="title" v-model="open" width="800px" append-to-body @opened="modalOpened" @close="closeDialog">
      <el-row>
        <el-col :xs="24" :md="12" :style="{ height: '350px' }">
          <vue-cropper
            ref="cropper"
            :img="options.img"
            :info="true"
            :autoCrop="options.autoCrop"
            :autoCropWidth="options.autoCropWidth"
            :autoCropHeight="options.autoCropHeight"
            :fixedBox="options.fixedBox"
            :outputType="options.outputType"
            @realTime="realTime"
            v-if="visible"
          />
        </el-col>
        <el-col :xs="24" :md="12" :style="{ height: '350px' }">
          <div class="avatar-upload-preview">
            <img :src="options.previews.url" :style="options.previews.img" />
          </div>
        </el-col>
      </el-row>
      <br />
      <el-row>
        <el-col :lg="2" :md="2">
          <el-upload
            action="#"
            :http-request="requestUpload"
            :show-file-list="false"
            :before-upload="beforeUpload"
          >
            <el-button>
              选择
              <el-icon class="el-icon--right"><Upload /></el-icon>
            </el-button>
          </el-upload>
        </el-col>
        <el-col :lg="{ span: 1, offset: 2 }" :md="2">
          <el-button icon="Plus" @click="changeScale(1)"></el-button>
        </el-col>
        <el-col :lg="{ span: 1, offset: 1 }" :md="2">
          <el-button icon="Minus" @click="changeScale(-1)"></el-button>
        </el-col>
        <el-col :lg="{ span: 1, offset: 1 }" :md="2">
          <el-button icon="RefreshLeft" @click="rotateLeft()"></el-button>
        </el-col>
        <el-col :lg="{ span: 1, offset: 1 }" :md="2">
          <el-button icon="RefreshRight" @click="rotateRight()"></el-button>
        </el-col>
        <el-col :lg="{ span: 2, offset: 6 }" :md="2">
          <el-button type="primary" @click="uploadImg()">提 交</el-button>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import "vue-cropper/dist/index.css"
import { VueCropper } from "vue-cropper"
import { useUserStore } from '@/stores/user'
import { presignImage, confirmUpload } from '@/api/resource/resource'
import { updateAvatar } from '@/api/user/user'
import { ElMessage } from "element-plus"
import { ref, reactive, computed } from "vue"
import axios from 'axios'

const userStore = useUserStore()

const open = ref(false)
const visible = ref(false)
const title = ref("修改头像")
const cropper = ref()

const avatarUrl = computed(() => userStore.userInfo?.avatarUrl || '')

const options = reactive({
  img: '',
  autoCrop: true,
  autoCropWidth: 200,
  autoCropHeight: 200,
  fixedBox: true,
  outputType: "png",
  filename: 'avatar',
  previews: {} as any
})

function editCropper() {
  options.img = avatarUrl.value
  open.value = true
}

function modalOpened() {
  visible.value = true
}

function requestUpload() {}

function rotateLeft() {
  cropper.value?.rotateLeft()
}

function rotateRight() {
  cropper.value?.rotateRight()
}

function changeScale(num: number) {
  num = num || 1
  cropper.value?.changeScale(num)
}

function beforeUpload(file: File) {
  if (file.type.indexOf("image/") === -1) {
    ElMessage.error("文件格式错误，请上传图片类型,如：JPG，PNG后缀的文件。")
  } else {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => {
      options.img = reader.result as string
      options.filename = file.name
    }
  }
}

async function uploadImg() {
  cropper.value?.getCropBlob(async (data: Blob) => {
    try {
      const uploadFileName = options.filename.replace(/\.[^.]+$/, '') + '.png'
      const presignResult = await presignImage({
        fileName: uploadFileName,
        fileSize: data.size
      })

      await axios.put(presignResult.uploadUrl, data, {
        headers: { 'Content-Type': 'image/png' }
      })

      await confirmUpload({ resourceId: presignResult.resourceId })

      await updateAvatar(presignResult.accessUrl)
      await userStore.fetchUserInfo()

      open.value = false
      visible.value = false
      ElMessage.success("头像上传成功")
    } catch (error: any) {
      ElMessage.error(error?.message || "头像上传失败")
    }
  })
}

function realTime(data: any) {
  options.previews = data
}

function closeDialog() {
  options.img = avatarUrl.value
  visible.value = false
}
</script>

<style lang='scss' scoped>
.user-info-head {
  position: relative;
  display: inline-block;
  height: 120px;
}

.user-info-head:hover:after {
  content: "+";
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  color: #eee;
  background: rgba(0, 0, 0, 0.5);
  font-size: 24px;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  cursor: pointer;
  line-height: 110px;
  border-radius: 50%;
}
</style>
