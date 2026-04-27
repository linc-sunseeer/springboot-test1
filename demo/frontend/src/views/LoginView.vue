<template>
  <div class="relative flex items-center justify-center min-h-[80vh] w-full">
    <!-- Background Decorators -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none z-0">
      <div class="absolute top-1/4 left-[10%] w-64 h-64 rounded-full bg-orange-100/50 blur-3xl"></div>
      <div class="absolute bottom-1/4 right-[10%] w-72 h-72 rounded-full bg-yellow-100/40 blur-3xl"></div>
    </div>

    <div class="relative w-full max-w-md bg-white rounded-[24px] shadow-[0_8px_30px_rgb(0,0,0,0.04)] border border-gray-100/60 overflow-hidden z-10">
      <div class="bg-gradient-to-br from-orange-500 to-orange-400 p-8 text-center relative overflow-hidden flex flex-col items-center">
        <!-- Inner card pattern -->
        <div class="absolute top-0 right-0 -mt-4 -mr-4 w-24 h-24 bg-white/10 rounded-full blur-xl"></div>
        <div class="absolute bottom-0 left-0 -mb-4 -ml-4 w-20 h-20 bg-black/5 rounded-full blur-lg"></div>
        
        <div class="w-16 h-16 bg-white rounded-full p-2 shadow-lg mb-3 relative z-10 flex items-center justify-center">
          <span class="text-4xl font-bold text-bento-orange">U</span>
        </div>
        <h2 class="text-2xl font-bold text-white tracking-wide relative z-10">ユーザーログイン</h2>
        <p class="text-orange-50 mt-2 text-sm font-medium relative z-10">Bento Boxへようこそ</p>
      </div>
      
      <div class="p-8">
        <form class="space-y-5" @submit.prevent="submitLogin">
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-1.5">メールアドレス</label>
            <input 
              v-model="email" 
              type="email" 
              autocomplete="username"
              placeholder="name@example.com" 
              required 
              class="w-full px-4 py-3.5 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:border-bento-orange focus:ring-4 focus:ring-bento-orange/10 transition-all outline-none font-medium text-gray-800 placeholder-gray-400"
            />
          </div>
          <div>
            <div class="flex justify-between items-center mb-1.5">
              <label class="block text-sm font-semibold text-gray-700">パスワード</label>
              <RouterLink to="/forgot-password" class="text-xs font-bold text-bento-orange hover:text-orange-600 transition-colors">パスワードを忘れた場合</RouterLink>
            </div>
            <div class="relative">
              <input 
                v-model="password" 
                :type="showPassword ? 'text' : 'password'" 
                autocomplete="current-password"
                placeholder="••••••••" 
                required 
                class="w-full px-4 py-3.5 pr-12 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:border-bento-orange focus:ring-4 focus:ring-bento-orange/10 transition-all outline-none font-medium text-gray-800 placeholder-gray-400"
              />
              <button 
                type="button" 
                @click="showPassword = !showPassword"
                tabindex="-1"
                class="absolute inset-y-0 right-0 px-4 flex items-center text-gray-400 hover:text-gray-600 focus:outline-none"
              >
                <svg v-if="!showPassword" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" /></svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.542-7a10.05 10.05 0 011.5-2.75M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3l18 18" /></svg>
              </button>
            </div>
          </div>
          
          <button 
            type="submit"
            :disabled="isSubmitting"
            class="w-full py-4 mt-4 bg-gradient-to-r from-orange-500 to-orange-400 hover:from-orange-600 hover:to-orange-500 text-white font-bold rounded-xl shadow-lg shadow-orange-500/30 hover:shadow-orange-500/40 transition-all active:scale-[0.98] disabled:opacity-70 disabled:cursor-not-allowed"
          >
            {{ isSubmitting ? 'ログイン中...' : 'ログイン' }}
          </button>
        </form>

        <div class="mt-8 pt-6 border-t border-gray-100 text-center">
          <p class="text-sm text-gray-500 font-medium">
            アカウントをお持ちでないですか？
            <RouterLink to="/register" class="text-bento-orange hover:text-orange-600 font-bold ml-1 transition-colors">新規登録はこちら</RouterLink>
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
import { useRouter } from 'vue-router'

import { loginUser } from '../api/user'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const showPassword = ref(false)
const message = ref('')
const errorMessage = ref('')
const isSubmitting = ref(false)

async function submitLogin() {
  if (!email.value.trim()) {
    errorMessage.value = 'メールアドレスを入力してください。'
    return
  }

  if (!password.value.trim()) {
    errorMessage.value = 'パスワードを入力してください。'
    return
  }

  isSubmitting.value = true
  try {
    const response = await loginUser({ email: email.value, password: password.value })
    authStore.loginAsUser(response.data)
    message.value = 'ログインしました。'
    errorMessage.value = ''
    router.push('/menus/today')
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'ログインに失敗しました。'
  } finally {
    isSubmitting.value = false
  }
}
</script>
