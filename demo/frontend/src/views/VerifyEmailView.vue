<template>
  <div class="relative flex items-center justify-center min-h-[80vh] w-full">
    <div class="absolute inset-0 overflow-hidden pointer-events-none z-0">
      <div class="absolute top-1/4 left-[10%] w-64 h-64 rounded-full bg-orange-100/50 blur-3xl"></div>
      <div class="absolute bottom-1/4 right-[10%] w-72 h-72 rounded-full bg-yellow-100/40 blur-3xl"></div>
    </div>

    <div class="relative w-full max-w-lg bg-white rounded-[24px] shadow-[0_8px_30px_rgb(0,0,0,0.04)] border border-gray-100/60 overflow-hidden z-10">
      <div class="bg-gradient-to-br from-orange-500 to-orange-400 p-8 text-center relative overflow-hidden">
        <div class="absolute top-0 right-0 -mt-4 -mr-4 w-24 h-24 bg-white/10 rounded-full blur-xl"></div>
        <div class="absolute bottom-0 left-0 -mb-4 -ml-4 w-20 h-20 bg-black/5 rounded-full blur-lg"></div>

        <h2 class="text-2xl font-bold text-white tracking-wide relative z-10">メール認証</h2>
        <p class="text-orange-50 mt-2 text-sm font-medium relative z-10">アカウントの有効化を確認しています</p>
      </div>

      <div class="p-8 space-y-5">
        <div v-if="isLoading" class="text-center text-gray-500 font-medium">認証を処理中です...</div>

        <div v-if="message" class="p-4 bg-green-50/80 border border-green-100 text-green-700 text-sm font-bold rounded-xl text-center">
          {{ message }}
        </div>

        <div v-if="errorMessage" class="p-4 bg-red-50/80 border border-red-100 text-red-700 text-sm font-bold rounded-xl text-center">
          {{ errorMessage }}
        </div>

        <div class="text-center pt-2">
          <RouterLink to="/login" class="inline-flex items-center justify-center px-5 py-3 bg-gray-900 text-white font-bold rounded-xl hover:bg-black transition-colors">
            ログイン画面へ戻る
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

import { verifyEmail } from '../api/user'

const route = useRoute()
const isLoading = ref(true)
const message = ref('')
const errorMessage = ref('')

onMounted(async () => {
  const token = typeof route.query.token === 'string' ? route.query.token : ''

  if (!token) {
    errorMessage.value = '認証トークンが見つかりません。'
    isLoading.value = false
    return
  }

  try {
    const response = await verifyEmail(token)
    message.value = response.message || 'メール認証が完了しました。ログインして利用を開始してください。'
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'メール認証に失敗しました。'
  } finally {
    isLoading.value = false
  }
})
</script>
