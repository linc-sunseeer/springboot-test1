<template>
  <div class="mx-auto max-w-5xl space-y-8 pb-12">
    <div class="flex items-center gap-3">
      <RouterLink to="/menus/today" class="inline-flex items-center gap-2 rounded-full border border-gray-200 bg-white px-4 py-2 text-sm font-bold text-gray-600 shadow-sm transition hover:border-orange-200 hover:text-bento-orange">
        <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/></svg>
        一覧へ戻る
      </RouterLink>
      <span class="text-sm font-medium text-gray-400">メニュー詳細</span>
    </div>

    <div v-if="loadingMessage" class="flex flex-col justify-center items-center py-24">
      <div class="relative w-16 h-16">
        <div class="absolute inset-0 rounded-full border-4 border-gray-100"></div>
        <div class="absolute inset-0 rounded-full border-4 border-bento-orange border-t-transparent animate-spin"></div>
      </div>
      <span class="mt-6 text-sm font-medium tracking-wider text-gray-500 uppercase">{{ loadingMessage }}</span>
    </div>

    <div v-else-if="errorMessage" class="rounded-2xl border border-red-100 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">{{ errorMessage }}</div>

    <div v-else-if="menu" class="space-y-8">
      <section class="overflow-hidden rounded-[32px] border border-orange-100 bg-white shadow-[0_24px_60px_rgba(15,23,42,0.08)]">
        <div class="grid gap-0 lg:grid-cols-2 lg:items-start">
          <div class="relative w-full aspect-square sm:aspect-video lg:aspect-[4/3] overflow-hidden bg-slate-50">
            <img v-if="menu.imageUrl" :src="displayImageUrl(menu.imageUrl)" :alt="menu.name" class="absolute inset-0 h-full w-full object-cover object-center transition-transform duration-700 hover:scale-105" />
            <div v-else class="flex h-full w-full items-center justify-center text-slate-300">
              <svg class="w-24 h-24" fill="currentColor" viewBox="0 0 24 24"><path d="M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z"/></svg>
            </div>
            <div class="absolute inset-x-0 bottom-0 bg-gradient-to-t from-slate-950/70 via-slate-950/20 to-transparent p-6 text-white sm:p-8">
              <div class="flex flex-wrap items-center gap-2">
                <span class="rounded-full border border-white/20 bg-white/20 px-3 py-1 text-xs font-bold backdrop-blur-md">{{ menu.availableDate }}</span>
                <span v-if="isTodayMenu" class="rounded-full border border-white/20 bg-bento-orange/80 px-3 py-1 text-xs font-bold backdrop-blur-md text-white shadow-sm">当日予約 {{ menu.reservationCount ?? 0 }} 人</span>
                <span v-else class="rounded-full border border-white/20 bg-slate-800/80 px-3 py-1 text-xs font-bold backdrop-blur-md">予約不可</span>
              </div>
            </div>
          </div>

          <div class="p-6 sm:p-8 lg:p-10 flex flex-col justify-center">
            <div class="flex flex-wrap items-center gap-2 mb-4">
              <span v-if="isTodayMenu" class="rounded-full border border-orange-200 bg-orange-50 px-3 py-1 text-[11px] font-bold tracking-wider text-orange-700">本日提供メニュー</span>
              <span v-else class="rounded-full border border-slate-200 bg-slate-50 px-3 py-1 text-[11px] font-bold tracking-wider text-slate-500">プレビュー専用</span>
            </div>

            <h1 class="text-3xl font-black tracking-tight text-slate-900 sm:text-4xl lg:text-[2.5rem] leading-tight">{{ menu.name }}</h1>
            <p class="mt-4 text-base leading-relaxed text-slate-600">{{ detailedDescription }}</p>

            <div class="mt-8 grid gap-4 grid-cols-2 sm:grid-cols-3">
              <article class="flex flex-col justify-center min-w-0 rounded-2xl border border-slate-100 bg-slate-50/50 p-4">
                <p class="text-[10px] font-bold uppercase tracking-widest text-slate-400">価格</p>
                <p class="mt-1 text-2xl font-black text-slate-800">¥{{ Number(menu.price || 0).toLocaleString() }}</p>
              </article>
              <article class="flex flex-col justify-center min-w-0 rounded-2xl border border-slate-100 bg-slate-50/50 p-4">
                <p class="text-[10px] font-bold uppercase tracking-widest text-slate-400">予約状況</p>
                <p class="mt-1 text-2xl font-black text-slate-800">{{ menu.reservationCount ?? 0 }} <span class="text-sm font-medium text-slate-500">人</span></p>
              </article>
              <article class="col-span-2 sm:col-span-1 flex flex-col justify-center min-w-0 rounded-2xl border border-slate-100 bg-slate-50/50 p-4">
                <p class="text-[10px] font-bold uppercase tracking-widest text-slate-400">提供日</p>
                <p class="mt-1 break-keep text-lg font-bold text-slate-800">{{ menu.availableDate }}</p>
              </article>
            </div>

            <!-- 熱量 & アレルゲン -->
            <div v-if="menu.calorie != null || menu.allergens != null" class="mt-10 border-t border-slate-100 pt-8">
              <h3 class="text-sm font-bold text-slate-800 flex items-center gap-2 mb-4">
                <svg class="w-4 h-4 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                栄養・アレルギー情報
              </h3>
              <div class="grid gap-4 sm:grid-cols-2">
                <article v-if="menu.calorie != null" class="relative overflow-hidden rounded-[20px] border border-orange-100 bg-orange-50/40 p-4 sm:p-5 transition-colors">
                  <div class="relative z-10 flex items-start justify-between">
                    <div>
                      <p class="text-[11px] font-bold uppercase tracking-wider mb-1.5 text-orange-500">カロリー (熱量)</p>
                      <p class="flex items-baseline gap-1 text-[28px] leading-none font-black text-slate-800">
                        {{ menu.calorie }} <span class="text-sm font-bold text-slate-500">kcal</span>
                      </p>
                    </div>
                    <div class="rounded-full p-2 bg-orange-100 text-orange-500">
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 18.657A8 8 0 016.343 7.343S7 9 9 10c0-2 .5-5 2.986-7C14 5 16.09 5.777 17.656 7.343A7.975 7.975 0 0120 13a7.975 7.975 0 01-2.343 5.657z" /></svg>
                    </div>
                  </div>
                </article>
                <article class="relative overflow-hidden rounded-[20px] border p-4 sm:p-5 transition-colors"
                  :class="menuHasAllergens ? 'border-red-100 bg-red-50/40' : 'border-emerald-100 bg-emerald-50/40'">
                  <div class="relative z-10 flex flex-col h-full">
                    <p class="text-[11px] font-bold uppercase tracking-wider mb-1.5" :class="menuHasAllergens ? 'text-red-500' : 'text-emerald-500'">含有アレルゲン</p>
                    <div v-if="menuHasAllergens" class="flex flex-wrap gap-1.5 mt-auto pt-2">
                      <span v-for="a in parseAllergens(menu.allergens)" :key="a" class="inline-flex items-center rounded bg-white px-2 py-1 text-[11px] font-bold text-red-700 border border-red-100 shadow-sm">
                        {{ a }}
                      </span>
                    </div>
                    <div v-else class="mt-auto pt-2">
                      <span class="inline-flex items-center rounded bg-white px-2 py-1 text-[11px] font-bold text-emerald-700 border border-emerald-100 shadow-sm">
                        アレルゲンなし
                      </span>
                    </div>
                  </div>
                </article>
              </div>
            </div>

            <!-- 個人警告（アレルギー・カロリー超過） -->
            <div class="mt-6 space-y-3" v-if="allergenConflicts.length > 0 || isCalorieExceeded">
              <div v-if="allergenConflicts.length > 0" class="rounded-2xl border border-red-200 bg-red-50 p-4 shadow-sm">
                <div class="flex items-start gap-3">
                  <div class="flex-shrink-0 w-8 h-8 rounded-full bg-red-100 flex items-center justify-center mt-0.5">
                    <svg class="w-4 h-4 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
                  </div>
                  <div>
                    <h3 class="text-sm font-bold text-red-800">アレルギー警告</h3>
                    <p class="mt-1 text-xs text-red-700 leading-relaxed">
                      設定したアレルゲン（<span class="font-bold">{{ allergenConflicts.join('、') }}</span>）が含まれています。
                    </p>
                  </div>
                </div>
              </div>

              <div v-if="isCalorieExceeded" class="rounded-2xl border border-amber-200 bg-amber-50 p-4 shadow-sm">
                <div class="flex items-start gap-3">
                  <div class="flex-shrink-0 w-8 h-8 rounded-full bg-amber-100 flex items-center justify-center mt-0.5">
                    <svg class="w-4 h-4 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                  </div>
                  <div>
                    <h3 class="text-sm font-bold text-amber-800">目標カロリーオーバー</h3>
                    <p class="mt-1 text-xs text-amber-700 leading-relaxed">
                      このメニューは {{ menu.calorie }} kcal で、目標（{{ userProfile.targetCalorie }} kcal）を超過します。
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <div class="mt-6 grid gap-4 xl:grid-cols-2">
              <div class="rounded-2xl border border-slate-200 bg-slate-50/70 p-5">
                <h3 class="text-[11px] font-bold uppercase tracking-[0.18em] text-slate-500">詳細説明</h3>
                <p class="mt-3 text-sm leading-7 text-slate-700">{{ menu.description || '詳細説明は登録されていません。' }}</p>
              </div>
              <div v-if="isTodayMenu" class="rounded-2xl border border-orange-200 bg-orange-50/70 p-5">
                <h3 class="text-[11px] font-bold uppercase tracking-[0.18em] text-orange-700">注文ガイド</h3>
                <p class="mt-3 text-sm leading-7 text-slate-700">受付締切前ならこの画面からそのままカートへ追加できます。追加後はカートで支払方法を選んで注文内容を確定します。</p>
              </div>
              <div v-else class="rounded-2xl border border-slate-200 bg-slate-50/70 p-5">
                <h3 class="text-[11px] font-bold uppercase tracking-[0.18em] text-slate-500">プレビューについて</h3>
                <p class="mt-3 text-sm leading-7 text-slate-700">このメニューは今週の提供予定です。予約は提供日当日のみ可能です。</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section v-if="isTodayMenu" class="rounded-3xl border border-slate-200 bg-white p-5 sm:p-6 shadow-[0_10px_40px_rgba(15,23,42,0.04)]">
        <div class="flex flex-col sm:flex-row gap-6 items-center justify-between">
          <div>
            <h2 class="text-xl font-bold text-slate-900">カートへ追加</h2>
            <p class="mt-1 text-sm text-slate-500">注文内容を確認してカートに追加します。</p>
          </div>
          
          <div class="flex flex-col sm:flex-row gap-3 w-full sm:w-auto min-w-[280px]">
            <RouterLink 
              to="/cart" 
              class="flex-1 inline-flex items-center justify-center rounded-2xl border border-slate-200 bg-white px-6 py-4 text-sm font-bold text-slate-700 shadow-sm transition hover:bg-slate-50 hover:text-slate-900"
            >
              カートを見る
            </RouterLink>
            <button 
              type="button" 
              @click="addToCart(menu)" 
              class="flex-1 rounded-2xl bg-bento-orange px-6 py-4 text-sm font-bold text-white shadow-sm transition hover:bg-orange-600 hover:shadow-md active:scale-[0.98]"
            >
              カートに追加
            </button>
          </div>
        </div>

        <p class="mt-5 text-center sm:text-right text-xs text-slate-400">追加後はカートで支払方法を選んで注文を確定できます。</p>

        <div v-if="successMessage" class="mt-6 rounded-xl border border-green-200 bg-green-50 p-4 flex items-start gap-3">
          <svg class="w-5 h-5 text-green-600 shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
          <p class="text-sm font-medium text-green-800">{{ successMessage }}</p>
        </div>
      </section>

      <section v-else class="rounded-3xl border border-slate-200 bg-slate-50/50 p-5 sm:p-6">
        <div class="flex flex-col sm:flex-row gap-6 items-center justify-between">
          <div>
            <h2 class="text-xl font-bold text-slate-900">予約不可</h2>
            <p class="mt-1 text-sm text-slate-500">このメニューは本日の提供ではないため、予約できません。</p>
          </div>
          <div class="flex flex-col sm:flex-row gap-3 w-full sm:w-auto min-w-[280px]">
            <RouterLink 
              to="/menus/today" 
              class="flex-1 inline-flex items-center justify-center rounded-2xl border border-slate-200 bg-white px-6 py-4 text-sm font-bold text-slate-700 shadow-sm transition hover:bg-slate-50 hover:text-slate-900"
            >
              本日メニューへ
            </RouterLink>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

import { fetchMenuById, fetchUserProfile } from '../api/user'
import { useCartStore } from '../stores/cart'

const route = useRoute()
const cartStore = useCartStore()
const menu = ref(null)
const loadingMessage = ref('読込中...')
const errorMessage = ref('')
const successMessage = ref('')
const userProfile = ref(null)

function parseAllergens(allergensStr) {
  if (!allergensStr) return []
  return allergensStr.split(',').map(s => s.trim()).filter(Boolean)
}

const menuHasAllergens = computed(() => {
  return parseAllergens(menu.value?.allergens).length > 0
})

const hasTargetCalorie = computed(() => {
  return userProfile.value?.targetCalorie != null && userProfile.value.targetCalorie > 0
})

const allergenConflicts = computed(() => {
  const userAllergens = parseAllergens(userProfile.value?.allergenSettings)
  const menuAllergens = parseAllergens(menu.value?.allergens)
  if (userAllergens.length === 0 || menuAllergens.length === 0) return []
  return userAllergens.filter(a => menuAllergens.includes(a))
})

const isCalorieExceeded = computed(() => {
  if (!hasTargetCalorie.value || menu.value?.calorie == null) return false
  return menu.value.calorie > userProfile.value.targetCalorie
})

const isTodayMenu = computed(() => {
  if (!menu.value?.availableDate) return false
  const today = new Date().toISOString().split('T')[0]
  return menu.value.availableDate === today
})

const detailedDescription = computed(() => {
  if (!menu.value?.description) {
    return '詳細説明は登録されていません。'
  }
  return `${menu.value?.description}。内容を確認してからカートに追加してください。`
})

function backendOrigin() {
  return import.meta.env.VITE_PUBLIC_BACKEND_ORIGIN
    || import.meta.env.VITE_API_PROXY_TARGET
    || 'http://localhost:8080'
}

function displayImageUrl(imageUrl) {
  if (!imageUrl) return ''
  if (/^https?:\/\//.test(imageUrl)) return imageUrl
  if (imageUrl.startsWith('/')) return `${backendOrigin()}${imageUrl}`
  return `${backendOrigin()}/${imageUrl}`
}

function addToCart(menuItem) {
  const { replacing } = cartStore.addItem(menuItem)
  successMessage.value = replacing
    ? `1日1件ルールのため、注文内容を「${menuItem.name}」に更新しました。`
    : `「${menuItem.name}」をカートに追加しました。`
}

async function loadMenu() {
  try {
    loadingMessage.value = '読込中...'
    const [menuRes, profileRes] = await Promise.all([
      fetchMenuById(route.params.menuId),
      fetchUserProfile().catch(() => null)
    ])
    if (menuRes.success && menuRes.data) {
      menu.value = menuRes.data
      errorMessage.value = ''
    } else {
      menu.value = null
      errorMessage.value = menuRes.message || '指定したメニューが見つかりません。'
    }
    if (profileRes?.data) {
      userProfile.value = profileRes.data
    }
  } catch (error) {
    menu.value = null
    errorMessage.value = error.message || 'メニュー詳細の取得に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

onMounted(loadMenu)
</script>
