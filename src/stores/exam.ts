import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { enterExam, getMySheet, saveAnswer, submitExam } from '@/api/exam/index'
import type { AnswerSheetDetailVO } from '@/api/exam/types'
import { SheetStatus } from '@/types/enums'

export const useExamStore = defineStore('exam', () => {
  const currentSheet = ref<AnswerSheetDetailVO | null>(null)
  const answers = ref<Map<string, string>>(new Map())
  const remainingSeconds = ref(0)
  const countdownTimer = ref<ReturnType<typeof setInterval> | null>(null)

  const isAnswering = computed(() => currentSheet.value?.status === SheetStatus.InProgress)
  const answeredCount = computed(() => answers.value.size)

  async function enterExamAction(examId: string) {
    const sheet = await enterExam(examId)
    if (sheet) {
      currentSheet.value = await getMySheet(examId)
      if (currentSheet.value?.deadline) {
        startCountdown()
      }
    }
  }

  async function fetchMySheet(examId: string) {
    currentSheet.value = await getMySheet(examId)
    if (currentSheet.value?.records) {
      for (const record of currentSheet.value.records) {
        if (record.answerContent) {
          answers.value.set(record.questionId, record.answerContent)
        }
      }
    }
    if (currentSheet.value?.deadline && currentSheet.value.status === SheetStatus.InProgress) {
      startCountdown()
    }
  }

  async function saveAnswerAction(sheetId: string, questionId: string, content: string) {
    answers.value.set(questionId, content)
    await saveAnswer(sheetId, questionId, { answerContent: content })
  }

  async function submitExamAction(sheetId: string) {
    await submitExam(sheetId)
    stopCountdown()
    if (currentSheet.value) {
      currentSheet.value.status = SheetStatus.Ended
    }
  }

  function startCountdown() {
    stopCountdown()
    if (!currentSheet.value?.deadline) return
    const deadline = new Date(currentSheet.value.deadline).getTime()
    const updateTimer = () => {
      const now = Date.now()
      const diff = Math.max(0, Math.floor((deadline - now) / 1000))
      remainingSeconds.value = diff
      if (diff <= 0) {
        stopCountdown()
        if (currentSheet.value?.sheetId) {
          submitExamAction(currentSheet.value.sheetId)
        }
      }
    }
    updateTimer()
    countdownTimer.value = setInterval(updateTimer, 1000)
  }

  function stopCountdown() {
    if (countdownTimer.value) {
      clearInterval(countdownTimer.value)
      countdownTimer.value = null
    }
  }

  function clearExamState() {
    stopCountdown()
    currentSheet.value = null
    answers.value.clear()
    remainingSeconds.value = 0
  }

  return {
    currentSheet,
    answers,
    remainingSeconds,
    isAnswering,
    answeredCount,
    enterExamAction,
    fetchMySheet,
    saveAnswerAction,
    submitExamAction,
    startCountdown,
    stopCountdown,
    clearExamState
  }
})
