<template>
  <div class="relative flex items-center justify-center min-h-[80vh] w-full">
    <div class="absolute inset-0 overflow-hidden pointer-events-none z-0">
      <div class="absolute top-1/4 left-[10%] w-64 h-64 rounded-full bg-orange-100/50 blur-3xl"></div>
      <div class="absolute bottom-1/4 right-[10%] w-72 h-72 rounded-full bg-yellow-100/40 blur-3xl"></div>
    </div>

    <div class="relative w-full max-w-md bg-white rounded-[24px] shadow-[0_8px_30px_rgb(0,0,0,0.04)] border border-gray-100/60 overflow-hidden z-10">
      <div class="bg-gradient-to-br from-orange-500 to-orange-400 p-8 text-center relative overflow-hidden flex flex-col items-center">
        <div class="absolute top-0 right-0 -mt-4 -mr-4 w-24 h-24 bg-white/10 rounded-full blur-xl"></div>
        <div class="absolute bottom-0 left-0 -mb-4 -ml-4 w-20 h-20 bg-black/5 rounded-full blur-lg"></div>

        <div class="w-16 h-16 bg-white rounded-full p-2 shadow-lg mb-3 relative z-10 flex items-center justify-center">
          <span class="text-4xl font-bold text-bento-orange">M</span>
        </div>
        <h2 class="text-2xl font-bold text-white tracking-wide relative z-10">パスワード再設定申請</h2>
        <p class="text-orange-50 mt-2 text-sm font-medium relative z-10">登録済みメールアドレスに再設定用リンクを送信します。</p>
      </div>

      <div class="p-8">
        <div class="mb-6 rounded-2xl border border-orange-100 bg-orange-50/70 px-4 py-3 text-xs leading-6 text-orange-700 font-medium">
          メールアドレスが登録済みの場合のみ、再設定用リンクが送信されます。受信箱に見つからない場合は迷惑メールフォルダも確認してください。
        </div>

        <form class="space-y-5" @submit.prevent="submitRequest">
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-1.5">メールアドレス</label>
            <input
              v-model="email"
              type="email"
              autocomplete="email"
              placeholder="name@example.com"
              required
              class="w-full px-4 py-3.5 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:border-bento-orange focus:ring-4 focus:ring-bento-orange/10 transition-all outline-none font-medium text-gray-800 placeholder-gray-400"
            />
          </div>

          <button
            type="submit"
            :disabled="isSubmitting"
            class="w-full py-4 mt-4 bg-gradient-to-r from-orange-500 to-orange-400 hover:from-orange-600 hover:to-orange-500 text-white font-bold rounded-xl shadow-lg shadow-orange-500/30 hover:shadow-orange-500/40 transition-all active:scale-[0.98] disabled:opacity-60 disabled:cursor-not-allowed"
          >
            {{ isSubmitting ? '送信中...' : '再設定リンクを送信' }}
          </button>
        </form>

        <div class="mt-8 pt-6 border-t border-gray-100 text-center">
          <p class="text-sm text-gray-500 font-medium">
            ログイン画面へ戻る場合は
            <RouterLink to="/login" class="text-bento-orange hover:text-orange-600 font-bold ml-1 transition-colors">こちら</RouterLink>
          </p>
        </div>

        <div v-if="message" class="mt-6 p-3.5 bg-green-50/80 border border-green-100 text-green-700 text-sm font-bold rounded-xl text-center flex items-center justify-center gap-2">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
          {{ message }}
        </div>
        <div v-if="errorMessage" class="mt-6 p-3.5 bg-red-50/80 border border-red-100 text-red-700 text-sm font-bold rounded-xl text-center flex items-center justify-center gap-2">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
          {{ errorMessage }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

import { requestPasswordReset } from '../api/user'

const email = ref('')
const isSubmitting = ref(false)
const message = ref('')
const errorMessage = ref('')

async function submitRequest() {
  if (!email.value.trim()) {
    errorMessage.value = 'メールアドレスを入力してください。'
    return
  }

  try {
    isSubmitting.value = true
    await requestPasswordReset({ email: email.value })
    message.value = '再設定申請を受け付けました。メールをご確認ください。'
    errorMessage.value = ''
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '再設定申請に失敗しました。'
  } finally {
    isSubmitting.value = false
  }
}
</script>
