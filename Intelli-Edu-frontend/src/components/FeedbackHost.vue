<script setup lang="ts">
import { AlertCircle, CheckCircle2, Info, X } from 'lucide-vue-next'
import { useUiStore } from '@/stores/ui'

const ui = useUiStore()
const icons = { success: CheckCircle2, error: AlertCircle, info: Info }
</script>

<template>
  <Teleport to="body">
    <div
      class="pointer-events-none fixed right-4 top-4 z-[80] flex w-[min(24rem,calc(100vw-2rem))] flex-col gap-2"
      aria-live="polite"
    >
      <TransitionGroup name="toast">
        <div
          v-for="toast in ui.toasts"
          :key="toast.id"
          :class="[
            'pointer-events-auto flex items-start gap-3 rounded-2xl border bg-white p-4 shadow-xl',
            toast.tone === 'error' ? 'border-red-200' : '',
          ]"
          role="status"
        >
          <component
            :is="icons[toast.tone]"
            :class="[
              'mt-0.5 shrink-0',
              toast.tone === 'success'
                ? 'text-leaf'
                : toast.tone === 'error'
                  ? 'text-red-600'
                  : 'text-blue-600',
            ]"
            :size="18"
          />
          <p class="flex-1 text-sm leading-5">{{ toast.message }}</p>
          <button
            class="-mr-1 -mt-1 grid size-7 place-items-center rounded-full hover:bg-[#f0f2ed]"
            aria-label="关闭提示"
            @click="ui.dismiss(toast.id)"
          >
            <X :size="14" />
          </button>
        </div>
      </TransitionGroup>
    </div>

    <Transition name="fade">
      <div
        v-if="ui.confirmation"
        class="fixed inset-0 z-[70] grid place-items-center bg-ink/40 p-5 backdrop-blur-sm"
        role="presentation"
        @click.self="ui.answerConfirmation(false)"
        @keydown.esc="ui.answerConfirmation(false)"
      >
        <section
          class="card w-full max-w-md p-7"
          role="alertdialog"
          aria-modal="true"
          :aria-labelledby="`confirm-title`"
          tabindex="-1"
        >
          <h2 id="confirm-title" class="text-xl font-black">{{ ui.confirmation.title }}</h2>
          <p class="mt-3 text-sm leading-6 text-[#6f7872]">{{ ui.confirmation.message }}</p>
          <div class="mt-7 flex justify-end gap-2">
            <button class="btn-secondary" @click="ui.answerConfirmation(false)">取消</button>
            <button
              :class="['btn-primary', ui.confirmation.danger ? 'bg-red-600 hover:bg-red-700' : '']"
              autofocus
              @click="ui.answerConfirmation(true)"
            >
              {{ ui.confirmation.confirmLabel }}
            </button>
          </div>
        </section>
      </div>
    </Transition>
  </Teleport>
</template>
