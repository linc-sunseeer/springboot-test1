<template>
  <div class="max-w-6xl mx-auto space-y-8 pb-12 pt-6">
    <div class="relative rounded-[2rem] overflow-hidden bg-gradient-to-r from-orange-400/90 to-amber-300/90 shadow-[0_8px_30px_rgb(251,146,60,0.15)] border border-orange-200/30">
      <div class="absolute inset-0 opacity-10" style="background-image: radial-gradient(#fff 2px, transparent 2px); background-size: 30px 30px;"></div>
      <div class="absolute top-0 right-0 -mt-10 -mr-10 w-40 h-40 bg-white/30 rounded-full blur-3xl"></div>

      <div class="relative px-8 py-10 flex flex-col md:flex-row items-center justify-between gap-6 z-10">
        <div class="text-left max-w-2xl text-white">
          <div class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-white/20 backdrop-blur-md text-[11px] font-bold mb-4 border border-white/20 text-white shadow-sm">
            <span class="relative flex h-1.5 w-1.5 mr-0.5">
              <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-red-300 opacity-75"></span>
              <span class="relative inline-flex rounded-full h-1.5 w-1.5 bg-red-400"></span>
            </span>
            今日のおすすめ
          </div>
          <h2 class="text-2xl md:text-3xl font-semibold mb-3 tracking-normal drop-shadow-sm">今日のランチをすぐ選んで、今週の予定もまとめて確認できます。</h2>
          <p class="mt-2.5 text-orange-50/90 font-medium tracking-wide mb-6">
            本日分はこの画面から予約へ進み、今週の弁当はプレビューとして先に確認できます。
          </p>
          <div class="flex flex-wrap items-center gap-3 text-orange-50/90 text-xs font-medium">
            <div class="inline-flex items-center bg-black/10 px-4 py-2 rounded-xl backdrop-blur-sm border border-white/10">
              <svg class="w-4 h-4 mr-1.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
              本日の予約締切: <span class="font-bold text-white ml-1">{{ cutoffTime }}</span>
            </div>
            <div class="inline-flex items-center px-4 py-2 rounded-xl backdrop-blur-sm border border-white/10" :class="thresholdMet ? 'bg-green-900/30' : 'bg-orange-900/30'">
              <svg class="w-4 h-4 mr-1.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
              <span class="font-bold text-white ml-1">現在の予約：{{ currentReservationCount }} 人</span>
            </div>
          </div>
        </div>

        <div class="flex flex-col items-center justify-center p-5 rounded-2xl bg-white/95 backdrop-blur-md border border-white/20 min-w-[130px] shrink-0 shadow-lg shadow-orange-900/10">
          <p class="text-orange-500 text-[10px] font-bold mb-1 tracking-wider">本日</p>
          <p class="text-4xl font-bold text-gray-800 mb-1 leading-none">{{ new Date().getDate() }}</p>
          <p class="text-gray-500 text-xs font-medium">{{ new Date().toLocaleDateString('ja-JP', { month: 'short', weekday: 'short' }) }}</p>
        </div>
      </div>
    </div>

    <div class="fixed top-4 right-4 sm:top-24 sm:right-6 z-[100] w-full max-w-[320px] pointer-events-none flex flex-col gap-3">
      <transition enter-active-class="transition duration-300 ease-out" enter-from-class="transform translate-x-8 opacity-0" enter-to-class="transform translate-x-0 opacity-100" leave-active-class="transition duration-200 ease-in" leave-from-class="transform translate-x-0 opacity-100" leave-to-class="transform translate-x-8 opacity-0">
        <div v-if="errorMessage" class="bg-white border-l-4 border-red-500 p-4 rounded-xl shadow-2xl pointer-events-auto flex items-start ring-1 ring-black/5">
          <div class="flex-shrink-0 w-8 h-8 rounded-full bg-red-50 flex items-center justify-center mr-3 mt-0.5">
            <svg class="h-5 w-5 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
          </div>
          <div class="flex-1 mr-2">
            <h3 class="text-sm font-bold text-gray-900 mb-0.5">エラー</h3>
            <p class="text-xs text-gray-600 leading-relaxed">{{ errorMessage }}</p>
          </div>
          <button @click="errorMessage = ''" class="text-gray-400 hover:text-gray-600 p-1 -mt-1 -mr-1 transition-colors"><svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg></button>
        </div>
      </transition>
    </div>

    <div v-if="loadingMessage" class="flex flex-col justify-center items-center py-32">
      <div class="relative w-16 h-16">
        <div class="absolute inset-0 rounded-full border-4 border-gray-100"></div>
        <div class="absolute inset-0 rounded-full border-4 border-bento-orange border-t-transparent animate-spin"></div>
      </div>
      <span class="mt-6 text-gray-600 font-medium tracking-normal text-sm uppercase">{{ loadingMessage }}</span>
    </div>

    <template v-else>
      <section class="space-y-4">
        <div class="flex items-center justify-between gap-3">
          <div>
            <p class="text-xs font-bold tracking-[0.24em] text-orange-500">今日のメニュー</p>
            <h3 class="mt-1 text-2xl font-bold text-gray-900">今日の弁当</h3>
            <p class="mt-1 text-sm text-gray-500">本日分はこのセクションから予約内容を確認できます。</p>
          </div>
        </div>

        <div v-if="todayMenu" class="overflow-hidden rounded-[2rem] border border-gray-100 bg-white shadow-[0_8px_30px_rgb(0,0,0,0.04)]">
          <div class="grid gap-0 lg:grid-cols-2 lg:items-start">
            <div class="relative w-full aspect-square lg:aspect-[4/3] overflow-hidden bg-slate-50 border-b border-gray-100 lg:border-b-0 lg:border-r">
              <img v-if="todayMenu.imageUrl" :src="displayImageUrl(todayMenu.imageUrl)" :alt="todayMenu.name" class="absolute inset-0 w-full h-full object-cover transition-transform duration-700 hover:scale-105" />
              <div v-else class="absolute inset-0 flex items-center justify-center text-gray-300">
                <svg class="w-14 h-14" fill="currentColor" viewBox="0 0 24 24"><path d="M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z"/></svg>
              </div>
              <div class="absolute top-6 left-6 flex flex-wrap gap-2 z-10">
                <span class="inline-flex items-center rounded-full bg-orange-500 px-3 py-1 text-[11px] font-bold text-white shadow-md">本日予約可</span>
                <span class="inline-flex items-center rounded-full bg-white/95 px-3 py-1 text-[11px] font-bold text-gray-800 shadow-md ring-1 ring-gray-900/5 backdrop-blur-sm">当日予約 {{ todayMenu.reservationCount ?? 0 }} 人</span>
              </div>
            </div>

            <div class="p-6 sm:p-8 flex flex-col gap-6">
              <div>
                <p class="text-[11px] font-bold tracking-[0.22em] text-orange-500">今日のピックアップ</p>
                <h4 class="mt-2 text-3xl font-bold tracking-tight text-gray-900">{{ todayMenu.name }}</h4>
                <p class="mt-4 text-sm leading-7 text-gray-600">{{ todayMenu.description || '詳細説明は準備中です。詳細ページで最新情報をご確認ください。' }}</p>

                <div v-if="todayMenu.calorie != null || todayMenu.allergens != null" class="mt-4 flex flex-wrap gap-2">
                  <span v-if="todayMenu.calorie != null" class="inline-flex items-center gap-1 rounded-lg bg-orange-50 px-2.5 py-1 text-xs font-bold text-orange-700 border border-orange-100">
                    <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 18.657A8 8 0 016.343 7.343S7 9 9 10c0-2 .5-5 2.986-7C14 5 16.09 5.777 17.656 7.343A7.975 7.975 0 0120 13a7.975 7.975 0 01-2.343 5.657z" /></svg>
                    {{ todayMenu.calorie }} kcal
                  </span>
                  <template v-if="parseAllergens(todayMenu.allergens).length > 0">
                    <span v-for="a in parseAllergens(todayMenu.allergens)" :key="a" class="inline-flex items-center rounded-md bg-red-50 px-2 py-0.5 text-[10px] font-bold text-red-600 ring-1 ring-inset ring-red-500/10">
                      {{ a }}
                    </span>
                  </template>
                  <span v-else class="inline-flex items-center rounded-md bg-emerald-50 px-2 py-0.5 text-[10px] font-bold text-emerald-600 ring-1 ring-inset ring-emerald-500/10">
                    アレルゲンなし
                  </span>
                </div>

                <!-- 過敏原衝突警告 -->
                <div v-if="getMenuAllergenConflicts(todayMenu).length > 0" class="mt-3 rounded-xl border border-red-200 bg-red-50 px-4 py-3">
                  <div class="flex items-start gap-2">
                    <svg class="w-5 h-5 text-red-500 shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
                    <div>
                      <p class="text-xs font-bold text-red-700">⚠️ アレルゲン含有の可能性があります</p>
                      <p class="text-[11px] text-red-600 mt-0.5">設定中の {{ getMenuAllergenConflicts(todayMenu).join('、') }} が含まれている可能性があります。</p>
                    </div>
                  </div>
                </div>

                <!-- カロリーオーバー提醒 -->
                <div v-else-if="isMenuCalorieExceeded(todayMenu)" class="mt-3 rounded-xl border border-amber-200 bg-amber-50 px-4 py-3">
                  <div class="flex items-start gap-2">
                    <svg class="w-5 h-5 text-amber-500 shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                    <div>
                      <p class="text-xs font-bold text-amber-700">🔥 目標熱量を超過しています</p>
                      <p class="text-[11px] text-amber-600 mt-0.5">{{ todayMenu.calorie }} kcal（目標: {{ userProfile.targetCalorie }} kcal）</p>
                    </div>
                  </div>
                </div>
              </div>

              <div class="grid gap-3 sm:grid-cols-2">
                <div class="rounded-2xl border border-gray-100 bg-gray-50 px-5 py-5 flex flex-col justify-center">
                  <p class="text-[11px] font-bold uppercase tracking-[0.18em] text-gray-400 mb-2">価格</p>
                  <p class="text-2xl font-black text-gray-900">¥{{ Number(todayMenu.price || 0).toLocaleString() }}</p>
                </div>
                
                <div class="rounded-2xl border border-gray-100 bg-gray-50 px-5 py-5 flex flex-col justify-center">
                  <p class="text-[11px] font-bold uppercase tracking-[0.18em] text-gray-400 mb-2">現在の予約</p>
                  <div class="flex items-center gap-3">
                    <p class="text-2xl font-black text-gray-900">
                      {{ currentReservationCount }} <span class="text-base font-bold text-gray-500">人</span>
                    </p>
                    <span v-if="thresholdMet" class="inline-flex items-center rounded-md bg-green-50 px-2 py-1 text-xs font-bold text-green-700 ring-1 ring-inset ring-green-600/20">注文成立</span>
                    <span v-else class="inline-flex items-center rounded-md bg-orange-50 px-2.5 py-1 text-[11px] font-bold text-orange-700 ring-1 ring-inset ring-orange-600/20">{{ minReservationThreshold }}人以上で注文成立</span>
                  </div>
                </div>
              </div>

              <div class="mt-auto flex flex-col sm:flex-row gap-3 sm:justify-end">
                <RouterLink :to="`/menus/${todayMenu.id}`" class="inline-flex items-center justify-center gap-2 rounded-xl border border-gray-200 bg-white px-5 py-3 text-sm font-bold text-gray-700 transition hover:bg-gray-50">
                  詳しく見る
                </RouterLink>
                <RouterLink to="/cart" class="inline-flex items-center justify-center gap-2 rounded-xl bg-gray-900 px-5 py-3 text-sm font-bold text-white transition hover:bg-gray-800">
                  カートへ進む
                </RouterLink>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="text-center py-20 bg-white rounded-[2rem] shadow-[0_8px_30px_rgb(0,0,0,0.04)] border border-gray-50/50">
          <div class="w-20 h-20 bg-gray-50/50 rounded-2xl flex items-center justify-center mx-auto mb-6 border border-gray-100/50">
            <svg class="h-8 w-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"></path></svg>
          </div>
          <h3 class="text-base font-semibold text-gray-800 mb-2">本日のメニューはまだありません</h3>
          <p class="text-gray-500 text-sm max-w-sm mx-auto leading-relaxed">現在は予約可能なメニューが登録されていません。<br>後ほど再度ご確認ください。</p>
        </div>
      </section>

      <section class="space-y-4">
        <div class="flex flex-col md:flex-row md:items-end md:justify-between gap-3">
          <div>
            <p class="text-xs font-bold tracking-[0.24em] text-gray-400">今週のプレビュー</p>
            <h3 class="mt-1 text-2xl font-bold text-gray-900">今週の弁当プレビュー</h3>
            <p class="mt-1 text-sm text-gray-500">このセクションはプレビュー専用です。内容確認のみ行えます。</p>
          </div>
          <div class="inline-flex items-center gap-2 rounded-xl bg-white px-3 py-1.5 shadow-sm border border-gray-100 shrink-0">
            <span class="text-[10px] font-bold text-gray-400 uppercase tracking-normal">件数</span>
            <span class="text-sm font-bold text-gray-800">{{ weeklyMenus.length }}</span>
          </div>
        </div>

        <div v-if="weeklyMenus.length" class="flex overflow-x-auto gap-6 pb-8 pt-4 px-2 scroll-smooth [&::-webkit-scrollbar]:h-2.5 [&::-webkit-scrollbar-track]:bg-gray-100/50 [&::-webkit-scrollbar-track]:rounded-full [&::-webkit-scrollbar-thumb]:bg-orange-300/80 [&::-webkit-scrollbar-thumb]:rounded-full hover:[&::-webkit-scrollbar-thumb]:bg-orange-400/90 transition-colors">
          <article v-for="menu in weeklyMenus" :key="menu.id" class="shrink-0 w-[calc(85vw)] sm:w-[calc(40%-14px)] lg:w-[calc(28.57%-20px)] rounded-[24px] bg-white shadow-[0_4px_24px_rgb(0,0,0,0.04)] hover:shadow-[0_12px_40px_rgb(251,146,60,0.12)] overflow-hidden transition-all duration-300 flex flex-col group border border-gray-100/60 cursor-pointer">
            <!-- 圖片區塊 -->
            <div class="relative w-full aspect-[4/3] sm:aspect-video bg-slate-50 overflow-hidden shrink-0">
              <div class="absolute top-4 left-4 z-10">
                <span class="inline-flex items-center rounded-xl bg-gray-900/80 backdrop-blur-md px-3 py-1.5 text-[11px] font-bold text-white shadow-sm ring-1 ring-white/10">
                  <svg class="w-3.5 h-3.5 mr-1.5 text-orange-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>
                  {{ formatMenuDate(menu.availableDate) }}
                </span>
              </div>
              <div class="absolute top-4 right-4 z-10">
                <span class="inline-flex items-center rounded-lg bg-orange-500/90 backdrop-blur-md px-2 py-1 text-[9px] font-black text-white tracking-widest">
                  プレビュー
                </span>
              </div>
              <img v-if="menu.imageUrl" :src="displayImageUrl(menu.imageUrl)" :alt="menu.name" class="absolute inset-0 w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" />
              <div v-else class="absolute inset-0 flex items-center justify-center text-gray-300">
                <svg class="w-10 h-10" fill="currentColor" viewBox="0 0 24 24"><path d="M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z"/></svg>
              </div>
            </div>

            <!-- 內容區塊 -->
            <div class="px-6 pb-6 pt-2 flex flex-col flex-1 bg-white relative z-20">
              <div class="mb-auto">
                <h4 class="text-[17px] font-bold text-gray-900 leading-snug group-hover:text-orange-500 transition-colors duration-300 line-clamp-1" :title="menu.name">{{ menu.name }}</h4>
                <p class="mt-2 text-[13px] leading-relaxed text-gray-500 line-clamp-2" :title="menu.description">{{ menu.description || '詳細説明は準備中です。' }}</p>

                <div v-if="menu.calorie != null || menu.allergens != null" class="mt-3 flex flex-wrap gap-1.5">
                  <span v-if="menu.calorie != null" class="inline-flex items-center gap-1 rounded-md bg-orange-50 px-2 py-0.5 text-[10px] font-bold text-orange-700 border border-orange-100">
                    {{ menu.calorie }} kcal
                  </span>
                  <template v-if="parseAllergens(menu.allergens).length > 0">
                    <span v-for="a in parseAllergens(menu.allergens).slice(0, 3)" :key="a" class="inline-flex items-center rounded-md bg-red-50 px-1.5 py-0.5 text-[9px] font-bold text-red-600 ring-1 ring-inset ring-red-500/10">
                      {{ a }}
                    </span>
                    <span v-if="parseAllergens(menu.allergens).length > 3" class="text-[9px] text-gray-400 font-bold">+{{ parseAllergens(menu.allergens).length - 3 }}</span>
                  </template>
                  <span v-else class="inline-flex items-center rounded-md bg-emerald-50 px-1.5 py-0.5 text-[9px] font-bold text-emerald-600 ring-1 ring-inset ring-emerald-500/10">
                    アレルゲンなし
                  </span>
                </div>

                <div v-if="getMenuAllergenConflicts(menu).length > 0" class="mt-2 flex items-center gap-1 text-[10px] font-bold text-red-600">
                  <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
                  アレルゲン注意
                </div>
                <div v-else-if="isMenuCalorieExceeded(menu)" class="mt-2 flex items-center gap-1 text-[10px] font-bold text-amber-600">
                  <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                  カロリーオーバー
                </div>
              </div>

              <div class="mt-5 pt-5 border-t border-gray-50 flex items-center justify-between">
                <div class="flex items-baseline gap-1">
                  <span class="text-lg font-black text-gray-900">¥{{ Number(menu.price || 0).toLocaleString() }}</span>
                  <span class="text-[10px] text-gray-400 font-bold ml-0.5">税込</span>
                </div>
              </div>
            </div>
          </article>
        </div>

        <div v-else class="rounded-[2rem] border border-dashed border-gray-200 bg-white px-6 py-14 text-center text-sm text-gray-500 shadow-[0_8px_30px_rgb(0,0,0,0.03)]">
          今週のプレビュー対象メニューはまだありません。
        </div>
      </section>
    </template>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'

import { fetchHomeMenus, fetchUserProfile } from '../api/user'

const todayMenu = ref(null)
const weeklyMenus = ref([])
const cutoffTime = ref('09:45')
const minReservationThreshold = ref(6)
const currentReservationCount = ref(0)
const thresholdMet = ref(false)
const errorMessage = ref('')
const loadingMessage = ref('読込中...')
const userProfile = ref(null)

const ALLERGEN_OPTIONS = [
  'えび', 'かに', '小麦', 'そば', '卵', '乳', '落花生', 'くるみ',
  'あわび', 'いか', 'いくら', 'オレンジ', 'カシューナッツ', 'キウイ',
  '牛肉', '鶏肉', '豚肉', 'まつたけ', 'もも', 'やまいも', 'りんご', 'ゼラチン'
]

function parseAllergens(allergensStr) {
  if (!allergensStr) return []
  return allergensStr.split(',').map(s => s.trim()).filter(Boolean)
}

function getMenuAllergenConflicts(menu) {
  const userAllergens = parseAllergens(userProfile.value?.allergenSettings)
  const menuAllergens = parseAllergens(menu?.allergens)
  if (userAllergens.length === 0 || menuAllergens.length === 0) return []
  return userAllergens.filter(a => menuAllergens.includes(a))
}

function isMenuCalorieExceeded(menu) {
  const targetCal = userProfile.value?.targetCalorie
  if (targetCal == null || targetCal <= 0 || menu?.calorie == null) return false
  return menu.calorie > targetCal
}

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

function formatMenuDate(value) {
  if (!value) return '日付未設定'
  const date = new Date(`${value}T00:00:00`)
  return date.toLocaleDateString('ja-JP', { month: 'short', day: 'numeric', weekday: 'short' })
}

async function loadMenus() {
  try {
    loadingMessage.value = '読込中...'
    const [homeRes, profileRes] = await Promise.all([
      fetchHomeMenus(),
      fetchUserProfile().catch(() => null)
    ])
    const homeData = homeRes.data || {}
    todayMenu.value = homeData.todayMenu || null
    weeklyMenus.value = homeData.weeklyMenus || []
    if (homeData.cutoffTime) {
      cutoffTime.value = homeData.cutoffTime.substring(0, 5)
    }
    if (homeData.minReservationThreshold) {
      minReservationThreshold.value = Number(homeData.minReservationThreshold)
    }
    if (homeData.currentReservationCount !== undefined) {
      currentReservationCount.value = Number(homeData.currentReservationCount)
    }
    if (homeData.thresholdMet !== undefined) {
      thresholdMet.value = Boolean(homeData.thresholdMet)
    }
    if (profileRes?.data) {
      userProfile.value = profileRes.data
    }
    errorMessage.value = ''
  } catch (error) {
    errorMessage.value = error.message || 'ホームメニューの取得に失敗しました。'
    setTimeout(() => { errorMessage.value = '' }, 5000)
  } finally {
    loadingMessage.value = ''
  }
}

onMounted(loadMenus)
</script>
