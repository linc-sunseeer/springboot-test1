<template>
  <div class="max-w-4xl mx-auto py-8 px-4">
    <div class="mb-8">
      <h1 class="text-3xl font-semibold text-gray-900 tracking-normal">システム設定</h1>
      <p class="text-gray-500 mt-2">弁当予約システムの基本設定を管理します</p>
    </div>

    <!-- Alert Messages -->
    <div v-if="successMessage" class="mb-6 bg-green-50 border-l-4 border-green-500 p-4 rounded-md flex items-center shadow-sm">
      <svg class="h-5 w-5 text-green-500 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
      <p class="text-sm text-green-700 font-bold">{{ successMessage }}</p>
    </div>
    
    <div v-if="errorMessage" class="mb-6 bg-red-50 border-l-4 border-red-500 p-4 rounded-md flex items-center shadow-sm">
      <svg class="h-5 w-5 text-red-500 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
      <p class="text-sm text-red-700 font-bold">{{ errorMessage }}</p>
    </div>

    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="p-6 md:p-8">
        <h3 class="text-lg font-bold text-gray-900 mb-6 flex items-center gap-2">
          <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
          予約締切時間
        </h3>
        
        <form @submit.prevent="saveTimeSetting" class="max-w-md">
          <div class="mb-6">
            <label for="cutoffTime" class="block text-sm font-semibold text-gray-700 mb-2">毎日の予約受付を終了する時間</label>
            <div class="relative">
              <input 
                id="cutoffTime" 
                type="time" 
                v-model="cutoffTime" 
                required
                class="block w-full rounded-xl border-gray-200 shadow-sm focus:ring-2 focus:ring-bento-orange focus:border-bento-orange transition-shadow px-4 py-3 bg-gray-50"
              />
            </div>
            <p class="mt-2 text-xs text-gray-500">
              この時間を過ぎると、ユーザーは本日の弁当を予約できなくなります。
            </p>
          </div>
          
          <button 
            type="submit" 
            :disabled="isSaving"
            class="px-6 py-2.5 bg-gray-900 hover:bg-gray-800 text-white text-sm font-bold rounded-xl transition-colors shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
          >
            <svg v-if="isSaving" class="animate-spin h-4 w-4 text-white" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
            {{ isSaving ? '保存中...' : '設定を保存' }}
          </button>
        </form>
      </div>
    </div>

    <div class="mt-6 bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="p-6 md:p-8">
        <h3 class="text-lg font-bold text-gray-900 mb-6 flex items-center gap-2">
          <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path></svg>
          最低成団人数
        </h3>

        <form @submit.prevent="saveThresholdSetting" class="max-w-md">
          <div class="mb-6">
            <label for="minThreshold" class="block text-sm font-semibold text-gray-700 mb-2">当日の予約が確定する最低人数</label>
            <div class="relative">
              <input
                id="minThreshold"
                v-model.number="minThreshold"
                type="number"
                min="1"
                max="99"
                required
                class="block w-full rounded-xl border-gray-200 shadow-sm focus:ring-2 focus:ring-bento-orange focus:border-bento-orange transition-shadow px-4 py-3 bg-gray-50"
              />
            </div>
            <p class="mt-2 text-xs text-gray-500">
              この人数に達するまで、ユーザーは予約を確定できません。
            </p>
          </div>

          <button
            type="submit"
            :disabled="isSaving"
            class="px-6 py-2.5 bg-gray-900 hover:bg-gray-800 text-white text-sm font-bold rounded-xl transition-colors shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
          >
            <svg v-if="isSaving" class="animate-spin h-4 w-4 text-white" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
            {{ isSaving ? '保存中...' : '設定を保存' }}
          </button>
        </form>
      </div>
    </div>

    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  fetchMinReservationThreshold,
  fetchReservationCutoffTime,
  updateMinReservationThreshold,
  updateReservationCutoffTime,
} from '../../api/admin'

const cutoffTime = ref('09:45')
const minThreshold = ref(6)
const isSaving = ref(false)
const successMessage = ref('')
const errorMessage = ref('')

async function loadSetting() {
  try {
    const res = await fetchReservationCutoffTime()
    if (res.data) {
      cutoffTime.value = res.data.substring(0, 5) // "09:45:00" -> "09:45"
    }

    const thresholdRes = await fetchMinReservationThreshold()
    if (thresholdRes.data) {
      minThreshold.value = Number(thresholdRes.data)
    }
  } catch (error) {
    console.error("設定の読み込みに失敗しました", error)
  }
}

async function saveTimeSetting() {
  if (!cutoffTime.value) return
  
  isSaving.value = true
  successMessage.value = ''
  errorMessage.value = ''
  
  try {
    await updateReservationCutoffTime(cutoffTime.value)
    successMessage.value = '予約締切時間を更新しました。'
    setTimeout(() => { successMessage.value = '' }, 3000)
  } catch (error) {
    errorMessage.value = error.message || '設定の保存に失敗しました。'
  } finally {
    isSaving.value = false
  }
}

async function saveThresholdSetting() {
  if (!minThreshold.value || Number(minThreshold.value) <= 0) return

  isSaving.value = true
  successMessage.value = ''
  errorMessage.value = ''

  try {
    await updateMinReservationThreshold(Number(minThreshold.value))
    successMessage.value = '最低成団人数を更新しました。'
    setTimeout(() => { successMessage.value = '' }, 3000)
  } catch (error) {
    errorMessage.value = error.message || '設定の保存に失敗しました。'
  } finally {
    isSaving.value = false
  }
}

onMounted(() => {
  loadSetting()
})
</script>
