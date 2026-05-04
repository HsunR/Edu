<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import type { VideoMetaVO } from '@/api/resource/types'

const props = withDefaults(defineProps<{
  src: string
  poster?: string
  meta?: VideoMetaVO | null
  autoplay?: boolean
}>(), {
  autoplay: false
})

const videoRef = ref<HTMLVideoElement | null>(null)
const playing = ref(false)
const currentTime = ref(0)
const duration = ref(0)
const volume = ref(1)
const muted = ref(false)

function togglePlay() {
  if (!videoRef.value) return
  if (playing.value) {
    videoRef.value.pause()
  } else {
    videoRef.value.play()
  }
}

function handlePlay() {
  playing.value = true
}

function handlePause() {
  playing.value = false
}

function handleTimeUpdate() {
  if (videoRef.value) {
    currentTime.value = videoRef.value.currentTime
  }
}

function handleLoadedMetadata() {
  if (videoRef.value) {
    duration.value = videoRef.value.duration
  }
}

function handleSeek(val: number) {
  if (videoRef.value) {
    videoRef.value.currentTime = val
  }
}

function handleVolumeChange(val: number) {
  volume.value = val
  if (videoRef.value) {
    videoRef.value.volume = val
  }
}

function toggleMute() {
  muted.value = !muted.value
  if (videoRef.value) {
    videoRef.value.muted = muted.value
  }
}

function toggleFullscreen() {
  if (!videoRef.value) return
  const container = videoRef.value.parentElement
  if (!container) return
  if (document.fullscreenElement) {
    document.exitFullscreen()
  } else {
    container.requestFullscreen()
  }
}

function formatTime(seconds: number): string {
  const m = Math.floor(seconds / 60)
  const s = Math.floor(seconds % 60)
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

onMounted(() => {
  if (videoRef.value) {
    videoRef.value.volume = volume.value
  }
})
</script>

<template>
  <div class="video-player">
    <div class="player-wrapper">
      <video
        ref="videoRef"
        :src="src"
        :poster="poster || meta?.coverUrl"
        :autoplay="autoplay"
        preload="metadata"
        @play="handlePlay"
        @pause="handlePause"
        @timeupdate="handleTimeUpdate"
        @loadedmetadata="handleLoadedMetadata"
        @click="togglePlay"
      />
      <div v-if="!playing" class="play-overlay" @click="togglePlay">
        <div class="play-btn">▶</div>
      </div>
    </div>

    <div class="player-controls">
      <el-slider
        :model-value="currentTime"
        :max="duration || 100"
        :show-tooltip="false"
        size="small"
        @change="handleSeek"
        style="flex: 1; margin: 0 8px"
      />
      <div class="controls-bar">
        <el-button size="small" link @click="togglePlay">
          {{ playing ? '⏸' : '▶' }}
        </el-button>
        <span class="time-display">
          {{ formatTime(currentTime) }} / {{ formatTime(duration) }}
        </span>
        <div class="volume-control">
          <el-button size="small" link @click="toggleMute">
            {{ muted ? '🔇' : '🔊' }}
          </el-button>
          <el-slider
            :model-value="muted ? 0 : volume"
            :max="1"
            :step="0.1"
            :show-tooltip="false"
            size="small"
            style="width: 80px"
            @change="handleVolumeChange"
          />
        </div>
        <el-button size="small" link @click="toggleFullscreen">⛶</el-button>
      </div>
    </div>

    <div v-if="meta" class="player-meta">
      <span v-if="meta.duration">时长：{{ formatTime(meta.duration) }}</span>
      <span v-if="meta.definition">清晰度：{{ meta.definition }}</span>
      <span v-if="meta.transcodeStatus !== undefined">
        转码状态：
        <el-tag :type="meta.transcodeStatus === 1 ? 'success' : meta.transcodeStatus === 0 ? 'info' : 'warning'" size="small">
          {{ meta.transcodeStatus === 0 ? '未转码' : meta.transcodeStatus === 1 ? '已完成' : '转码中' }}
        </el-tag>
      </span>
    </div>
  </div>
</template>

<style scoped lang="scss">
.video-player {
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}

.player-wrapper {
  position: relative;
  width: 100%;
  background: #000;

  video {
    width: 100%;
    display: block;
  }

  .play-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, 0.3);
    cursor: pointer;

    .play-btn {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.9);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      color: #303133;
      transition: transform 0.2s;

      &:hover {
        transform: scale(1.1);
      }
    }
  }
}

.player-controls {
  background: #1a1a1a;
  padding: 4px 12px 8px;
}

.controls-bar {
  display: flex;
  align-items: center;
  gap: 8px;

  .time-display {
    font-size: 12px;
    color: #fff;
    min-width: 90px;
  }

  .volume-control {
    display: flex;
    align-items: center;
    gap: 4px;
    margin-left: auto;
  }
}

.player-meta {
  background: #1a1a1a;
  padding: 8px 12px;
  display: flex;
  gap: 16px;

  span {
    font-size: 12px;
    color: #aaa;
  }
}
</style>
