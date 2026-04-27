<template>
  <div class="space-y-6">
    <div class="overflow-hidden rounded-[28px] border border-slate-200/80 bg-white shadow-[0_20px_60px_rgba(15,23,42,0.08)]">
      <div class="border-b border-gray-100 bg-white px-5 py-6 md:px-8">
        <div class="flex flex-col gap-4 xl:flex-row xl:items-end xl:justify-between">
          <div class="space-y-2">
            <div class="inline-flex items-center gap-2 rounded-full border border-gray-200/60 bg-gray-50 px-3 py-1 text-[11px] font-bold uppercase tracking-[0.24em] text-gray-500">
              日次運用管理
            </div>
            <div>
              <h3 class="text-2xl font-black tracking-tight text-gray-900">日次集計・当日予約</h3>
              <p class="mt-1 text-sm font-medium text-gray-500">対象日の予約状況、支払状態、確認対象をまとめて管理できます。</p>
            </div>
          </div>

          <div class="flex flex-col gap-3 rounded-[1.25rem] border border-gray-100 bg-gray-50/50 p-4 lg:min-w-[360px]">
            <div class="flex flex-wrap items-center gap-2 text-[11px] font-bold uppercase tracking-[0.2em] text-gray-500">
              <span>対象日</span>
              <span class="rounded-lg bg-white border border-gray-200 px-2.5 py-1 normal-case tracking-normal text-gray-700 shadow-sm">{{ date }}</span>
            </div>
            <div class="flex flex-wrap items-center gap-2">
              <input
                v-model="date"
                type="date"
                class="rounded-xl border border-gray-200 bg-white px-3 py-2 text-sm font-bold text-gray-900 shadow-sm outline-none transition focus:border-bento-orange focus:ring-4 focus:ring-orange-500/10"
              />
              <button
                type="button"
                @click="loadAggregate"
                class="rounded-xl bg-gray-900 px-4 py-2 text-sm font-bold text-white shadow-sm transition hover:bg-gray-800 hover:shadow-md"
              >
                集計取得
              </button>
              <button
                type="button"
                @click="downloadCsv"
                class="rounded-xl border border-gray-200 bg-white px-4 py-2 text-sm font-bold text-gray-700 shadow-sm transition hover:bg-gray-50 hover:border-gray-300"
              >
                CSV 出力
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="px-5 py-5 md:px-7 md:py-6">
        <div v-if="loadingMessage" class="rounded-2xl border border-blue-200 bg-blue-50 px-4 py-3 text-sm font-medium text-blue-700">{{ loadingMessage }}</div>
        <div v-if="successMessage" class="rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm font-medium text-emerald-700">{{ successMessage }}</div>
        <div v-if="errorMessage" class="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">{{ errorMessage }}</div>

        <div v-if="aggregate && (aggregate.menus || []).length > 0" class="space-y-6">
          <div class="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
            <article class="rounded-[1.25rem] border border-gray-100 bg-white p-5 shadow-sm transition-shadow hover:shadow-md">
              <p class="text-[11px] font-bold uppercase tracking-[0.2em] text-gray-500">総予約数</p>
              <div class="mt-4 flex items-end justify-between gap-3">
                <p class="text-3xl font-black tracking-tight text-gray-900">{{ aggregate.totalReservations }}</p>
                <span class="rounded-lg bg-gray-50 px-2.5 py-1 text-[11px] font-bold text-gray-400 border border-gray-100">件</span>
              </div>
            </article>

            <article class="rounded-[1.25rem] border border-gray-100 bg-white p-5 shadow-sm transition-shadow hover:shadow-md">
              <p class="text-[11px] font-bold uppercase tracking-[0.2em] text-gray-500 mb-4 flex items-center gap-1.5 break-words">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>支払済み
              </p>
              <div class="flex items-end justify-between gap-3">
                <p class="text-3xl font-black tracking-tight text-gray-900">{{ aggregate.paidCount }}</p>
                <span class="rounded-lg bg-gray-50 px-2.5 py-1 text-[11px] font-bold text-gray-400 border border-gray-100">支払済</span>
              </div>
            </article>

            <article class="rounded-[1.25rem] border border-gray-100 bg-white p-5 shadow-sm transition-shadow hover:shadow-md">
              <p class="text-[11px] font-bold uppercase tracking-[0.2em] text-gray-500 mb-4 flex items-center gap-1.5 break-words">
                <span class="w-1.5 h-1.5 rounded-full bg-yellow-400"></span>未払い
              </p>
              <div class="flex items-end justify-between gap-3">
                <p class="text-3xl font-black tracking-tight text-gray-900">{{ aggregate.unpaidCount }}</p>
                <span class="rounded-lg bg-gray-50 px-2.5 py-1 text-[11px] font-bold text-gray-400 border border-gray-100">要確認</span>
              </div>
            </article>

            <article class="rounded-[1.25rem] border border-gray-100 bg-white p-5 shadow-sm transition-shadow hover:shadow-md">
              <p class="text-[11px] font-bold uppercase tracking-[0.2em] text-gray-500">メニュー種別</p>
              <div class="mt-4 flex items-end justify-between gap-3">
                <p class="text-3xl font-black tracking-tight text-gray-900">{{ aggregate.menus.length }}</p>
                <span class="rounded-lg bg-gray-50 px-2.5 py-1 text-[11px] font-bold text-gray-400 border border-gray-100">種類</span>
              </div>
            </article>
          </div>

          <!-- 注文確定管理セクション -->
          <section class="rounded-3xl border border-gray-100 bg-white p-6 shadow-sm">
            <div class="flex items-center justify-between gap-4 mb-6">
              <div>
                <h4 class="text-lg font-bold text-gray-900 flex items-center gap-2">
                  <svg class="w-5 h-5 text-bento-orange" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z"></path></svg>
                  注文確定管理
                </h4>
                <p class="mt-1 text-sm text-gray-500">注文確定人数に達したら注文を確定、未達の場合は全件キャンセルできます。</p>
              </div>
              <div class="flex items-center gap-3 rounded-xl px-4 py-2" :class="groupBuyStatus.met ? 'bg-green-50 border border-green-200' : 'bg-orange-50 border border-orange-200'">
                <span class="text-sm font-bold" :class="groupBuyStatus.met ? 'text-green-700' : 'text-orange-700'">
                  {{ groupBuyStatus.current }} / {{ groupBuyStatus.threshold }} 人
                </span>
                <span class="rounded-full px-2 py-0.5 text-xs font-bold" :class="groupBuyStatus.met ? 'bg-green-500 text-white' : 'bg-orange-500 text-white'">
                  {{ groupBuyStatus.met ? '注文確定' : `あと${groupBuyStatus.remaining}人` }}
                </span>
              </div>
            </div>

            <div class="flex flex-wrap gap-3">
              <button
                type="button"
                @click="confirmGroupBuy"
                :disabled="!groupBuyStatus.met"
                class="rounded-xl bg-green-600 px-5 py-2.5 text-sm font-bold text-white shadow-sm transition hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
                注文確定・全件確認
              </button>
              <button
                type="button"
                @click="cancelGroupBuy"
                class="rounded-xl border border-red-200 bg-white px-5 py-2.5 text-sm font-bold text-red-600 shadow-sm transition hover:bg-red-50 hover:border-red-300 flex items-center gap-2"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
                注文不成立・全件キャンセル
              </button>
            </div>
            <p class="mt-3 text-xs text-gray-400">
              ※ 確定時は全ユーザーに確認メールを送信、キャンセル時は全ユーザーにキャンセル・返金通知メールを送信します。
            </p>
          </section>

          <div class="grid gap-5 xl:grid-cols-[340px_minmax(0,1fr)]">
            <section class="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">
              <div class="border-b border-slate-200 bg-slate-50 px-5 py-4">
                <div class="flex items-center justify-between gap-3">
                  <div>
                    <h4 class="text-base font-bold text-slate-900">メニュー別構成</h4>
                    <p class="mt-1 text-sm text-slate-500">主要メニューごとの予約量と支払状況を確認します。</p>
                  </div>
                  <span class="rounded-full border border-slate-200 bg-white px-3 py-1 text-xs font-bold text-slate-500">{{ date }}</span>
                </div>
              </div>

              <div class="max-h-[720px] space-y-3 overflow-y-auto p-4">
                <article v-for="menu in aggregate.menus || []" :key="menu.menuId" class="rounded-2xl border border-slate-200 bg-slate-50/70 p-4">
                  <div class="flex items-start justify-between gap-3">
                    <div class="min-w-0 flex-1">
                      <p class="truncate text-sm font-bold text-slate-900">{{ menu.menuName }}</p>
                      <p class="mt-1 text-[11px] uppercase tracking-[0.18em] text-slate-400">#{{ menu.menuId }}</p>
                    </div>
                    <div class="text-right">
                      <p class="text-2xl font-black leading-none text-slate-900">{{ menu.reservationCount }}</p>
                      <p class="mt-1 text-[11px] font-semibold tracking-[0.18em] text-slate-400">件</p>
                    </div>
                  </div>

                  <div class="mt-3 h-2 overflow-hidden rounded-full bg-slate-200">
                    <div class="h-full rounded-full bg-gradient-to-r from-blue-700 via-blue-500 to-amber-400" :style="{ width: `${menuShare(menu.reservationCount)}%` }"></div>
                  </div>

                  <div class="mt-4 grid gap-2">
                    <div class="rounded-2xl border border-emerald-200 bg-emerald-50/70 px-3 py-3">
                      <div class="flex items-center justify-between gap-2">
                        <p class="text-[11px] font-bold uppercase tracking-[0.18em] text-emerald-700">支払済み</p>
                        <span class="rounded-full bg-white px-2 py-1 text-[10px] font-semibold text-emerald-600 shadow-sm">{{ (menu.paidUserNames || []).length }} 名</span>
                      </div>
                      <p class="mt-2 text-xs leading-5 text-slate-600">{{ joinUserNames(menu.paidUserNames, '該当者なし') }}</p>
                    </div>
                    <div class="rounded-2xl border border-amber-200 bg-amber-50/80 px-3 py-3">
                      <div class="flex items-center justify-between gap-2">
                        <p class="text-[11px] font-bold uppercase tracking-[0.18em] text-amber-700">未払い</p>
                        <span class="rounded-full bg-white px-2 py-1 text-[10px] font-semibold text-amber-600 shadow-sm">{{ (menu.unpaidUserNames || []).length }} 名</span>
                      </div>
                      <p class="mt-2 text-xs leading-5 text-slate-600">{{ joinUserNames(menu.unpaidUserNames, '該当者なし') }}</p>
                    </div>
                  </div>
                </article>
              </div>
            </section>

            <section class="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">
              <div class="border-b border-slate-200 bg-slate-50 px-5 py-4">
                <div class="flex flex-col gap-4 xl:flex-row xl:items-center xl:justify-between">
                  <div class="space-y-2">
                    <div class="flex flex-wrap items-center gap-2 text-sm font-bold text-slate-900">
                      <h4>予約ユーザー一覧</h4>
                      <span class="rounded-full border border-slate-200 bg-white px-2.5 py-1 text-[11px] font-semibold text-slate-500">{{ visibleReservationUsers.length }} 件表示</span>
                    </div>
                    <div class="flex flex-wrap items-center gap-2 text-xs font-medium text-slate-600">
                      <span class="inline-flex items-center gap-1.5 rounded-full bg-emerald-50 px-2.5 py-1 text-emerald-700"><span class="h-2 w-2 rounded-full bg-emerald-500"></span>支払済み {{ paidUsers.length }}</span>
                      <span class="inline-flex items-center gap-1.5 rounded-full bg-amber-50 px-2.5 py-1 text-amber-700"><span class="h-2 w-2 rounded-full bg-amber-500"></span>未払い {{ unpaidUsers.length }}</span>
                      <span class="inline-flex items-center gap-1.5 rounded-full bg-blue-50 px-2.5 py-1 text-blue-700"><span class="h-2 w-2 rounded-full bg-blue-500"></span>確認待ち {{ reservedUsers.length }}</span>
                      <span v-if="refundedUsers.length" class="inline-flex items-center gap-1.5 rounded-full bg-purple-50 px-2.5 py-1 text-purple-700"><span class="h-2 w-2 rounded-full bg-purple-500"></span>返金済み {{ refundedUsers.length }}</span>
                    </div>
                  </div>

                  <div class="flex flex-wrap gap-2">
                    <button type="button" @click="activeFilter = 'all'" :class="filterButtonClass('all')">すべて</button>
                    <button type="button" @click="activeFilter = 'paid'" :class="filterButtonClass('paid')">支払済み</button>
                    <button type="button" @click="activeFilter = 'unpaid'" :class="filterButtonClass('unpaid')">未払い</button>
                    <button type="button" @click="activeFilter = 'reserved'" :class="filterButtonClass('reserved')">確認待ち</button>
                    <button type="button" @click="activeFilter = 'refunded'" :class="filterButtonClass('refunded')">返金済み</button>
                  </div>
                </div>
              </div>

              <div class="grid grid-cols-[minmax(0,1fr)_90px_90px] gap-3 border-b border-slate-200 bg-white px-5 py-3 text-[11px] font-bold uppercase tracking-[0.18em] text-slate-500">
                <span>ユーザー / メニュー</span>
                <span class="text-center">状態</span>
                <span class="text-center">支払</span>
              </div>

              <div class="max-h-[440px] overflow-y-auto">
                <div v-for="item in visibleReservationUsers" :key="`${item.userId}-${item.menuId}`" class="grid grid-cols-[minmax(0,1fr)_90px_90px] gap-3 border-b border-slate-100 px-5 py-4 transition-colors hover:bg-slate-50/80">
                  <div class="min-w-0">
                    <div class="flex flex-wrap items-center gap-2">
                      <p class="truncate text-sm font-bold text-slate-900">{{ item.userName }}</p>
                      <span class="rounded-full bg-slate-100 px-2 py-0.5 text-[10px] font-semibold text-slate-500">#{{ item.reservationId }}</span>
                    </div>
                    <p class="mt-1 text-xs text-slate-500">{{ item.menuName }} <span class="mx-1 text-slate-300">|</span> 数量 {{ item.quantity || 1 }}</p>

                    <div v-if="canApprovePayment(item)" class="mt-3">
                      <button
                        type="button"
                        class="rounded-xl border border-orange-200 bg-orange-50 px-3 py-2 text-xs font-bold text-orange-700 transition hover:bg-orange-100"
                        @click="approvePayment(item)"
                      >
                        現金支払いを承認
                      </button>
                    </div>
                    <div v-if="canRefund(item)" class="mt-2">
                      <button
                        type="button"
                        class="rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-xs font-bold text-red-700 transition hover:bg-red-100"
                        @click="openRefundModal(item)"
                      >
                        返金・キャンセル
                      </button>
                    </div>
                  </div>

                  <div class="flex justify-center">
                    <span class="inline-flex h-fit rounded-full border px-2.5 py-1 text-[10px] font-bold" :class="item.orderStatus === 'CONFIRMED' ? 'border-slate-200 bg-slate-100 text-slate-700' : 'border-amber-200 bg-amber-50 text-amber-700'">
                      {{ localizeOrderStatus(item.orderStatus) }}
                    </span>
                  </div>

                  <div class="flex justify-center">
                    <span class="inline-flex h-fit rounded-full border px-2.5 py-1 text-[10px] font-bold" :class="paymentStatusTone(item.paymentStatus)">
                      {{ localizePaymentStatus(item.paymentStatus) }}
                    </span>
                  </div>
                  <div v-if="item.refundReason" class="col-span-3 mt-1">
                    <p class="text-[10px] text-purple-600 bg-purple-50 rounded-md px-2 py-1 border border-purple-100 truncate" :title="item.refundReason">理由: {{ item.refundReason }}</p>
                  </div>
                </div>

                <div v-if="!visibleReservationUsers.length" class="px-5 py-12 text-center text-sm text-slate-500">
                  条件に一致するユーザーはありません。
                </div>
              </div>

              <div class="grid gap-4 border-t border-slate-200 bg-slate-50 px-5 py-5 md:grid-cols-2">
                <section class="rounded-2xl border border-emerald-200 bg-white p-4 shadow-sm">
                  <div class="flex items-center justify-between gap-3">
                    <h5 class="text-sm font-bold text-emerald-700">支払済み一覧</h5>
                    <span class="rounded-full bg-emerald-50 px-2.5 py-1 text-xs font-bold text-emerald-700">{{ paidUsers.length }} 件</span>
                  </div>
                  <ul class="mt-3 space-y-2 text-sm text-slate-600">
                    <li v-for="item in paidUsers" :key="`paid-${item.userId}-${item.menuId}`" class="flex items-center justify-between rounded-xl border border-emerald-100 bg-emerald-50/60 px-3 py-2.5">
                      <div>
                        <p class="font-bold text-slate-900">{{ item.userName }}</p>
                        <p class="text-xs text-slate-500">{{ item.menuName }}</p>
                      </div>
                      <span class="rounded-full border border-emerald-100 bg-white px-2 py-1 text-[11px] font-semibold text-emerald-600">x{{ item.quantity || 1 }}</span>
                    </li>
                    <li v-if="!paidUsers.length" class="rounded-xl border border-dashed border-emerald-200 px-3 py-4 text-center text-xs text-slate-400">支払済みユーザーはありません。</li>
                  </ul>
                </section>

                <section class="rounded-2xl border border-amber-200 bg-white p-4 shadow-sm">
                  <div class="flex items-center justify-between gap-3">
                    <h5 class="text-sm font-bold text-amber-700">未払い一覧</h5>
                    <span class="rounded-full bg-amber-50 px-2.5 py-1 text-xs font-bold text-amber-700">{{ unpaidUsers.length }} 件</span>
                  </div>
                  <ul class="mt-3 space-y-2 text-sm text-slate-600">
                    <li v-for="item in unpaidUsers" :key="`unpaid-${item.userId}-${item.menuId}`" class="flex items-center justify-between rounded-xl border border-amber-100 bg-amber-50/60 px-3 py-2.5">
                      <div>
                        <p class="font-bold text-slate-900">{{ item.userName }}</p>
                        <p class="text-xs text-slate-500">{{ item.menuName }}</p>
                      </div>
                      <span class="rounded-full border border-amber-100 bg-white px-2 py-1 text-[11px] font-semibold text-amber-600">x{{ item.quantity || 1 }}</span>
                    </li>
                    <li v-if="!unpaidUsers.length" class="rounded-xl border border-dashed border-amber-200 px-3 py-4 text-center text-xs text-slate-400">未払いユーザーはありません。</li>
                  </ul>
                </section>
              </div>
            </section>
          </div>
        </div>

        <div v-else-if="!loadingMessage && aggregate" class="rounded-2xl border border-dashed border-slate-300 bg-slate-50 px-5 py-12 text-center text-sm text-slate-500">
          対象日のデータがありません
        </div>
      </div>
    </div>

    <!-- 返金モーダル -->
    <div v-if="refundTarget" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40" @click.self="closeRefundModal">
      <div class="w-full max-w-md rounded-2xl bg-white p-6 shadow-2xl">
        <h3 class="text-lg font-bold text-gray-900">返金・キャンセル確認</h3>
        <p class="mt-2 text-sm text-gray-600">
          <span class="font-bold">{{ refundTarget.userName }}</span> さんの予約
          <span class="font-mono text-xs bg-gray-100 px-1.5 py-0.5 rounded">#{{ refundTarget.reservationId }}</span>
          を返金・キャンセルしますか？
        </p>
        <div class="mt-4">
          <label class="block text-sm font-bold text-gray-700 mb-1">退款原因 <span class="text-red-500">*</span></label>
          <textarea
            v-model="refundReason"
            rows="3"
            placeholder="例：ユーザーからのキャンセル申請、メニュー提供中止など"
            class="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm outline-none transition focus:border-red-400 focus:ring-2 focus:ring-red-100 resize-none"
          ></textarea>
        </div>
        <div class="mt-5 flex items-center justify-end gap-3">
          <button type="button" @click="closeRefundModal" class="rounded-xl border border-gray-200 bg-white px-4 py-2.5 text-sm font-bold text-gray-600 transition hover:bg-gray-50">キャンセル</button>
          <button type="button" @click="submitRefund" class="rounded-xl bg-red-600 px-4 py-2.5 text-sm font-bold text-white transition hover:bg-red-700">返金実行</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

import { approveAdminReservationPayment, cancelAllReservations, confirmAllReservations, exportDailyAggregateCsv, fetchDailyAggregate, fetchDailyReservationUsers, fetchMinReservationThreshold, refundReservation } from '../../api/admin'

const route = useRoute()
const date = ref(route.query.date || formatLocalDate())
const aggregate = ref(null)
const reservationUsers = ref([])
const minThreshold = ref(6)
const errorMessage = ref('')
const successMessage = ref('')
const loadingMessage = ref('読込中...')
const activeFilter = ref('all')
const refundTarget = ref(null)
const refundReason = ref('')

const unpaidUsers = computed(() => reservationUsers.value.filter((item) => item.paymentStatus !== 'PAID' && item.paymentStatus !== 'REFUNDED'))
const paidUsers = computed(() => reservationUsers.value.filter((item) => item.paymentStatus === 'PAID'))
const refundedUsers = computed(() => reservationUsers.value.filter((item) => item.paymentStatus === 'REFUNDED'))
const reservedUsers = computed(() => reservationUsers.value.filter((item) => item.orderStatus !== 'CONFIRMED' && item.orderStatus !== 'CANCELLED'))
const visibleReservationUsers = computed(() => {
  if (activeFilter.value === 'paid') return paidUsers.value
  if (activeFilter.value === 'unpaid') return unpaidUsers.value
  if (activeFilter.value === 'reserved') return reservedUsers.value
  if (activeFilter.value === 'refunded') return refundedUsers.value
  return reservationUsers.value
})

const groupBuyStatus = computed(() => {
  if (!aggregate.value) return { threshold: minThreshold.value, current: 0, met: false, remaining: minThreshold.value }
  const threshold = minThreshold.value
  const current = aggregate.value.totalReservations || 0
  return { threshold, current, met: current >= threshold, remaining: Math.max(0, threshold - current) }
})

async function loadAggregate() {
  try {
    loadingMessage.value = '読込中...'
    const [dailyRes, usersRes, thresholdRes] = await Promise.all([
      fetchDailyAggregate(date.value),
      fetchDailyReservationUsers(date.value),
      fetchMinReservationThreshold(),
    ])
    aggregate.value = dailyRes.data
    reservationUsers.value = usersRes.data?.users || []
    minThreshold.value = Number(thresholdRes.data) || 6
    errorMessage.value = ''
    successMessage.value = ''
  } catch (error) {
    errorMessage.value = '日次集計の取得に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

async function downloadCsv() {
  if (!window.confirm('CSV を出力しますか？')) return

  try {
    loadingMessage.value = 'CSV 生成中...'
    if (!aggregate.value) {
      await loadAggregate()
    }

    const csvBuffer = await exportDailyAggregateCsv(date.value)
    const blob = new Blob([csvBuffer], { type: 'text/csv;charset=utf-8' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `daily-aggregate-${date.value}.csv`
    link.style.display = 'none'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    successMessage.value = 'CSV を出力しました。'
    errorMessage.value = ''
  } catch (error) {
    errorMessage.value = 'CSV出力に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

async function approvePayment(item) {
  if (!canApprovePayment(item)) return
  if (!window.confirm(`予約 #${item.reservationId} の現金支払いを承認しますか？`)) return

  try {
    loadingMessage.value = '支払い承認中...'
    await approveAdminReservationPayment(item.reservationId)
    await loadAggregate()
    successMessage.value = `予約 #${item.reservationId} の支払いを承認しました。`
    errorMessage.value = ''
  } catch (error) {
    errorMessage.value = error.message || '支払い承認に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

function menuShare(count) {
  if (!aggregate.value?.totalReservations) return 0
  return Math.max(12, Math.round((count / aggregate.value.totalReservations) * 100))
}

function joinUserNames(names, fallback) {
  if (!Array.isArray(names) || names.length === 0) return fallback
  return names.join(' / ')
}

function filterButtonClass(filter) {
  return activeFilter.value === filter
    ? 'rounded-full bg-bento-orange px-4 py-2 text-sm font-bold text-white shadow-sm'
    : 'rounded-full border border-gray-200 bg-white px-4 py-2 text-sm font-bold text-gray-600 hover:border-orange-200 hover:text-bento-orange transition-colors'
}

function formatLocalDate() {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

function localizeOrderStatus(status) {
  if (status === 'CANCELLED') return 'キャンセル'
  return status === 'RESERVED' ? '予約済み' : (status === 'CONFIRMED' ? '確認済み' : status)
}

function localizePaymentStatus(status) {
  if (status === 'PAID') return '支払済み'
  if (status === 'PAYMENT_REQUESTED') return '確認待ち'
  if (status === 'REFUNDED') return '返金済み'
  return '未払い'
}

function paymentStatusTone(status) {
  if (status === 'PAID') return 'border-emerald-200 bg-emerald-50 text-emerald-700'
  if (status === 'PAYMENT_REQUESTED') return 'border-blue-200 bg-blue-50 text-blue-700'
  if (status === 'REFUNDED') return 'border-purple-200 bg-purple-50 text-purple-700'
  return 'border-orange-200 bg-orange-50 text-orange-600'
}

function canApprovePayment(item) {
  return item?.paymentMethod === 'CASH' && item?.paymentStatus === 'PAYMENT_REQUESTED'
}

function canRefund(item) {
  return item?.paymentStatus !== 'REFUNDED' && item?.paymentStatus !== 'PAID' && item?.orderStatus !== 'CANCELLED'
}

function openRefundModal(item) {
  refundTarget.value = item
  refundReason.value = ''
}

function closeRefundModal() {
  refundTarget.value = null
  refundReason.value = ''
}

async function submitRefund() {
  if (!refundTarget.value) return
  const reason = refundReason.value.trim()
  if (!reason) {
    errorMessage.value = '退款原因を入力してください。'
    return
  }

  try {
    loadingMessage.value = '返金処理中...'
    await refundReservation(refundTarget.value.reservationId, reason)
    await loadAggregate()
    successMessage.value = `予約 #${refundTarget.value.reservationId} の返金処理が完了しました。`
    errorMessage.value = ''
    closeRefundModal()
  } catch (error) {
    errorMessage.value = error.message || '返金処理に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

async function confirmGroupBuy() {
  if (!window.confirm(`注文を確定しますか？\n${groupBuyStatus.value.current} 件の予約が確定状態になります。`)) return

  try {
    loadingMessage.value = '注文確定処理中...'
    const res = await confirmAllReservations(date.value)
    await loadAggregate()
    successMessage.value = `注文確定完了：${res.data?.confirmedCount || 0} 件を確認済みにしました。`
    errorMessage.value = ''
  } catch (error) {
    errorMessage.value = error.message || '注文確定に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

async function cancelGroupBuy() {
  if (!window.confirm('注文不成立として全件キャンセルしますか？\nすべての予約がキャンセルされ、返金処理されます。')) return

  try {
    loadingMessage.value = 'キャンセル処理中...'
    const res = await cancelAllReservations(date.value)
    await loadAggregate()
    successMessage.value = `全件キャンセル完了：${res.data?.cancelledCount || 0} 件をキャンセルし、返金通知を送信しました。`
    errorMessage.value = ''
  } catch (error) {
    errorMessage.value = error.message || 'キャンセル処理に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

onMounted(loadAggregate)
</script>
