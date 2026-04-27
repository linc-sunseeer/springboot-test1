<template>
  <div class="space-y-6">
    <div class="overflow-hidden rounded-[28px] border border-slate-200/80 bg-white shadow-[0_20px_60px_rgba(15,23,42,0.08)]">
      <div class="border-b border-gray-100 bg-white px-5 py-6 md:px-8">
        <div class="space-y-2">
          <div class="inline-flex items-center gap-2 rounded-full border border-gray-200/60 bg-gray-50 px-3 py-1 text-[11px] font-bold uppercase tracking-[0.24em] text-gray-500">
            管理者ダッシュボード
          </div>
          <div>
            <h3 class="text-2xl font-black tracking-tight text-gray-900">今日の概要</h3>
            <p class="mt-1 text-sm font-medium text-gray-500">当日予約の全体像を一目で把握できます。</p>
          </div>
        </div>
      </div>

      <div class="px-5 py-5 md:px-7 md:py-6">
        <div v-if="errorMessage" class="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">{{ errorMessage }}</div>

        <div v-if="loading" class="flex items-center justify-center py-16">
          <div class="h-8 w-8 animate-spin rounded-full border-4 border-gray-200 border-t-gray-900"></div>
        </div>

        <div v-else-if="dashboard" class="space-y-6">
          <!-- 統計カード -->
          <div class="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
            <article class="rounded-[1.25rem] border border-gray-100 bg-white p-5 shadow-sm transition-shadow hover:shadow-md">
              <p class="text-[11px] font-bold uppercase tracking-[0.2em] text-gray-500">総予約数</p>
              <div class="mt-4 flex items-end justify-between gap-3">
                <p class="text-3xl font-black tracking-tight text-gray-900">{{ dashboard.totalReservations || 0 }}</p>
                <span class="rounded-lg bg-gray-50 px-2.5 py-1 text-[11px] font-bold text-gray-400 border border-gray-100">件</span>
              </div>
            </article>

            <article class="rounded-[1.25rem] border border-gray-100 bg-white p-5 shadow-sm transition-shadow hover:shadow-md">
              <p class="text-[11px] font-bold uppercase tracking-[0.2em] text-gray-500 mb-4 flex items-center gap-1.5">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>支払済み
              </p>
              <div class="flex items-end justify-between gap-3">
                <p class="text-3xl font-black tracking-tight text-gray-900">{{ dashboard.paidCount || 0 }}</p>
                <span class="rounded-lg bg-gray-50 px-2.5 py-1 text-[11px] font-bold text-gray-400 border border-gray-100">支払済</span>
              </div>
            </article>

            <article class="rounded-[1.25rem] border border-gray-100 bg-white p-5 shadow-sm transition-shadow hover:shadow-md">
              <p class="text-[11px] font-bold uppercase tracking-[0.2em] text-gray-500 mb-4 flex items-center gap-1.5">
                <span class="w-1.5 h-1.5 rounded-full bg-yellow-400"></span>未払い
              </p>
              <div class="flex items-end justify-between gap-3">
                <p class="text-3xl font-black tracking-tight text-gray-900">{{ dashboard.unpaidCount || 0 }}</p>
                <span class="rounded-lg bg-gray-50 px-2.5 py-1 text-[11px] font-bold text-gray-400 border border-gray-100">要確認</span>
              </div>
            </article>

            <article class="rounded-[1.25rem] border border-gray-100 bg-white p-5 shadow-sm transition-shadow hover:shadow-md">
              <p class="text-[11px] font-bold uppercase tracking-[0.2em] text-gray-500 mb-4 flex items-center gap-1.5">
                <span class="w-1.5 h-1.5 rounded-full bg-blue-400"></span>売上
              </p>
              <div class="flex items-end justify-between gap-3">
                <p class="text-3xl font-black tracking-tight text-gray-900">{{ dashboard.paidRevenue || 0 }}</p>
                <span class="rounded-lg bg-gray-50 px-2.5 py-1 text-[11px] font-bold text-gray-400 border border-gray-100">円</span>
              </div>
            </article>
          </div>

          <!-- 二次統計 -->
          <div class="grid gap-4 md:grid-cols-3">
            <article class="rounded-[1.25rem] border border-gray-100 bg-gradient-to-br from-orange-50 to-white p-5 shadow-sm">
              <p class="text-[11px] font-bold uppercase tracking-[0.2em] text-orange-600">メニュー数</p>
              <div class="mt-2 flex items-end justify-between gap-3">
                <p class="text-2xl font-black tracking-tight text-gray-900">{{ dashboard.menuCount || 0 }}</p>
                <span class="text-xs font-bold text-orange-500">品</span>
              </div>
            </article>
            <article class="rounded-[1.25rem] border border-gray-100 bg-gradient-to-br from-violet-50 to-white p-5 shadow-sm">
              <p class="text-[11px] font-bold uppercase tracking-[0.2em] text-violet-600">総売上</p>
              <div class="mt-2 flex items-end justify-between gap-3">
                <p class="text-2xl font-black tracking-tight text-gray-900">{{ dashboard.totalRevenue || 0 }}</p>
                <span class="text-xs font-bold text-violet-500">円</span>
              </div>
            </article>
            <article class="rounded-[1.25rem] border border-gray-100 bg-gradient-to-br from-teal-50 to-white p-5 shadow-sm">
              <p class="text-[11px] font-bold uppercase tracking-[0.2em] text-teal-600">登録ユーザー</p>
              <div class="mt-2 flex items-end justify-between gap-3">
                <p class="text-2xl font-black tracking-tight text-gray-900">{{ dashboard.totalUsers || 0 }}</p>
                <span class="text-xs font-bold text-teal-500">人</span>
              </div>
            </article>
          </div>

          <!-- メニュー別予約明細 -->
          <div v-if="(dashboard.menus || []).length > 0" class="space-y-4">
            <h4 class="text-lg font-black tracking-tight text-gray-900">メニュー別予約状況</h4>
            <div class="overflow-x-auto">
              <table class="w-full text-sm">
                <thead>
                  <tr class="border-b border-gray-100 text-left">
                    <th class="pb-3 pr-4 font-bold text-gray-500">メニュー名</th>
                    <th class="pb-3 pr-4 font-bold text-gray-500 text-right">予約数</th>
                    <th class="pb-3 pr-4 font-bold text-gray-500 text-right">支払済</th>
                    <th class="pb-3 font-bold text-gray-500 text-right">未払</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="menu in dashboard.menus" :key="menu.menuId" class="border-b border-gray-50">
                    <td class="py-3 pr-4 font-bold text-gray-900">{{ menu.menuName }}</td>
                    <td class="py-3 pr-4 text-right font-bold text-gray-700">{{ menu.reservationCount }}</td>
                    <td class="py-3 pr-4 text-right font-bold text-emerald-600">{{ menu.paidCount }}</td>
                    <td class="py-3 text-right font-bold text-yellow-600">{{ menu.unpaidCount }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div v-else class="rounded-2xl border border-gray-100 bg-gray-50 px-6 py-12 text-center">
            <p class="text-lg font-bold text-gray-400">今日の予約はまだありません</p>
            <p class="mt-1 text-sm text-gray-400">予約が入ると、ここに集計が表示されます。</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchAdminDashboard } from '../../api/admin'

const dashboard = ref(null)
const loading = ref(true)
const errorMessage = ref('')

async function loadDashboard() {
  loading.value = true
  errorMessage.value = ''
  try {
    const response = await fetchAdminDashboard()
    dashboard.value = response.data
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'ダッシュボード情報の取得に失敗しました。'
  } finally {
    loading.value = false
  }
}

onMounted(loadDashboard)
</script>