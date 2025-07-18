<script setup>
import { ref, onMounted } from 'vue'
import { marked } from 'marked';
import api from '@/temp/ai/index'
const {
  fileUploadController,
  chatListController,
  aiCourseController, } = api

const inputMessage = ref('');
const messages = ref([])
const userAvatar = ref(localStorage.getItem("headPortrait")) || ref('@/assets/images/userAvatar.png')
const botAvatar = ref('@/assets/images/botAvatar.png')
const loading = ref(false)

// 解析消息内容
const parseMessageContent = (content) => {
  const parts = []
  let remaining = content
  while (remaining.includes("<think>") && remaining.includes("</think>")) {
    const thinkStart = remaining.indexOf("<think>")
    const thinkEnd = remaining.indexOf("</think>") + "</think>".length

    if (thinkStart > 0) {
      parts.push({ text: remaining.slice(0, thinkStart), isThink: false })
    }

    const thinkContent = remaining.slice(thinkStart + "<think>".length, thinkEnd - "</think>".length)
    parts.push({ text: thinkContent, isThink: true })

    remaining = remaining.slice(thinkEnd)
  }

  if (remaining) {
    parts.push({ text: remaining, isThink: false })
  }
  return parts
}
// 渲染markdown
const renderMarkdown = (content) => {
  return marked(content)
}

// 发送消息 并接收AI回复
const sendMessage = async () => {
  if (!inputMessage.value.trim()) return;

  // 添加用户消息
  const userMessage = {
    sender: 'user',
    content: inputMessage.value,
    loading: false
  };
  messages.value.push(userMessage);

  // 添加bot消息，初始状态为loading
  const botMessage = {
    sender: 'bot',
    content: '',
    loading: true
  };
  messages.value.push(botMessage);

  const userText = inputMessage.value;
  inputMessage.value = '';

  try {
    const eventSource = new EventSource(`/api/ai/aiCourse/doChatWithToolAndRagByStream?userPrompt=${encodeURIComponent(userText)}&chatId=1`);

    eventSource.onmessage = (event) => {
      try {
        const parsedData = JSON.parse(event.data);
        if (parsedData.data) {
          const lastBotMessage = messages.value[messages.value.length - 1];
          lastBotMessage.content += parsedData.data;
          lastBotMessage.loading = false;
        }
      } catch (e) {
        console.error('解析错误:', e);
      }
    };

    eventSource.onerror = () => {
      const lastBotMessage = messages.value[messages.value.length - 1];
      lastBotMessage.loading = false; 
      eventSource.close();
    };

  } catch (error) {
    console.error('请求失败:', error);
    const lastBotMessage = messages.value[messages.value.length - 1];
    lastBotMessage.loading = false;
  }
};

// 随机生成会话ID
const generateConversationId = () => {
  return 'conv-' + Date.now() + '-' + Math.random().toString(36).slice(2, 11);
};

// 根据会话ID获取聊天记录
const getChatListByConversationId = async (conversationId) => {
  try {
    const res = await chatListController.getChatListByConversationId(conversationId)
    console.log(res)
    // messages.value = res.data?.slrt((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  } catch (error) {
    console.error(error)
  }
}
// 获取当前用户的聊天会话列表
const getUserChatList = async () => {
  try {
    const userId = localStorage.getItem('userId')
    const res = await chatListController.getUserChatList(userId)
    console.log(res)
  } catch (error) {
    console.error(error)
  }
}

onMounted(async() => {
  const conversationId = localStorage.getItem('LastConversationId') || ''
  if (conversationId) {
    await getChatListByConversationId(conversationId)
  } else {
    createChat()
  }
  await getUserChatList()
})

const count = ref(0)
const load = () => {
  count.value += 2
}

// 创建聊天会话
const createChat = async () => {
  const userId = localStorage.getItem('userId')
  const conversationId = generateConversationId()
  localStorage.setItem('LastConversationId', conversationId)
  const chatListAddRequest = {
    "userId": userId,
    "conversationId": conversationId,
    "conversationTitle": ""
  }
  const res = await chatListController.createChatList(chatListAddRequest)
  console.log(res)
}
</script>

<template>
  <div class="chat-container">
    <!-- 历史记录 + 创建新窗口 -->
    <div class="chat-left">
      <div class="chat-left-top">
        <el-button @click="createChat" class="btn">创建新对话</el-button>
      </div>

      <div class="chat-history">
        <ul v-infinite-scroll="load" class="infinite-list" style="overflow: auto">
          <li v-for="i in count" :key="i" class="infinite-list-item" style="position: relative;">
            {{ i }}
            <el-button class="more"
              style="margin-left: 10px;position: absolute; top: 5px; right: 5px; width: 10px; border: none; background-color: var(--el-color-primary-light-9);"><el-icon>
                <MoreFilled />
              </el-icon></el-button>
          </li>
        </ul>
      </div>
    </div>

    <!--  聊天窗口  -->
    <div class="chat-box" ref="chatBox">
      <div class="message-content-wrapp">
        <!-- 已有消息列表 -->
        <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.sender]">
          <img :src="msg.sender === 'user' ? userAvatar : botAvatar" alt="avatar" :class="['avatar', msg.sender]">
          <div class="message-content">
            <template v-if="msg.sender === 'bot'">
              <span v-for="(part, index) in parseMessageContent(msg.content)" :key="index"
                :class="part.isThink && part.text.trim() ? 'think-content' : ''">
                <!-- 思考内容 -->
                <span v-if="part.isThink && part.text.trim()">
                  <span v-html="renderMarkdown(part.text)"></span>
                </span>
                <!-- 回答内容 -->
                <span v-else>
                  <span v-html="renderMarkdown(part.text)"></span>
                </span>
                <br v-if="part.isThink && part.text.trim()">
              </span>
            </template>
            <!-- 用户提问内容 -->
            <template v-else>
              {{ msg.content }}
            </template>

            <!-- 添加加载状态指示器 -->
            <div v-if="msg.sender === 'bot' && msg.loading" class="thinking-indicator">
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="dot"></span>
              <span>正在思考...</span>
            </div>
          </div>
        </div>
      </div>
    </div>


    <!--  输入区域  -->
    <div class="input-area">
      <el-input v-model="inputMessage" placeholder="请输入内容" class="message-input" @keyup.enter="sendMessage"
        type="textarea" :autosize="{ minRows: 4, maxRows: 6 }" resize="none"></el-input>
      <!-- <el-upload v-model:file-list="fileList" class="upload-demo"
        action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15" multiple :on-preview="handlePreview"
        :on-remove="handleRemove" :before-remove="beforeRemove" :limit="3" :on-exceed="handleExceed"> -->
      <el-button class="upload-btn"><el-icon>
          <UploadFilled />
        </el-icon></el-button>
      <!-- </el-upload> -->
      <el-button @click="sendMessage" class="send-button">发送</el-button>
    </div>

  </div>
</template>

<style scoped lang="scss">
.chat-container {
  position: relative;
  height: calc(90vh - 16px);
  background: linear-gradient(to right, #eff7fd, #f9f2f5);
  display: flex;
  margin: auto;
  border-radius: 8px;

  .chat-left {
    width: 180px;
    height: 100%;

    .chat-left-top {
      margin: 10px;
      margin-left: 40px;

      .btn {
        background: linear-gradient(to right, #eff7fd, #f9f2f5);
        border-radius: 20px;

        &:hover {
          background: linear-gradient(to right, #f9f2f5, #eff7fd);
          color: #409eff;
        }
      }

      .infinite-list-item {
        position: relative;

        .more {
          position: absolute;
          top: 10px;
          right: 3px;
          width: 10px;
        }
      }



    }
  }

  .chat-box {
    flex: 1;
    padding: 10px;
    overflow-y: auto;
    padding-bottom: 20vh;
    word-break: break-word;
    overflow-wrap: break-word;
    white-space: pre-wrap;
    max-width: 90vh;
    overflow-x: hidden;

    .message-content-wrapp {
      max-height: 65vh;
      overflow-y: auto;
    }

    .message {
      margin-bottom: 10px;
      display: flex;
      justify-content: flex-start;
      /* 默认左对齐 */
      align-items: flex-start;

    }

    .message.user {
      justify-content: flex-end;
      /* 用户消息右对齐 */
    }

    .message.bot .message-content {
      justify-content: flex-start;
    }

    .message-content-wrapper {
      display: flex;
      align-items: flex-start;
    }

    .avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      margin: 0 10px;
    }

    .avatar.user {
      order: 2;
    }

    .message-content {
      padding: 10px 20px;
      border-radius: 10px;
      background-color: #fff;

      p {
        width: 40vh;
      }
    }
  }



}

.input-area {
  position: absolute;
  bottom: 30px;
  left: 180px;
  display: flex;
  width: 60%;
  margin: 30px;
  border: 2px solid #eff7fd;
  border-radius: 50px;

  .message-input {
    width: 100%;

    &:hover {
      border: #eff7fd;
    }
  }

  .upload-btn {
    position: absolute;
    bottom: 1vh;
    right: 12vh;
    width: 5vh;
    border-radius: 10px;
  }

  .send-button {
    position: absolute;
    bottom: 1vh;
    right: 1vh;
    width: 10vh;
    background: linear-gradient(to right, #eff7fd, #f9f2f5);
    border-radius: 20px;

    &:hover {
      background: linear-gradient(to right, #f9f2f5, #eff7fd);
      color: #409eff;
    }
  }
}

/* 思考指示器样式 */
.thinking-indicator {
  display: flex;
  align-items: center;
  margin-top: 8px;
  color: #999;
  font-size: 14px;
}

.thinking-indicator .dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #999;
  margin-right: 6px;
  animation: bounce 1.4s infinite ease-in-out;
}

.thinking-indicator .dot:nth-child(1) {
  animation-delay: -0.32s;
}

.thinking-indicator .dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {

  0%,
  80%,
  100% {
    transform: translateY(0);
  }

  40% {
    transform: translateY(-8px);
  }
}
</style>
