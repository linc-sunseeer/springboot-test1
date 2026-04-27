<template>
  <div class="flex items-center justify-center min-h-[70vh]">
    <div class="w-full max-w-md bg-white rounded-2xl shadow-xl border border-gray-100 overflow-hidden">
      <div class="bg-gray-900 p-6 text-center border-b-4 border-bento-orange flex flex-col items-center">
        <div class="w-16 h-16 bg-white rounded-full p-2 shadow-lg mb-3 relative z-10 flex items-center justify-center">
          <span class="text-4xl font-bold text-gray-900">A</span>
        </div>
        <h2 class="text-2xl font-bold text-white tracking-wide flex items-center justify-center gap-2">
          管理者ログイン
        </h2>
        <p class="text-gray-400 mt-2 text-sm font-medium">Bento Box 管理システム</p>
      </div>
      
      <div class="p-8">
        <form class="space-y-5" @submit.prevent="submitLogin">
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-1.5">管理者メールアドレス</label>
            <input 
              v-model="email" 
              type="email" 
              autocomplete="username"
              placeholder="admin@example.com" 
              required 
              class="w-full px-4 py-3 rounded-lg border border-gray-200 bg-gray-50 focus:bg-white focus:border-gray-900 focus:ring-2 focus:ring-gray-900/20 transition-all outline-none"
            />
          </div>
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-1.5">パスワード</label>
            <div class="relative">
              <input 
                v-model="password" 
                :type="showPassword ? 'text' : 'password'" 
                autocomplete="current-password"
                placeholder="••••••••" 
                required 
                class="w-full px-4 py-3 pr-12 rounded-lg border border-gray-200 bg-gray-50 focus:bg-white focus:border-gray-900 focus:ring-2 focus:ring-gray-900/20 transition-all outline-none"
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
            class="w-full py-3.5 mt-4 bg-gray-900 hover:bg-black text-white font-bold rounded-lg shadow-md hover:shadow-lg transition-all active:scale-[0.98]"
          >
            システムにログイン
          </button>
        </form>

        <div v-if="message" class="mt-6 p-3 bg-green-50 border border-green-100 text-green-600 text-sm font-medium rounded-lg text-center flex items-center justify-center gap-2">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
          {{ message }}
        </div>
        <div v-if="errorMessage" class="mt-6 p-3 bg-red-50 border border-red-100 text-red-600 text-sm font-medium rounded-lg text-center flex items-center justify-center gap-2">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
          {{ errorMessage }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import { loginAdmin } from '../../api/user'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const showPassword = ref(false)
const message = ref('')
const errorMessage = ref('')

async function submitLogin() {
  if (!email.value.trim()) {
    errorMessage.value = '管理者メールを入力してください。'
    return
  }

  if (!password.value.trim()) {
    errorMessage.value = 'パスワードを入力してください。'
    return
  }

  try {
    const response = await loginAdmin({ email: email.value, password: password.value })
    authStore.loginAsAdmin(response.data)
    message.value = '管理者ログインしました。'
    errorMessage.value = ''
    router.push('/admin')
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '管理者ログインに失敗しました。'
  }
}
</script>
