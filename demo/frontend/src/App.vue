<template>
  <!-- Admin Dashboard Layout -->
  <div v-if="isAdminDashboard" class="flex h-screen bg-gray-50 font-sans overflow-hidden">
    <!-- Sidebar -->
    <aside class="w-64 bg-gray-900 text-white flex flex-col flex-shrink-0 transition-all shadow-xl z-20">
      <div class="h-20 flex items-center px-6 border-b border-gray-800 bg-gray-900 shrink-0">
        <div class="w-10 h-10 flex items-center justify-center mr-3">
          <img :src="brandIcon" alt="Bento icon" class="h-full w-full object-contain" />
        </div>
        <span class="text-base font-bold tracking-wide">管理パネル</span>
      </div>
      <div class="flex-1 overflow-y-auto py-6 flex flex-col gap-2 px-4">
        <div class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2 px-2">管理メニュー</div>
        <RouterLink to="/admin" class="flex items-center px-3 py-3 text-sm font-medium rounded-lg text-gray-300 hover:text-white hover:bg-gray-800 transition-colors" active-class="bg-bento-orange/10 text-bento-orange hover:bg-bento-orange/20 hover:text-bento-orange">
          <svg class="w-5 h-5 mr-3 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"></path></svg>
          ダッシュボード
        </RouterLink>
        <RouterLink to="/admin/menus" class="flex items-center px-3 py-3 text-sm font-medium rounded-lg text-gray-300 hover:text-white hover:bg-gray-800 transition-colors" active-class="bg-bento-orange/10 text-bento-orange hover:bg-bento-orange/20 hover:text-bento-orange">
          <svg class="w-5 h-5 mr-3 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 10h16M4 14h16M4 18h16"></path></svg>
          メニュー管理
        </RouterLink>
        <RouterLink to="/admin/aggregates/daily" class="flex items-center px-3 py-3 text-sm font-medium rounded-lg text-gray-300 hover:text-white hover:bg-gray-800 transition-colors" active-class="bg-bento-orange/10 text-bento-orange hover:bg-bento-orange/20 hover:text-bento-orange">
          <svg class="w-5 h-5 mr-3 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"></path></svg>
          日次集計・当日予約
        </RouterLink>
        <RouterLink to="/admin/aggregates/monthly" class="flex items-center px-3 py-3 text-sm font-medium rounded-lg text-gray-300 hover:text-white hover:bg-gray-800 transition-colors" active-class="bg-bento-orange/10 text-bento-orange hover:bg-bento-orange/20 hover:text-bento-orange">
          <svg class="w-5 h-5 mr-3 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>
          月次集計
        </RouterLink>
        <RouterLink to="/admin/settings" class="flex items-center px-3 py-3 text-sm font-medium rounded-lg text-gray-300 hover:text-white hover:bg-gray-800 transition-colors" active-class="bg-bento-orange/10 text-bento-orange hover:bg-bento-orange/20 hover:text-bento-orange">
          <svg class="w-5 h-5 mr-3 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path></svg>
          システム設定
        </RouterLink>
        <RouterLink to="/admin/users" class="flex items-center px-3 py-3 text-sm font-medium rounded-lg text-gray-300 hover:text-white hover:bg-gray-800 transition-colors" active-class="bg-bento-orange/10 text-bento-orange hover:bg-bento-orange/20 hover:text-bento-orange">
          <svg class="w-5 h-5 mr-3 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5V4H2v16h5m10 0v-2a4 4 0 00-4-4H9a4 4 0 00-4 4v2m12 0H7m6-12a4 4 0 11-8 0 4 4 0 018 0zm6 4v6"></path></svg>
          ユーザー管理
        </RouterLink>
      </div>
      <div class="p-4 border-t border-gray-800 shrink-0">
        <RouterLink to="/menus/today" class="flex items-center justify-center w-full px-4 py-2 text-sm font-medium text-gray-400 bg-gray-800 rounded-lg hover:text-white hover:bg-gray-700 transition-colors">
          <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"></path></svg>
          ユーザー画面へ戻る
        </RouterLink>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="flex-1 flex flex-col min-w-0 overflow-hidden relative">
      <!-- Top Header -->
      <header class="h-16 bg-white border-b border-gray-200 flex items-center justify-between px-8 shrink-0 shadow-sm z-10">
        <h2 class="text-xl font-bold text-gray-800 tracking-normal">{{ adminPageTitle }}</h2>
        
        <!-- Auth Profile -->
        <div class="flex items-center gap-3">
          <div class="flex items-center bg-gray-50 rounded-full border border-gray-200 p-1 shadow-sm transition-all hover:shadow-md">
            <div class="w-8 h-8 rounded-full flex items-center justify-center bg-orange-100 text-bento-orange border border-orange-200 shrink-0">
              <span class="text-xs font-bold">{{ authStore.userType.charAt(0) }}</span>
            </div>
            <div class="flex flex-col px-3 justify-center">
              <span class="text-xs text-gray-500 font-semibold leading-none mb-1 flex items-center gap-1">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 inline-block"></span>
                {{ authStore.userType }}
              </span>
              <span class="text-sm text-gray-800 font-bold leading-none truncate max-w-[120px]">{{ displayUserName }}</span>
            </div>
            <div class="w-px h-6 bg-gray-300 mx-1"></div>
            <button type="button" @click="logout" title="ログアウト" class="w-8 h-8 flex items-center justify-center rounded-full text-gray-500 hover:bg-red-100 hover:text-red-600 transition-colors mr-1">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" /></svg>
            </button>
          </div>
        </div>
      </header>
      
      <!-- Content Area -->
      <main class="flex-1 overflow-y-auto p-6 lg:p-8 bg-gray-50 z-0 flex flex-col">
        <div class="max-w-6xl mx-auto w-full flex-grow">
          <RouterView />
        </div>
        <!-- Footer -->
        <footer class="mt-auto pt-8 pb-2 flex items-center justify-center shrink-0 z-10 text-xs text-gray-400/80 font-medium">
          &copy; 2026 株式会社サンシーア All Rights Reserved. &middot; Kenlin
        </footer>
      </main>
    </div>
  </div>

  <!-- User Layout -->
  <div v-else class="min-h-screen bg-bento-bg flex flex-col font-sans relative overflow-hidden">
    <!-- Subtle background decorations -->
    <div class="fixed inset-0 pointer-events-none z-0 overflow-hidden">
      <!-- Top Left Blob -->
      <div class="absolute -top-[10%] -left-[5%] w-[60vw] h-[60vh] rounded-full bg-orange-200/30 blur-[120px] mix-blend-multiply"></div>
      <!-- Middle Right Blob -->
      <div class="absolute top-[20%] -right-[10%] w-[50vw] h-[70vh] rounded-full bg-yellow-100/40 blur-[130px] mix-blend-multiply"></div>
      <!-- Bottom Center Blob -->
      <div class="absolute -bottom-[20%] left-[20%] w-[50vw] h-[50vh] rounded-full bg-red-50/50 blur-[100px] mix-blend-multiply"></div>
      
      <!-- Dotted Pattern Overlay to reduce pure emptiness -->
      <div class="absolute inset-0 opacity-[0.03]" style="background-image: radial-gradient(#000 1.5px, transparent 1.5px); background-size: 32px 32px;"></div>
    </div>

    <header class="bg-white/85 backdrop-blur-xl shadow-sm border-b border-gray-100/80 sticky top-0 z-50 transition-all">
      <div class="max-w-[90rem] mx-auto w-full px-6 md:px-10 py-5 flex justify-between items-center">
        <!-- Logo -->
        <div class="flex items-center gap-2 sm:gap-3 group">
          <div class="w-12 h-12 sm:w-16 sm:h-16 flex items-center justify-center relative">
            <img :src="brandIcon" alt="Bento icon" class="h-full w-full object-contain relative z-10 group-hover:scale-110 group-hover:rotate-3 transition-transform duration-500 ease-out" style="filter: drop-shadow(0 2px 4px rgba(0,0,0,0.06));" />
          </div>
          <h1 class="text-2xl sm:text-3xl font-bold tracking-normal text-gray-800 drop-shadow-sm">弁当予約管理<span class="text-bento-orange">システム</span></h1>
        </div>
        
        <!-- Navigation -->
        <nav class="flex gap-1 items-center bg-gray-100/80 p-1 rounded-full border border-gray-200 shadow-inner">
          <RouterLink v-if="!authStore.isAuthenticated" to="/login" class="px-5 py-2 text-sm font-bold text-gray-500 hover:text-gray-900 rounded-full transition-all" active-class="bg-white text-bento-orange shadow-sm">ログイン</RouterLink>
          <RouterLink to="/menus/today" class="px-5 py-2 text-sm font-bold text-gray-500 hover:text-gray-900 rounded-full transition-all" active-class="bg-white text-bento-orange shadow-sm">本日メニュー</RouterLink>
          <RouterLink v-if="authStore.userType === 'USER'" to="/mypage" class="px-5 py-2 text-sm font-bold text-gray-500 hover:text-gray-900 rounded-full transition-all" active-class="bg-white text-bento-orange shadow-sm">マイページ</RouterLink>
          <RouterLink v-if="authStore.userType === 'ADMIN'" to="/admin" class="px-5 py-2 text-sm font-bold text-gray-500 hover:text-gray-900 rounded-full transition-all" active-class="bg-white text-bento-orange shadow-sm">管理者</RouterLink>
        </nav>

        <!-- User Profile & Actions -->
        <div class="flex items-center gap-3">
          <RouterLink v-if="authStore.userType === 'USER'" to="/cart" class="relative flex h-11 w-11 items-center justify-center rounded-full border border-gray-200 bg-white text-gray-500 shadow-sm transition hover:-translate-y-0.5 hover:border-orange-200 hover:text-bento-orange hover:shadow-md">
            <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2m0 0L7 13h10l2-8H5.4m0 0H19M7 13l-1.35 2.45A1 1 0 006.52 17H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 0a2 2 0 100 4 2 2 0 000-4z"/></svg>
            <span v-if="cartStore.totalQuantity" class="absolute -right-1 -top-1 min-w-[1.4rem] rounded-full bg-bento-orange px-1.5 py-0.5 text-center text-[11px] font-bold text-white shadow-sm">{{ cartStore.totalQuantity }}</span>
          </RouterLink>

          <div v-if="authStore.isAuthenticated" class="flex items-center bg-white rounded-full border border-gray-200 p-1 shadow-sm transition-all hover:shadow-md">
            <!-- Avatar Placeholder / Role Icon -->
            <div class="w-8 h-8 rounded-full flex items-center justify-center bg-orange-50 text-bento-orange border border-orange-100 shrink-0">
              <span class="text-xs font-bold">{{ authStore.userType.charAt(0) }}</span>
            </div>
            
            <div class="flex flex-col px-3 justify-center">
              <span class="text-xs text-gray-400 font-semibold leading-none mb-1 flex items-center gap-1">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 inline-block"></span>
                {{ authStore.userType }}
              </span>
              <span class="text-sm text-gray-700 font-bold leading-none truncate max-w-[120px]">{{ displayUserName }}</span>
            </div>

            <!-- Divider -->
            <div class="w-px h-6 bg-gray-200 mx-1"></div>

            <!-- Logout Button (Icon Style) -->
            <button 
              type="button" 
              @click="logout"
              title="ログアウト"
              class="w-8 h-8 flex items-center justify-center rounded-full text-gray-400 hover:bg-red-50 hover:text-red-500 transition-colors mr-1"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
              </svg>
            </button>
          </div>
          
          <div v-else class="text-sm text-gray-400 font-medium px-4">未ログイン</div>
        </div>
      </div>
    </header>

    <main class="flex-grow p-6 lg:p-12 max-w-[90rem] mx-auto w-full relative z-10">
      <RouterView />
    </main>

    <!-- Footer -->
    <footer class="mt-auto py-6 relative z-10 text-center text-xs text-gray-400/80 font-medium">
      <div class="max-w-[90rem] mx-auto w-full px-6 md:px-10">
        &copy; 2026 株式会社サンシーア All Rights Reserved. <span class="mx-1">&middot;</span> Kenlin
      </div>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import brandIcon from './assets/brand-icon.png'
import { useAuthStore } from './stores/auth'
import { useCartStore } from './stores/cart'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()

// Determine if we are on an admin dashboard page (excludes admin login)
// Check both route.path and window.location.pathname as a fallback to ensure it renders correctly on fresh loads
const isAdminDashboard = computed(() => {
  const currentPath = route.path || window.location.pathname
  return currentPath.includes('/admin') && currentPath !== '/admin/login' && authStore.userType === 'ADMIN'
})

// Dynamic admin page title based on route
const adminPageTitle = computed(() => {
  const currentPath = route.path || window.location.pathname
  if (currentPath === '/admin/menus') return 'メニュー管理'
  if (currentPath === '/admin/aggregates/daily') return '日次集計・当日予約'
  if (currentPath === '/admin/aggregates/monthly') return '月次集計'
  if (currentPath === '/admin/settings') return 'システム設定'
  if (currentPath === '/admin/users') return 'ユーザー管理'
  if (currentPath === '/admin') return 'ダッシュボード'
  return '管理ダッシュボード'
})

const displayUserName = computed(() => {
  const currentName = (authStore.userName || '').trim()
  if (!currentName) return authStore.userType === 'ADMIN' ? '管理者' : 'ユーザー'
  return currentName
})

function logout() {
  const nextRoute = authStore.userType === 'ADMIN' ? '/admin/login' : '/login'
  cartStore.clear()
  authStore.logout()
  router.push(nextRoute)
}
</script>
