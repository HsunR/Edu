<script setup>
import {ref, computed, onMounted} from 'vue';
import { ElMessage, ElLoading } from 'element-plus';
import { useRoute } from 'vue-router'
import axios from 'axios';
import api from "../../../../temp/index";
const { coResourceController } = api;
const route = useRoute()

const uploadRef = ref();
const fileList = ref([]);
const videoUrl = ref('');
const uploading = ref(false);
const progressPercent = ref(0);
const progressStatus = ref('');

// 计算属性
const fileName = computed(() => {
  return fileList.value[0]?.name || '';
});

const progressText = computed(() => {
  return uploading.value ? `上传中 ${progressPercent.value}%` : '';
});

const formData = ref(new FormData());
// 上传前的校验
const beforeUpload = (file) => {
  const validTypes = ['video/mp4', 'video/webm', 'video/ogg'];
  const isVideo = validTypes.includes(file.type);
  formData.value.append('video', file);

  if (!isVideo) {
    ElMessage.error('请上传视频文件（MP4/WebM/OGG）!');
    return false;
  }
  // 显示加载状态
  uploading.value = true;
  progressPercent.value = 0;
  progressStatus.value = '';

  return true;
};

// 上传进度
const handleProgress = (event) => {
  progressPercent.value = Math.floor(event.percent);
};

// 上传成功
const handleSuccess = (response) => {
  console.log(response);
  if (response.data) {
    uploading.value = false;
    progressStatus.value = 'success';
    videoUrl.value = response.data.resourceLink;
    ElMessage.success('视频上传成功!');
  } else {
    uploading.value = false;
    ElMessage.error('上传失败');
  }
  
};

// 上传失败
const handleError = (res) => {
  console.log(res);
  uploading.value = false;
  progressStatus.value = 'exception';
  ElMessage.error('视频上传失败，请重试!');
};

// 删除视频
const handleRemove = () => {
  videoUrl.value = '';
};
let playerOptions = ref({
  // height: 200,
  // width: document.documentElement.clientWidth,
  playbackRates: [0.7, 1.0, 1.5, 2.0], // 播放速度
  autoplay: 'any', // 如果true,浏览器准备好时开始回放。
  muted: true, // 默认情况下将会消除任何音频。
  loop: true, // 导致视频一结束就重新开始。
  preload: 'auto', // 建议浏览器在<video>加载元素后是否应该开始下载视频数据。auto浏览器选择最佳行为,立即开始加载视频（如果浏览器支持）
  language: 'zh-CN',
  aspectRatio: '16:9', // 将播放器置于流畅模式，并在计算播放器的动态大小时使用该值。值应该代表一个比例 - 用冒号分隔的两个数字（例如"16:9"或"4:3"）
  fluid: true, // 当true时，Video.js player将拥有流体大小。换句话说，它将按比例缩放以适应其容器。
  notSupportedMessage: '此视频暂无法播放，请稍后再试', // 允许覆盖Video.js无法播放媒体源时显示的默认信息。
  controls: true,
  controlBar: {
    timeDivider: true,
    durationDisplay: true,
    remainingTimeDisplay: false,
    fullscreenToggle: true // 全屏按钮
  }
})


// 上传文档
const uploadDocumentRef = ref();
const DocumentUrl = ref("");
const fileListDocument = ref([]);

// 上传前的校验
const beforeUploadDocument = (file) => {
  const validTypes = [
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'application/vnd.ms-excel',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'application/vnd.ms-powerpoint',
    'application/vnd.openxmlformats-officedocument.presentationml.presentation',
    'text/plain'
  ];
  const isDocument = validTypes.includes(file.type);

  if (!isDocument) {
    ElMessage.error('请上传文档文件!');
    return false;
  }
  return true;
};

// 删除文档
const handleRemoveDocument = () => {
  DocumentUrl.value = '';
};

const customUpload = async (options) => {
  const formData = new FormData();
  const courseId = route.params.id
  formData.append('courseId', courseId.toString());
  formData.append('file', options.file);

  try {
    const res = await coResourceController.uploadDocument(formData)
    console.log(res);
    if(res.data.data){
      DocumentUrl.value = res.data.data.resourceLink;
      ElMessage.success("上传成功")
    }
    options.onSuccess(res.data);
  } catch (err) {
    const errorMsg = err.response?.data?.message || '上传失败';
    ElMessage.error(`上传失败: ${errorMsg}`);
    options.onError(err);
  }
};
</script>

<template>
  <div>
    课程资料
    <!--    上传视频-->
    <div class="upload-container">
      <el-upload ref="uploadRef" class="uploader"
      action="/api/resource/coresource/upload/video"
      name="video"
      :limit="1"
      :file-list="fileList"
      :before-upload="beforeUpload"
      :on-progress="handleProgress"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-remove="handleRemove"
      :auto-upload="true"
      accept="video/*"
      >
      <template #trigger>
        <el-button type="primary" class="gradient-btn">上传视频</el-button>
      </template>

      <template #default>
        <!-- 上传中的状态 -->
        <div v-if="uploading" class="uploading-container">
          <el-progress
              :percentage="progressPercent"
              :stroke-width="4"
              :status="progressStatus"
          />
          <span class="uploading-text">{{ progressText }}</span>
        </div>

        <!-- 上传完成后的预览 -->
        <div v-if="videoUrl && !uploading" class="video-preview">
          <video-player
              :src="videoUrl"
              :options="playerOptions"
              :volume="0.6"
          />
          <div class="video-info">
            <span>{{ fileName }}</span>
          </div>
        </div>
      </template>

      </el-upload>
    </div>

    <!--    上传文档-->
    <div class="upload-container">
      <el-upload ref="uploadDocumentRef" class="uploader"
                 :http-request="customUpload"
                 :file-list="fileListDocument"
                 :before-upload="beforeUploadDocument"
                 :on-remove="handleRemoveDocument"
                 :auto-upload="true"
                 accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.txt"
      >
        <template #trigger>
          <el-button type="primary" class="gradient-btn">上传文档</el-button>
        </template>
      </el-upload>
    </div>
  </div>
</template>

<style scoped>
.upload-container{
  display: flex;
}
.uploading-text {
  display: block;
  margin-top: 10px;
  color: #67c23a;
}

.video-preview {
  margin-top: 20px;
}

.video-info {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
}
</style>
