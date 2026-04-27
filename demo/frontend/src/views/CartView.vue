<template>
  <div class="mx-auto max-w-4xl space-y-6 pb-12">
    <section class="rounded-3xl border border-gray-100 bg-white p-6 sm:p-8 shadow-sm">
      <div class="flex flex-col gap-4 border-b border-gray-100 pb-4 md:flex-row md:items-center md:justify-between">
        <div>
          <h2 class="text-2xl font-bold text-gray-900 tracking-tight">カート</h2>
          <p class="mt-2 text-sm text-gray-500">ご注文内容を確認してください（1日1件・成団人数 {{ minReservationThreshold }} 人）。</p>
        </div>
        <div class="flex items-center gap-3">
          <div class="flex items-center gap-2 rounded-xl border border-gray-100 bg-gray-50 px-4 py-2">
            <span class="text-xs font-bold text-gray-500">品目数</span>
            <span class="text-base font-black text-gray-900">{{ cartStore.itemCount }}</span>
          </div>
          <div class="flex items-center gap-2 rounded-xl border border-orange-100 bg-orange-50 px-4 py-2">
            <span class="text-xs font-bold text-orange-600">合計数量</span>
            <span class="text-base font-black text-orange-700">{{ cartStore.totalQuantity }}</span>
          </div>
        </div>
      </div>

      <div class="mt-4 rounded-xl border border-red-100 bg-red-50 px-4 py-3 text-sm font-bold text-red-600 flex items-center gap-2">
        <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
        本日提供中のメニューのみ注文できます。
      </div>
    </section>

    <div v-if="loadingMessage" class="rounded-2xl border border-blue-100 bg-blue-50 px-5 py-4 text-sm font-bold text-blue-700">{{ loadingMessage }}</div>
    <div v-if="successMessage" class="rounded-2xl border border-green-100 bg-green-50 px-5 py-4 text-sm font-bold text-green-700">{{ successMessage }}</div>
    <div v-if="errorMessage" class="rounded-2xl border border-red-100 bg-red-50 px-5 py-4 text-sm font-bold text-red-700">{{ errorMessage }}</div>

    <div class="grid gap-6 md:grid-cols-[minmax(0,1fr)_420px] items-start xl:grid-cols-[minmax(0,1fr)_460px]">
      <section class="rounded-3xl border border-gray-100 bg-white p-6 sm:p-8 shadow-sm">
        <div class="flex items-center justify-between border-b border-gray-100 pb-4 mb-6">
          <h3 class="text-lg font-bold text-gray-900">ご注文内容</h3>
          <button v-if="cartStore.items.length" type="button" @click="clearCart" class="text-sm font-bold text-gray-400 hover:text-red-500 transition-colors">すべて削除</button>
        </div>

        <div v-if="cartStore.items.length" class="space-y-4">
          <article v-for="item in cartStore.items" :key="item.menuId" class="flex flex-col gap-4 rounded-2xl border border-gray-100 bg-white p-5 shadow-sm sm:flex-row sm:items-center sm:justify-between transition hover:border-orange-100 hover:shadow-md">
            <div class="min-w-0 flex-1">
              <p class="text-base font-bold text-gray-900 truncate">{{ item.name }}</p>
              <p class="mt-1 text-sm font-medium text-gray-500">提供日: {{ item.availableDate }}</p>
            </div>
            
            <div class="flex items-center justify-between sm:justify-end gap-6 shrink-0">
              <p class="text-xl font-black text-gray-900">¥{{ Number(item.price || 0).toLocaleString() }}</p>
              
              <div class="flex items-center gap-3">
                <div class="flex items-center gap-2">
                  <button type="button" @click="cartStore.decrement(item.menuId)" class="rounded-lg border border-gray-200 bg-white px-2.5 py-1.5 text-sm font-black text-gray-600 transition hover:bg-gray-50">-</button>
                  <div class="rounded-lg bg-gray-50 border border-gray-200 px-3 py-1.5 text-xs font-bold text-gray-600 min-w-[64px] text-center">
                    数量: {{ item.quantity }}
                  </div>
                  <button type="button" @click="cartStore.increment(item.menuId)" class="rounded-lg border border-gray-200 bg-white px-2.5 py-1.5 text-sm font-black text-gray-600 transition hover:bg-gray-50">+</button>
                </div>
                <button type="button" @click="cartStore.removeItem(item.menuId)" class="rounded-lg p-2 text-gray-400 hover:bg-red-50 hover:text-red-500 transition-colors" title="削除">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                </button>
              </div>
            </div>
          </article>
        </div>

        <div v-else class="flex flex-col items-center justify-center rounded-2xl border-2 border-dashed border-gray-100 bg-gray-50 py-16 text-center">
          <svg class="w-12 h-12 text-gray-300 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"></path></svg>
          <p class="text-base font-bold text-gray-500">カートは空です</p>
          <RouterLink to="/menus/today" class="mt-4 rounded-xl bg-white px-5 py-2.5 text-sm font-bold text-bento-orange border border-orange-200 shadow-sm transition hover:bg-orange-50">本日メニューへ戻る</RouterLink>
        </div>
      </section>

      <aside class="rounded-3xl border border-gray-100 bg-white p-6 sm:p-8 shadow-sm md:sticky md:top-6">
        <h3 class="text-lg font-bold text-gray-900 border-b border-gray-100 pb-4 mb-6">注文確認</h3>
        
        <div class="space-y-4 text-sm">
          <div class="flex items-center justify-between text-gray-500 font-medium">
            <span>品目数</span>
            <span class="text-gray-900">{{ cartStore.itemCount }}</span>
          </div>
          <div class="flex items-center justify-between text-gray-500 font-medium">
            <span>合計数量</span>
            <span class="text-gray-900">{{ cartStore.totalQuantity }}</span>
          </div>
          
          <div class="border-t border-dashed border-gray-200 my-2"></div>
          
          <div class="flex items-center justify-between">
            <span class="font-bold text-gray-900">概算合計</span>
            <span class="text-3xl font-black text-orange-600">¥{{ totalPrice.toLocaleString() }}</span>
          </div>
        </div>

        <div class="mt-8 rounded-2xl border border-gray-100 bg-gray-50 p-5">
          <div class="mb-4 flex items-center justify-between gap-3">
            <div>
              <p class="text-xs font-bold text-gray-500 uppercase tracking-wider">支払方法</p>
              <p class="mt-1 text-xs text-gray-400">お支払い方法を選択してください。</p>
            </div>
            <span class="rounded-full border border-gray-200 bg-white px-2.5 py-1 text-[11px] font-bold text-gray-500 shadow-sm">選択</span>
          </div>

          <div class="grid grid-cols-3 gap-3">
            <label class="flex flex-col items-center justify-center gap-2 p-3 rounded-2xl border cursor-pointer transition-all" :class="paymentMethod === 'CARD' ? 'border-gray-900 bg-white shadow-sm ring-1 ring-gray-900' : 'border-gray-200 bg-white hover:border-gray-300 hover:bg-gray-50'">
              <input v-model="paymentMethod" type="radio" value="CARD" class="sr-only" />
              <svg class="w-6 h-6" :class="paymentMethod === 'CARD' ? 'text-gray-900' : 'text-gray-400'" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" /></svg>
              <span class="text-[11px] font-bold" :class="paymentMethod === 'CARD' ? 'text-gray-900' : 'text-gray-600'">クレジット</span>
            </label>

            <label class="flex flex-col items-center justify-center gap-2 p-3 rounded-2xl border cursor-pointer transition-all" :class="paymentMethod === 'PAYPAY' ? 'border-gray-900 bg-white shadow-sm ring-1 ring-gray-900' : 'border-gray-200 bg-white hover:border-gray-300 hover:bg-gray-50'">
              <input v-model="paymentMethod" type="radio" value="PAYPAY" class="sr-only" />
              <div class="w-6 h-6 rounded-full flex items-center justify-center" :class="paymentMethod === 'PAYPAY' ? 'bg-[#FF0033] text-white' : 'bg-gray-200 text-gray-400'"><span class="font-black text-[12px] italic pr-0.5">P</span></div>
              <span class="text-[11px] font-bold" :class="paymentMethod === 'PAYPAY' ? 'text-gray-900' : 'text-gray-600'">PayPay</span>
            </label>

            <label class="flex flex-col items-center justify-center gap-2 p-3 rounded-2xl border cursor-pointer transition-all" :class="paymentMethod === 'CASH' ? 'border-gray-900 bg-white shadow-sm ring-1 ring-gray-900' : 'border-gray-200 bg-white hover:border-gray-300 hover:bg-gray-50'">
              <input v-model="paymentMethod" type="radio" value="CASH" class="sr-only" />
              <svg class="w-6 h-6" :class="paymentMethod === 'CASH' ? 'text-gray-900' : 'text-gray-400'" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" /></svg>
              <span class="text-[11px] font-bold" :class="paymentMethod === 'CASH' ? 'text-gray-900' : 'text-gray-600'">現金</span>
            </label>
          </div>

          <div v-if="paymentMethod === 'CARD'" class="mt-4 rounded-2xl border border-gray-200 bg-white p-4">
            <div class="mb-4 flex items-center justify-between gap-3">
              <div>
                <p class="text-sm font-bold text-gray-900">クレジットカード情報</p>
                <p class="mt-1 text-xs text-gray-500">カード番号を入力してください。</p>
              </div>
            </div>

            <div class="space-y-3">
              <div>
                <label class="mb-1.5 block text-xs font-bold text-gray-700">カード番号</label>
                <input v-model="cardNumber" type="text" inputmode="numeric" placeholder="4111 1111 1111 1111" class="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm font-medium text-gray-800 outline-none transition focus:border-gray-300 focus:bg-white focus:ring-4 focus:ring-gray-100" />
              </div>
              <div>
                <label class="mb-1.5 block text-xs font-bold text-gray-700">カード名義</label>
                <input v-model="cardHolder" type="text" placeholder="TARO YAMADA" class="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm font-medium text-gray-800 outline-none transition focus:border-gray-300 focus:bg-white focus:ring-4 focus:ring-gray-100" />
              </div>
              <div class="grid gap-3 grid-cols-[minmax(0,1fr)_96px]">
                <div>
                  <label class="mb-1.5 block text-xs font-bold text-gray-700">有効期限</label>
                  <input v-model="cardExpiry" type="text" placeholder="12/30" class="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm font-medium text-gray-800 outline-none transition focus:border-gray-300 focus:bg-white focus:ring-4 focus:ring-gray-100" />
                </div>
                <div>
                  <label class="mb-1.5 block text-xs font-bold text-gray-700">CVV</label>
                  <input v-model="cardCvv" type="password" inputmode="numeric" placeholder="123" class="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm font-medium text-gray-800 outline-none transition focus:border-gray-300 focus:bg-white focus:ring-4 focus:ring-gray-100" />
                </div>
              </div>
            </div>
          </div>

          <div v-else-if="paymentMethod === 'PAYPAY'" class="mt-4 rounded-2xl border border-gray-200 bg-white p-5">
            <div class="flex flex-col sm:flex-row items-center gap-5">
              <div class="w-28 h-28 bg-white p-2 rounded-xl shadow-sm border border-gray-100 flex items-center justify-center shrink-0">
                <svg viewBox="0 0 96 96" class="h-full w-full text-gray-800" fill="currentColor" aria-hidden="true">
                  <rect x="4" y="4" width="24" height="24" rx="2"></rect><rect x="10" y="10" width="12" height="12" fill="white"></rect>
                  <rect x="68" y="4" width="24" height="24" rx="2"></rect><rect x="74" y="10" width="12" height="12" fill="white"></rect>
                  <rect x="4" y="68" width="24" height="24" rx="2"></rect><rect x="10" y="74" width="12" height="12" fill="white"></rect>
                  <rect x="38" y="8" width="8" height="8"></rect><rect x="50" y="8" width="8" height="8"></rect><rect x="38" y="20" width="8" height="8"></rect><rect x="50" y="20" width="8" height="8"></rect>
                  <rect x="34" y="34" width="8" height="8"></rect><rect x="46" y="34" width="8" height="8"></rect><rect x="58" y="34" width="8" height="8"></rect><rect x="70" y="34" width="8" height="8"></rect>
                  <rect x="34" y="46" width="8" height="8"></rect><rect x="58" y="46" width="8" height="8"></rect><rect x="70" y="46" width="8" height="8"></rect>
                  <rect x="34" y="58" width="8" height="8"></rect><rect x="46" y="58" width="8" height="8"></rect><rect x="58" y="58" width="8" height="8"></rect><rect x="70" y="58" width="8" height="8"></rect>
                  <rect x="46" y="70" width="8" height="8"></rect><rect x="58" y="70" width="8" height="8"></rect><rect x="70" y="70" width="8" height="8"></rect>
                </svg>
              </div>
              <div class="flex-1 w-full space-y-3 text-center sm:text-left">
                <div>
                  <p class="text-sm font-bold text-gray-900">PayPay スキャン支払い</p>
                  <p class="text-xs text-gray-500 mt-1">スマホアプリでQRコードを読み取ってください。</p>
                </div>
                <label class="flex items-center justify-center sm:justify-start gap-2.5 p-3 rounded-xl border border-gray-200 bg-gray-50 cursor-pointer hover:bg-gray-100 transition-colors">
                  <input v-model="paypayQrChecked" type="checkbox" class="h-4 w-4 rounded border-gray-300 text-gray-900 focus:ring-gray-900" />
                  <span class="text-xs font-bold text-gray-700">スキャン完了・支払いをシミュレート</span>
                </label>
              </div>
            </div>
          </div>

          <p class="mt-4 text-xs font-medium text-gray-400 text-center">※ 1日1件ルール: カートには常に1件のみ保持されます。成団人数に達すると予約が確定します。</p>
        </div>

        <button type="button" :disabled="!cartStore.items.length" @click="checkout" class="mt-8 w-full rounded-xl bg-gray-900 px-4 py-4 text-sm font-bold text-white shadow-sm transition hover:bg-gray-800 disabled:cursor-not-allowed disabled:opacity-50 active:scale-[0.98]">
          この内容で注文する
        </button>
        <p class="mt-4 text-center text-xs font-medium text-gray-400">注文後、マイページで確認・支払いができます</p>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { useCartStore } from '../stores/cart'
import { checkoutCart, fetchTodayMenus } from '../api/user'

const router = useRouter()
const cartStore = useCartStore()
const loadingMessage = ref('')
const successMessage = ref('')
const errorMessage = ref('')
const paymentMethod = ref('CASH')
const cardNumber = ref('')
const cardHolder = ref('')
const cardExpiry = ref('')
const cardCvv = ref('')
const paypayQrChecked = ref(false)
const minReservationThreshold = ref(6)
const currentReservationCount = ref(0)
const thresholdMet = ref(false)

const totalPrice = computed(() => cartStore.items.reduce((sum, item) => sum + (Number(item.price || 0) * item.quantity), 0))

async function loadReservationConfig() {
  try {
    const response = await fetchTodayMenus()
    const data = response.data || {}
    if (data.minReservationThreshold) {
      minReservationThreshold.value = Number(data.minReservationThreshold)
    }
    if (data.currentReservationCount !== undefined) {
      currentReservationCount.value = Number(data.currentReservationCount)
    }
    if (data.thresholdMet !== undefined) {
      thresholdMet.value = Boolean(data.thresholdMet)
    }
  } catch {
    // keep default fallback for cart usability
  }
}

async function checkout() {
  if (!cartStore.items.length) return
  if (!validateDemoPaymentInput()) return
  if (!window.confirm('カートの内容で注文を送信しますか？')) return

  try {
    loadingMessage.value = '注文を送信中...'
    await checkoutCart({
      items: cartStore.items.map((item) => ({
        menuId: item.menuId,
        reservationDate: item.availableDate,
        quantity: item.quantity,
        paymentMethod: paymentMethod.value,
      })),
    })
    cartStore.clear()
    successMessage.value = '注文を送信しました。マイページで確認できます。'
    errorMessage.value = ''
    setTimeout(() => {
      successMessage.value = ''
      router.push('/mypage')
    }, 1200)
  } catch (error) {
    errorMessage.value = error.message || '注文送信に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

function validateDemoPaymentInput() {
  if (paymentMethod.value === 'CARD') {
    if (!cardNumber.value.trim() || !cardHolder.value.trim() || !cardExpiry.value.trim() || !cardCvv.value.trim()) {
      errorMessage.value = 'クレジットカードのデモ入力欄をすべて入力してください。'
      return false
    }
  }

  if (paymentMethod.value === 'PAYPAY') {
    if (!paypayQrChecked.value) {
      errorMessage.value = 'PayPay のデモ QR コード確認にチェックを入れてください。'
      return false
    }
  }

  errorMessage.value = ''
  return true
}

onMounted(loadReservationConfig)

function clearCart() {
  if (!window.confirm('カート内の商品をすべて削除しますか？')) return
  cartStore.clear()
}
</script>
