<template>
  <div class="mx-auto max-w-[1280px] space-y-6 px-1 pb-8 xl:max-w-[1360px]">
    <div class="overflow-hidden rounded-[28px] border border-slate-200/80 bg-white shadow-[0_20px_60px_rgba(15,23,42,0.08)]">
      <div class="border-b border-slate-100 bg-white px-5 py-6 md:px-8">
        <div class="flex flex-col gap-4 xl:flex-row xl:items-end xl:justify-between">
          <div class="space-y-2">
            <div class="inline-flex items-center gap-2 rounded-full border border-slate-200/60 bg-slate-50 px-3 py-1 text-[11px] font-bold tracking-[0.18em] text-slate-500">
              月次分析
            </div>
            <div>
              <h3 class="text-2xl font-black tracking-tight text-slate-900">月次集計</h3>
              <p class="mt-1 text-sm font-medium text-slate-500">月間の予約動向と主要メニューを 1 画面で確認できます。</p>
            </div>
          </div>

          <div class="flex flex-col gap-3 rounded-[1.25rem] border border-slate-100 bg-slate-50/50 p-4 sm:flex-row sm:items-end">
            <label class="text-[11px] font-bold uppercase tracking-[0.2em] text-slate-500">対象月</label>
            <div class="flex items-center gap-3">
              <input
                v-model="yearMonth"
                type="month"
                class="rounded-xl border border-slate-200 bg-white px-4 py-2 text-sm font-bold text-slate-900 shadow-sm outline-none transition focus:border-bento-orange focus:ring-4 focus:ring-orange-500/10"
              />
              <button
                type="button"
                @click="loadAggregate"
                class="rounded-xl bg-slate-900 px-5 py-2 text-sm font-bold text-white shadow-sm transition hover:bg-slate-800 hover:shadow-md"
              >
                集計取得
              </button>
              <button
                type="button"
                @click="exportCsv"
                :disabled="!aggregate"
                class="rounded-xl border border-slate-200 bg-white px-5 py-2 text-sm font-bold text-slate-700 shadow-sm transition hover:bg-slate-50 hover:shadow-md disabled:opacity-40 disabled:cursor-not-allowed"
              >
                CSV出力
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="px-5 py-5 md:px-7 md:py-6">
        <div v-if="loadingMessage" class="rounded-2xl border border-blue-200 bg-blue-50 px-4 py-3 text-sm font-medium text-blue-700">{{ loadingMessage }}</div>
        <div v-if="errorMessage" class="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">{{ errorMessage }}</div>

        <div v-if="aggregate" class="space-y-6">
          <!-- Summary Cards -->
          <div class="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
            <article class="rounded-[1.25rem] border border-blue-200 bg-blue-50/70 p-5 shadow-sm transition-shadow hover:shadow-md">
              <p class="text-[11px] font-bold uppercase tracking-[0.2em] text-blue-700 mb-4 flex items-center gap-1.5 break-words">
                <span class="w-1.5 h-1.5 rounded-full bg-blue-400"></span>営業日数
              </p>
              <div class="flex items-end justify-between gap-3">
                <p class="text-3xl font-black tracking-tight text-blue-900">{{ (aggregate.days || []).length }}</p>
                <span class="rounded-lg bg-white px-2.5 py-1 text-[11px] font-bold text-blue-600 border border-blue-100">日</span>
              </div>
            </article>

            <article class="rounded-[1.25rem] border border-purple-200 bg-purple-50/70 p-5 shadow-sm transition-shadow hover:shadow-md">
              <p class="text-[11px] font-bold uppercase tracking-[0.2em] text-purple-700 mb-4 flex items-center gap-1.5 break-words">
                <span class="w-1.5 h-1.5 rounded-full bg-purple-400"></span>月間総予約
              </p>
              <div class="flex items-end justify-between gap-3">
                <p class="text-3xl font-black tracking-tight text-purple-900">{{ totalReservations }}</p>
                <span class="rounded-lg bg-white px-2.5 py-1 text-[11px] font-bold text-purple-600 border border-purple-100">件</span>
              </div>
            </article>

            <article class="rounded-[1.25rem] border border-emerald-200 bg-emerald-50/70 p-5 shadow-sm transition-shadow hover:shadow-md">
              <p class="text-[11px] font-bold uppercase tracking-[0.2em] text-emerald-700 mb-4 flex items-center gap-1.5 break-words">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>支払済み
              </p>
              <div class="flex items-end justify-between gap-3">
                <p class="text-3xl font-black tracking-tight text-emerald-900">{{ totalPaid }}</p>
                <span class="rounded-lg bg-white px-2.5 py-1 text-[11px] font-bold text-emerald-600 border border-emerald-100">件</span>
              </div>
            </article>

            <article class="rounded-[1.25rem] border border-amber-200 bg-amber-50/80 p-5 shadow-sm transition-shadow hover:shadow-md">
              <p class="text-[11px] font-bold uppercase tracking-[0.2em] text-amber-700 mb-4 flex items-center gap-1.5 break-words">
                 <span class="w-1.5 h-1.5 rounded-full bg-amber-400"></span>未払い
              </p>
              <div class="flex items-end justify-between gap-3">
                <p class="text-3xl font-black tracking-tight text-amber-900">{{ totalUnpaid }}</p>
                <span class="rounded-lg bg-white px-2.5 py-1 text-[11px] font-bold text-amber-600 border border-amber-100">件</span>
              </div>
            </article>
          </div>

          <!-- Menu Summary -->
          <section class="rounded-[1.5rem] border border-gray-100 bg-white p-6 shadow-sm">
            <div class="flex items-center justify-between gap-3 border-b border-gray-50 pb-5">
              <div>
                <h4 class="text-base font-bold text-gray-900">月間メニューサマリー</h4>
                <p class="mt-1 text-sm font-medium text-gray-500">予約数の多いメニューを上位から表示します。</p>
              </div>
              <span class="rounded-xl border border-gray-100 bg-gray-50 px-4 py-1.5 text-[11px] font-bold text-gray-600">
                {{ monthlyMenuSummary.length }} 種類
              </span>
            </div>

            <div v-if="monthlyMenuSummary.length" class="mt-5 grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              <article
                v-for="(menu, index) in monthlyMenuSummary"
                :key="menu.menuName"
                class="rounded-[1.25rem] border border-slate-200 bg-slate-50/70 p-5 hover:border-slate-300 transition-colors shadow-sm"
              >
                <div class="flex items-start justify-between gap-3">
                  <div class="min-w-0 flex-1">
                    <p class="truncate text-sm font-bold text-slate-900">{{ menu.menuName }}</p>
                    <p class="mt-1 text-[11px] font-semibold text-slate-400">稼働日 {{ menu.activeDays }} 日</p>
                  </div>
                  <div class="text-right">
                    <p class="text-2xl font-black leading-none text-slate-900">{{ menu.reservationCount }}</p>
                    <p class="mt-1 text-[10px] font-bold tracking-[0.2em] text-slate-400">件</p>
                  </div>
                </div>
                <div class="mt-4 h-2 overflow-hidden rounded-full bg-slate-200">
                  <div
                    class="h-full rounded-full bg-gradient-to-r from-blue-600 via-blue-400 to-amber-400 transition-all duration-1000"
                    :style="{ width: `${menuShare(menu.reservationCount, topMonthlyMenuReservations)}%` }"
                  ></div>
                </div>
              </article>
            </div>
            <div v-else class="mt-5 rounded-2xl border border-dashed border-gray-200 px-4 py-12 text-center text-sm font-medium text-gray-400">
              該当月のメニュー集計はまだありません。
            </div>
          </section>

          <!-- Redesigned Calendar -->
          <section class="overflow-hidden rounded-[1.5rem] border border-gray-100 bg-white shadow-sm">
            <div class="border-b border-gray-50 px-6 py-5 bg-gradient-to-r from-gray-50/50 to-white">
              <div class="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
                <div>
                  <h4 class="text-base font-bold text-gray-900 tracking-wide">月間カレンダー</h4>
                  <p class="mt-1 text-[13px] text-gray-500 font-medium">予約のある日付を押すと、対象日の詳細へ移動します。</p>
                </div>
                <div class="flex items-center gap-3">
                  <span class="flex items-center gap-1.5 text-[11px] font-bold text-emerald-600 bg-emerald-50 px-3 py-1.5 rounded-xl border border-emerald-100/50"><span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>支払済</span>
                  <span class="flex items-center gap-1.5 text-[11px] font-bold text-yellow-600 bg-yellow-50 px-3 py-1.5 rounded-xl border border-yellow-100/50"><span class="w-1.5 h-1.5 rounded-full bg-yellow-400"></span>未払い</span>
                </div>
              </div>
            </div>

            <div class="p-4 md:p-6 bg-slate-50/50">
              <!-- Weekday Headers -->
              <div class="grid grid-cols-7 gap-px mb-2 text-center text-[11px] font-bold uppercase tracking-widest text-slate-400 bg-slate-200/50 border border-slate-200/50 rounded-xl overflow-hidden">
                <div class="bg-white/80 py-3 text-rose-500">日</div>
                <div class="bg-white/80 py-3 text-slate-500">月</div>
                <div class="bg-white/80 py-3 text-slate-500">火</div>
                <div class="bg-white/80 py-3 text-slate-500">水</div>
                <div class="bg-white/80 py-3 text-slate-500">木</div>
                <div class="bg-white/80 py-3 text-slate-500">金</div>
                <div class="bg-white/80 py-3 text-sky-500">土</div>
              </div>

              <!-- Calendar Grid -->
              <div class="grid grid-cols-7 gap-2">
                <div
                  v-for="(day, index) in calendarDays"
                  :key="index"
                  class="min-h-[110px] sm:min-h-[120px] rounded-2xl p-2.5 transition-all duration-300 relative flex flex-col border"
                  :class="[
                    day.empty ? 'border-transparent bg-transparent' : 
                    day.data?.reservationCount > 0 
                      ? 'bg-white border-slate-200 shadow-sm cursor-pointer hover:shadow-md hover:border-bento-orange hover:-translate-y-1 z-10' 
                      : 'border-slate-100 bg-white/40'
                  ]"
                  @click="(!day.empty && day.data?.reservationCount > 0) ? goToDailyView(day.date) : null"
                >
                  <template v-if="!day.empty">
                    <!-- Date Number -->
                    <div class="flex items-center justify-between mb-2">
                      <span 
                        class="inline-flex h-7 w-7 items-center justify-center rounded-full text-[13px] font-black font-sans transition-all"
                        :class="[
                          day.data?.reservationCount > 0 
                            ? 'bg-slate-900 text-white shadow-sm'
                            : (index % 7 === 0 ? 'text-rose-400 bg-rose-50' : (index % 7 === 6 ? 'text-sky-400 bg-sky-50' : 'text-slate-400 bg-slate-100'))
                        ]"
                      >
                        {{ day.dayNumber }}
                      </span>
                      <div v-if="day.data?.reservationCount > 0 && day.data?.unpaidCount > 0" class="flex items-center justify-center h-5 px-2 rounded-full bg-rose-100 border border-rose-200 text-[10px] font-bold text-rose-600 gap-1">
                        <span class="relative flex h-1.5 w-1.5">
                          <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-rose-400 opacity-75"></span>
                          <span class="relative inline-flex rounded-full h-1.5 w-1.5 bg-rose-500"></span>
                        </span>
                        未払
                      </div>
                    </div>

                    <!-- Data Row -->
                    <div v-if="day.data && day.data.reservationCount > 0" class="flex flex-col gap-1.5 flex-1 justify-end mt-1">
                      <!-- Total Count -->
                      <div class="flex items-center justify-between bg-slate-50 rounded-lg px-2.5 py-1.5 border border-slate-100">
                        <span class="text-[10px] font-bold tracking-widest text-slate-400">合計</span>
                        <span class="text-sm font-black text-slate-800 leading-none">{{ day.data.reservationCount }}</span>
                      </div>
                      
                      <!-- Paid/Unpaid Mini Bars -->
                      <div class="flex items-center gap-1 mt-0.5">
                        <div class="flex-1 bg-emerald-50 text-emerald-600 rounded-md px-1.5 py-1 text-[11px] font-bold text-center border border-emerald-100/60 transition-colors group-hover:bg-emerald-100" title="支払済み">
                          {{ day.data.paidCount || 0 }}
                        </div>
                        <div class="flex-1 bg-yellow-50 text-yellow-600 rounded-md px-1.5 py-1 text-[11px] font-bold text-center border border-yellow-100/60 transition-colors group-hover:bg-yellow-100" title="未払い">
                          {{ day.data.unpaidCount || 0 }}
                        </div>
                      </div>

                      <!-- Top Menu Name -->
                      <div v-if="topMenus(day.data.menus).length" class="mt-1">
                        <p class="text-[10px] font-semibold text-slate-500 truncate w-full px-1">
                          {{ topMenus(day.data.menus)[0].menuName }}
                        </p>
                      </div>
                    </div>
                  </template>
                </div>
              </div>
            </div>
          </section>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { fetchMonthlyAggregate, exportMonthlyAggregateCsv } from '../../api/admin'

const router = useRouter()
const yearMonth = ref(new Date().toISOString().slice(0, 7))
const aggregate = ref(null)
const errorMessage = ref('')
const loadingMessage = ref('読込中...')

const totalReservations = computed(() => (aggregate.value?.days || []).reduce((sum, day) => sum + Number(day.reservationCount || 0), 0))
const totalPaid = computed(() => (aggregate.value?.days || []).reduce((sum, day) => sum + Number(day.paidCount || 0), 0))
const totalUnpaid = computed(() => (aggregate.value?.days || []).reduce((sum, day) => sum + Number(day.unpaidCount || 0), 0))
const monthlyMenuSummary = computed(() => {
  const summary = new Map()

  for (const day of aggregate.value?.days || []) {
    for (const menu of day.menus || []) {
      const current = summary.get(menu.menuName) || {
        menuName: menu.menuName,
        reservationCount: 0,
        activeDays: 0,
      }
      current.reservationCount += Number(menu.reservationCount || 0)
      current.activeDays += 1
      summary.set(menu.menuName, current)
    }
  }

  return Array.from(summary.values())
    .sort((a, b) => b.reservationCount - a.reservationCount)
    .slice(0, 6)
})
const topMonthlyMenuReservations = computed(() => monthlyMenuSummary.value[0]?.reservationCount || 1)

const calendarDays = computed(() => {
  if (!yearMonth.value) return []
  const [year, month] = yearMonth.value.split('-').map(Number)
  const firstDay = new Date(year, month - 1, 1)
  const lastDay = new Date(year, month, 0)
  
  const days = []
  
  const firstDayOfWeek = firstDay.getDay()
  for (let i = 0; i < firstDayOfWeek; i++) {
    days.push({ empty: true })
  }
  
  const aggregateDays = aggregate.value?.days || []
  for (let i = 1; i <= lastDay.getDate(); i++) {
    const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(i).padStart(2, '0')}`
    const dayData = aggregateDays.find(d => d.date === dateStr) || null
    days.push({
      empty: false,
      date: dateStr,
      dayNumber: i,
      data: dayData
    })
  }
  
  const remainingCells = (7 - (days.length % 7)) % 7
  if (remainingCells > 0) {
    for (let i = 0; i < remainingCells; i++) {
      days.push({ empty: true })
    }
  }
  
  return days
})

async function loadAggregate() {
  try {
    loadingMessage.value = '読込中...'
    const response = await fetchMonthlyAggregate(yearMonth.value)
    aggregate.value = response.data
    errorMessage.value = ''
  } catch (error) {
    errorMessage.value = '月次集計の取得に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

async function exportCsv() {
  if (!yearMonth.value) return
  try {
    const blob = await exportMonthlyAggregateCsv(yearMonth.value)
    const url = URL.createObjectURL(new Blob([blob], { type: 'text/csv;charset=utf-8' }))
    const a = document.createElement('a')
    a.href = url
    a.download = `monthly-aggregate-${yearMonth.value}.csv`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
  } catch (error) {
    errorMessage.value = 'CSV出力に失敗しました。'
  }
}

function goToDailyView(date) {
  router.push({ path: '/admin/aggregates/daily', query: { date } })
}

function topMenus(menus = []) {
  return [...menus]
    .sort((a, b) => Number(b.reservationCount || 0) - Number(a.reservationCount || 0))
    .slice(0, 1)
}

function menuShare(count, maxCount) {
  if (!maxCount) return 0
  return Math.max(12, Math.round((Number(count || 0) / Number(maxCount)) * 100))
}

watch(yearMonth, () => {
  if (!yearMonth.value) return
  loadAggregate()
})

onMounted(loadAggregate)
</script>
