<template>
  <div class="space-y-4">
    <div class="rounded-lg border border-gray-200 bg-white p-4 shadow-sm md:p-6">
      <div class="flex flex-col gap-4 border-b border-gray-100 pb-4 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <h3 class="text-lg font-bold text-gray-900">ユーザー管理</h3>
          <p class="mt-1 text-sm text-gray-500">ユーザー一覧と詳細情報の管理</p>
        </div>

        <div class="flex items-center gap-4">
          <div class="flex items-center gap-2 rounded-md border border-gray-200 bg-gray-50 px-3 py-2">
            <span class="text-xs font-semibold text-gray-500">登録人数</span>
            <span class="text-sm font-bold text-gray-900">{{ users.length }}</span>
          </div>
          <div class="flex items-center gap-2 rounded-md border border-orange-200 bg-orange-50 px-3 py-2">
            <span class="text-xs font-semibold text-orange-600">選択中</span>
            <span class="text-sm font-bold text-orange-700">{{ selectedUser ? 1 : 0 }}</span>
          </div>
        </div>
      </div>

      <div v-if="errorMessage" class="mt-4 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-600">{{ errorMessage }}</div>
      <div v-if="loadingMessage" class="mt-4 rounded-md border border-blue-200 bg-blue-50 px-3 py-2 text-sm text-blue-600">{{ loadingMessage }}</div>
      <div v-if="successMessage" class="mt-4 rounded-md border border-green-200 bg-green-50 px-3 py-2 text-sm text-green-700">{{ successMessage }}</div>

      <div class="mt-6 grid gap-6 md:grid-cols-[280px_1fr] lg:grid-cols-[320px_1fr]">
        <section class="flex flex-col rounded-lg border border-gray-200 bg-white overflow-hidden h-[calc(100vh-240px)] min-h-[400px]">
          <div class="border-b border-gray-200 bg-gray-50 px-4 py-3 flex items-center justify-between">
            <h4 class="text-sm font-bold text-gray-700">ユーザー一覧</h4>
            <span class="rounded-full bg-white px-2 py-0.5 text-xs font-medium text-gray-500 border border-gray-200">{{ users.length }}</span>
          </div>

          <div class="flex-1 overflow-y-auto p-2 space-y-1">
            <button
              v-for="user in users"
              :key="user.userId"
              type="button"
              @click="selectUser(user)"
              :class="[
                'w-full rounded-md px-3 py-2.5 text-left transition-colors flex items-start justify-between gap-2',
                selectedUser?.userId === user.userId
                  ? 'bg-orange-50 text-orange-900'
                  : 'hover:bg-gray-50 text-gray-700'
              ]"
            >
              <div class="truncate">
                <p class="text-sm font-semibold truncate text-gray-900" :class="selectedUser?.userId === user.userId ? 'text-orange-900' : ''">{{ user.name }}</p>
                <p class="mt-0.5 text-xs truncate" :class="selectedUser?.userId === user.userId ? 'text-orange-600/70' : 'text-gray-500'">{{ user.email }}</p>
              </div>
              <span class="shrink-0 rounded-full px-2 py-0.5 text-[10px] font-medium mt-0.5" :class="selectedUser?.userId === user.userId ? 'bg-orange-200 text-orange-800' : 'bg-gray-100 text-gray-600'">{{ user.reservationCount }}</span>
            </button>
          </div>
        </section>

        <section class="rounded-lg border border-gray-200 bg-white p-5 h-[calc(100vh-240px)] min-h-[400px] overflow-y-auto">
          <div v-if="selectedUser" class="space-y-6">
            <div class="flex flex-col gap-4 border-b border-gray-100 pb-4 md:flex-row md:items-center md:justify-between">
              <div>
                <h4 class="text-lg font-bold text-gray-900">{{ selectedUser.name }} さんの詳細</h4>
                <p class="mt-1 text-sm text-gray-500">{{ selectedUser.email }}</p>
              </div>

              <div class="flex gap-3">
                <div class="flex flex-col rounded-md border border-gray-200 bg-gray-50 px-3 py-1.5 text-center min-w-[70px]">
                  <span class="text-[10px] font-semibold text-gray-500">注文数</span>
                  <span class="text-lg font-bold text-gray-900">{{ reservations.length }}</span>
                </div>
                <div class="flex flex-col rounded-md border border-emerald-200 bg-emerald-50 px-3 py-1.5 text-center min-w-[70px]">
                  <span class="text-[10px] font-semibold text-emerald-600">支払済み</span>
                  <span class="text-lg font-bold text-emerald-700">{{ paidCount }}</span>
                </div>
                <div class="flex flex-col rounded-md border border-orange-200 bg-orange-50 px-3 py-1.5 text-center min-w-[70px]">
                  <span class="text-[10px] font-semibold text-orange-600">未払い</span>
                  <span class="text-lg font-bold text-orange-700">{{ unpaidCount }}</span>
                </div>
              </div>

              <button
                type="button"
                @click="openDeleteModal(selectedUser)"
                class="inline-flex items-center gap-1.5 rounded-lg border border-red-200 bg-red-50 px-3 py-1.5 text-xs font-bold text-red-700 transition-colors hover:bg-red-100 hover:border-red-300"
              >
                <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                ユーザー削除
              </button>
            </div>

            <div v-if="reservations.length" class="space-y-3">
              <article v-for="item in reservations" :key="item.reservationId" class="rounded-md border border-gray-200 bg-white p-4 shadow-sm flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
                <div class="flex-1">
                  <div class="flex items-center gap-2">
                    <p class="text-base font-bold text-gray-900">{{ item.menuName }}</p>
                    <span class="text-xs text-gray-500">#{{ item.reservationId }}</span>
                  </div>
                  <p class="mt-1 text-sm text-gray-500">予約日: {{ item.reservationDate }} / 数量: {{ item.quantity || 1 }}</p>
                </div>
                
                <div class="flex flex-col gap-2 shrink-0 md:items-end">
                  <div class="flex items-center gap-2">
                    <span class="rounded-full border px-2 py-0.5 text-[11px] font-medium" :class="paymentStatusTone(item.paymentStatus)">
                      {{ localizePaymentStatus(item.paymentStatus) }}
                    </span>
                    <span class="rounded-full border px-2 py-0.5 text-[11px] font-medium" :class="statusTone(item.orderStatus)">
                      {{ localizeOrderStatus(item.orderStatus) }}
                    </span>
                  </div>
                  <p v-if="item.refundReason" class="text-[10px] text-purple-600 bg-purple-50 rounded-md px-2 py-1 border border-purple-100 mt-1 text-right" :title="item.refundReason">理由: {{ item.refundReason }}</p>
                  <div class="flex items-center gap-1 mt-1">
                    <button
                      v-for="status in orderStatuses"
                      :key="status"
                      type="button"
                      @click="changeOrderStatus(item.reservationId, status)"
                      :class="[
                        'rounded px-2.5 py-1 text-xs font-medium transition-colors border',
                        item.orderStatus === status
                          ? 'border-orange-500 bg-orange-500 text-white'
                          : 'border-gray-200 bg-white text-gray-600 hover:border-orange-300 hover:bg-orange-50'
                      ]"
                    >
                      {{ localizeOrderStatus(status) }}
                    </button>
                    <button
                      v-if="canRefund(item)"
                      type="button"
                      @click="openRefundModal(item)"
                      class="rounded px-2.5 py-1 text-xs font-medium border border-red-200 bg-red-50 text-red-700 transition-colors hover:bg-red-100"
                    >
                      返金
                    </button>
                  </div>
                </div>
              </article>
            </div>

            <div v-else class="rounded-md border border-dashed border-gray-300 bg-gray-50 p-8 text-center text-sm text-gray-500">
              注文履歴がありません。
            </div>
          </div>

          <div v-else class="flex h-full items-center justify-center rounded-md border border-dashed border-gray-300 bg-gray-50 p-8 text-center text-sm text-gray-500">
            左側のリストからユーザーを選択してください。
          </div>
        </section>
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
          <label class="block text-sm font-bold text-gray-700 mb-1">返金理由 <span class="text-red-500">*</span></label>
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

    <!-- ユーザー削除モーダル -->
    <div v-if="deleteTarget" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40" @click.self="closeDeleteModal">
      <div class="w-full max-w-md rounded-2xl bg-white p-6 shadow-2xl">
        <div class="flex items-center gap-3 mb-4">
          <div class="flex-shrink-0 w-10 h-10 rounded-full bg-red-100 flex items-center justify-center">
            <svg class="w-5 h-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
          </div>
          <h3 class="text-lg font-bold text-gray-900">ユーザー削除確認</h3>
        </div>
        <p class="text-sm text-gray-600">
          <span class="font-bold text-red-700">{{ deleteTarget.name }}</span>（{{ deleteTarget.email }}）を削除しますか？<br>
          このユーザーの全ての予約データも削除されます。<span class="font-bold text-red-600">この操作は元に戻せません。</span>
        </p>
        <div class="mt-4">
          <label class="block text-sm font-bold text-gray-700 mb-1">管理者パスワード <span class="text-red-500">*</span></label>
          <input
            v-model="deletePassword"
            type="password"
            placeholder="パスワードを入力して削除を実行"
            class="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm outline-none transition focus:border-red-400 focus:ring-2 focus:ring-red-100"
          />
          <p class="mt-1.5 text-[11px] text-gray-400">誤削除を防ぐため、管理者パスワードの入力が必要です。</p>
        </div>
        <div class="mt-5 flex items-center justify-end gap-3">
          <button type="button" @click="closeDeleteModal" class="rounded-xl border border-gray-200 bg-white px-4 py-2.5 text-sm font-bold text-gray-600 transition hover:bg-gray-50">キャンセル</button>
          <button type="button" @click="submitDeleteUser" :disabled="!deletePassword.trim()" class="rounded-xl bg-red-600 px-4 py-2.5 text-sm font-bold text-white transition hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed">削除を実行</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'

import { fetchAdminUserReservations, fetchAdminUsers, refundReservation, updateAdminReservationStatus, deleteAdminUser } from '../../api/admin'

const users = ref([])
const selectedUser = ref(null)
const reservations = ref([])
const errorMessage = ref('')
const loadingMessage = ref('')
const successMessage = ref('')
const refundTarget = ref(null)
const refundReason = ref('')
const deleteTarget = ref(null)
const deletePassword = ref('')
const orderStatuses = ['RESERVED', 'CONFIRMED', 'CANCELLED']

const paidCount = computed(() => reservations.value.filter((item) => item.paymentStatus === 'PAID').length)
const unpaidCount = computed(() => reservations.value.filter((item) => item.paymentStatus !== 'PAID').length)

async function loadUsers() {
  try {
    loadingMessage.value = 'ユーザー情報を取得中...'
    const response = await fetchAdminUsers()
    users.value = response.data || []
    errorMessage.value = ''
    successMessage.value = ''
  } catch (error) {
    errorMessage.value = error.message || 'ユーザー一覧の取得に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

async function selectUser(user) {
  selectedUser.value = user
  await loadUserReservations(user.userId)
}

async function loadUserReservations(userId) {
  try {
    loadingMessage.value = '注文履歴を取得中...'
    const response = await fetchAdminUserReservations(userId)
    reservations.value = response.data || []
    errorMessage.value = ''
  } catch (error) {
    errorMessage.value = error.message || '注文履歴の取得に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

async function changeOrderStatus(reservationId, orderStatus) {
  const reservation = reservations.value.find((item) => item.reservationId === reservationId)
  if (!reservation || reservation.orderStatus === orderStatus) {
    return
  }

  const confirmed = window.confirm(
    `「${reservation.menuName}」(#${reservation.reservationId}) の状態を「${localizeOrderStatus(reservation.orderStatus)}」から「${localizeOrderStatus(orderStatus)}」へ変更します。\n\n誤操作でなければ OK を押してください。`
  )

  if (!confirmed) {
    return
  }

  try {
    loadingMessage.value = '注文状態を更新中...'
    await updateAdminReservationStatus(reservationId, orderStatus)
    if (selectedUser.value) {
      await loadUserReservations(selectedUser.value.userId)
      await loadUsers()
    }
    successMessage.value = `予約 #${reservationId} を「${localizeOrderStatus(orderStatus)}」に更新しました。`
    errorMessage.value = ''
  } catch (error) {
    errorMessage.value = error.message || '注文状態の更新に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

function statusTone(status) {
  if (status === 'CONFIRMED') return 'border-blue-200 bg-blue-50 text-blue-700'
  if (status === 'CANCELLED') return 'border-gray-200 bg-gray-100 text-gray-600'
  return 'border-yellow-200 bg-yellow-50 text-yellow-700'
}

function localizeOrderStatus(status) {
  if (status === 'CONFIRMED') return '確認済み'
  if (status === 'CANCELLED') return 'キャンセル'
  if (status === 'RESERVED') return '予約済み'
  return status || '未設定'
}

function localizePaymentStatus(status) {
  if (status === 'PAID') return '支払済み'
  if (status === 'REFUNDED') return '返金済み'
  return '未払い'
}

function paymentStatusTone(status) {
  if (status === 'PAID') return 'border-emerald-200 bg-emerald-50 text-emerald-700'
  if (status === 'REFUNDED') return 'border-purple-200 bg-purple-50 text-purple-700'
  return 'border-orange-200 bg-orange-50 text-orange-600'
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
    errorMessage.value = '返金理由を入力してください。'
    return
  }

  try {
    loadingMessage.value = '返金処理中...'
    await refundReservation(refundTarget.value.reservationId, reason)
    if (selectedUser.value) {
      await loadUserReservations(selectedUser.value.userId)
      await loadUsers()
    }
    successMessage.value = `予約 #${refundTarget.value.reservationId} の返金処理が完了しました。`
    errorMessage.value = ''
    closeRefundModal()
  } catch (error) {
    errorMessage.value = error.message || '返金処理に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

function openDeleteModal(user) {
  deleteTarget.value = user
  deletePassword.value = ''
}

function closeDeleteModal() {
  deleteTarget.value = null
  deletePassword.value = ''
}

async function submitDeleteUser() {
  if (!deleteTarget.value) return
  const password = deletePassword.value.trim()
  if (!password) {
    errorMessage.value = '管理者パスワードを入力してください。'
    return
  }

  try {
    loadingMessage.value = 'ユーザーを削除中...'
    await deleteAdminUser(deleteTarget.value.userId, password)
    selectedUser.value = null
    reservations.value = []
    await loadUsers()
    successMessage.value = `「${deleteTarget.value.name}」を削除しました。`
    errorMessage.value = ''
    closeDeleteModal()
  } catch (error) {
    errorMessage.value = error.message || 'ユーザーの削除に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

onMounted(loadUsers)
</script>
