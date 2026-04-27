<template>
  <div class="max-w-6xl mx-auto space-y-6 pb-12 pt-6">
    <!-- Hero Section -->
    <div class="relative rounded-[2rem] bg-white shadow-sm mb-8 border border-gray-100">
      <div class="relative px-8 py-10 flex flex-col md:flex-row items-center justify-between gap-6">
        <div class="text-left z-10">
          <h2 class="text-3xl font-black tracking-tight text-gray-900 flex items-center gap-3">
            <div class="w-12 h-12 rounded-2xl bg-orange-50 text-bento-orange flex items-center justify-center border border-orange-100/50">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" /></svg>
            </div>
            マイページ
          </h2>
          <p class="mt-3 text-sm font-medium text-gray-500">注文管理・個人情報・支払い履歴をここで一括管理。</p>
        </div>

        <div class="flex items-center gap-3 z-10 bg-gray-50/50 p-4 rounded-2xl border border-gray-100">
          <div class="flex flex-col items-center bg-white px-5 py-2.5 rounded-xl shadow-sm border border-gray-100">
            <span class="text-[11px] font-bold text-gray-400 uppercase tracking-widest mb-0.5">進行中</span>
            <span class="text-2xl font-black text-gray-900 leading-none">{{ pendingReservations.length }}</span>
          </div>
          <div class="flex flex-col items-center bg-white px-5 py-2.5 rounded-xl shadow-sm border border-gray-100">
            <span class="text-[11px] font-bold text-gray-400 uppercase tracking-widest mb-0.5 flex items-center gap-1"><span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>支払済み</span>
            <span class="text-2xl font-black text-gray-900 leading-none">{{ paidReservations.length }}</span>
          </div>
        </div>
      </div>
    </div>
    <p class="text-xs text-gray-500">※ 支払い方法は注文内容に応じて表示されます。</p>

    <!-- Navigation Tabs -->
    <nav class="flex gap-2 sm:gap-4 mb-6 overflow-x-auto pb-2 scrollbar-hide">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        type="button"
        @click="activeTab = tab.key"
        :class="[
          'px-5 py-2.5 rounded-2xl text-sm font-bold transition-all whitespace-nowrap border-2',
          activeTab === tab.key
            ? 'border-bento-orange bg-orange-50 text-bento-orange shadow-sm'
            : 'border-transparent text-gray-500 hover:bg-gray-100 hover:text-gray-900'
        ]"
      >
        <span class="flex items-center gap-2">
          <svg v-if="tab.key === 'orders'" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" /></svg>
          <svg v-if="tab.key === 'profile'" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" /></svg>
          <svg v-if="tab.key === 'diet'" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 18.657A8 8 0 016.343 7.343S7 9 9 10c0-2 .5-5 2.986-7C14 5 16.09 5.777 17.656 7.343A7.975 7.975 0 0120 13a7.975 7.975 0 01-2.343 5.657z" /></svg>
          <svg v-if="tab.key === 'payments'" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" /></svg>
          {{ tab.label }}
        </span>
      </button>
    </nav>

    <!-- Alerts -->
    <div v-if="loadingMessage" class="flex items-center rounded border border-blue-200 bg-blue-50 px-3 py-2 text-sm text-blue-700">
      <div class="mr-2 h-3 w-3 animate-spin rounded-full border-2 border-blue-600 border-t-transparent"></div>
      {{ loadingMessage }}
    </div>
    <div v-if="successMessage" class="rounded border border-green-200 bg-green-50 px-3 py-2 text-sm text-green-700">{{ successMessage }}</div>
    <div v-if="errorMessage" class="rounded border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">{{ errorMessage }}</div>

    <!-- Orders Tab -->
    <section v-if="activeTab === 'orders'" class="grid gap-6 md:grid-cols-[1fr_250px]">
      <div class="space-y-4">
        <article class="rounded-[2rem] border border-gray-50/50 bg-white shadow-[0_8px_30px_rgb(0,0,0,0.04)] overflow-hidden transition-shadow duration-300 hover:shadow-[0_8px_30px_rgb(0,0,0,0.08)]">
          <div class="flex items-center justify-between border-b border-gray-50/80 px-8 py-5 bg-gradient-to-r from-gray-50/50 to-white">
            <h3 class="text-sm font-semibold text-gray-800 tracking-wide">進行中の注文</h3>
            <span v-if="currentReservation" :class="statusPill(currentReservation.orderStatus, 'order')">
              {{ orderStatusLabel(currentReservation.orderStatus) }}
            </span>
          </div>

          <div v-if="currentReservation" class="p-8 space-y-6">
            <div class="grid grid-cols-2 gap-5 rounded-2xl bg-gray-50/50 p-5 border border-gray-100/30">
              <div>
                <p class="text-[10px] font-bold text-gray-400 uppercase tracking-normal mb-1.5">注文番号</p>
                <p class="text-xl font-bold text-gray-800 tracking-normal">#{{ currentReservation.id }}</p>
              </div>
              <div>
                <p class="text-[10px] font-bold text-gray-400 uppercase tracking-normal mb-1.5">予約日</p>
                <p class="text-xl font-bold text-gray-800 tracking-normal">{{ currentReservation.reservationDate }}</p>
              </div>
            </div>

            <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-5 rounded-2xl bg-orange-50/30 p-5 border border-orange-100/30">
              <div>
                <p class="text-[10px] font-bold text-gray-400 uppercase tracking-normal mb-2">内容</p>
                <p class="text-base font-semibold text-gray-800">メニュー ID {{ currentReservation.menuId }} <span class="text-sm font-bold text-gray-500 bg-white px-2.5 py-1 rounded-lg ml-1.5 border border-gray-100 shadow-sm">× {{ currentReservation.quantity || 1 }}</span></p>
                <p class="text-xs font-semibold text-gray-500 mt-2.5 flex items-center gap-1.5">
                  <svg v-if="currentReservation.paymentMethod === 'CARD'" class="w-4 h-4 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" /></svg>
                  <div v-else-if="currentReservation.paymentMethod === 'PAYPAY'" class="w-4 h-4 rounded-full bg-emerald-500 flex items-center justify-center text-[10px] font-black italic text-white leading-none">P</div>
                  <svg v-else class="w-4 h-4 text-emerald-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" /></svg>
                  支払方法: {{ paymentMethodLabel(currentReservation.paymentMethod) }}
                </p>
              </div>
              <span :class="statusPill(currentReservation.paymentStatus, 'payment')">{{ paymentStatusLabel(currentReservation.paymentStatus) }}</span>
            </div>

            <div class="flex flex-col sm:flex-row gap-3 pt-3">
              <button
                type="button"
                @click="confirmReservation()"
                :disabled="currentReservation.orderStatus !== 'RESERVED'"
                class="flex-1 rounded-xl px-5 py-3.5 text-sm font-semibold transition-all duration-300 flex items-center justify-center gap-2 border"
                :class="currentReservation.orderStatus === 'RESERVED'
                  ? 'border-orange-400/80 bg-orange-400/90 text-white shadow-sm hover:bg-orange-500 hover:shadow-orange-500/20 hover:shadow-md'
                  : 'border-gray-100 bg-gray-50/50 text-gray-400 cursor-not-allowed'"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" /></svg>
                注文を確定する
              </button>

              <button
                type="button"
                @click="markPaid()"
                :disabled="isPaymentActionDisabled(currentReservation)"
                class="flex-1 rounded-xl px-5 py-3.5 text-sm font-semibold transition-all duration-300 flex items-center justify-center gap-2 border"
                :class="!isPaymentActionDisabled(currentReservation)
                  ? 'border-gray-800 bg-gray-900 text-white shadow-sm hover:bg-gray-800 hover:shadow-gray-900/20 hover:shadow-md'
                  : 'border-gray-100 bg-gray-50/50 text-gray-400 cursor-not-allowed'"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" /></svg>
                {{ paymentActionLabel(currentReservation) }}
              </button>
            </div>
          </div>

          <div v-else class="p-12 text-center text-sm text-gray-500 flex flex-col items-center">
            <div class="w-16 h-16 bg-gray-50 rounded-full flex items-center justify-center mb-4">
              <svg class="w-8 h-8 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" /></svg>
            </div>
            <p class="font-bold text-gray-900 mb-1">進行中の注文はありません</p>
            <p class="text-xs mb-4">現在対応が必要な注文はありません。</p>
            <RouterLink to="/menus/today" class="inline-flex items-center gap-2 bg-white border border-gray-200 px-4 py-2 rounded-xl text-bento-orange hover:bg-orange-50 font-bold shadow-sm transition-colors">
              本日メニューを見る
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"/></svg>
            </RouterLink>
          </div>
        </article>

        <article class="rounded-[2rem] border border-gray-50/50 bg-white shadow-[0_8px_30px_rgb(0,0,0,0.04)] overflow-hidden transition-shadow duration-300 hover:shadow-[0_8px_30px_rgb(0,0,0,0.08)]">
          <div class="flex items-center justify-between border-b border-gray-50/80 px-8 py-5 bg-gradient-to-r from-gray-50/50 to-white">
            <h3 class="text-sm font-semibold text-gray-800 tracking-wide">対応待ち一覧</h3>
            <span class="text-xs font-bold text-gray-400 bg-gray-50/80 px-3 py-1 rounded-lg">{{ pendingReservations.length }} 件</span>
          </div>

          <div v-if="pendingReservations.length" class="divide-y divide-gray-50/80">
            <div v-for="item in pendingReservations" :key="item.id" class="p-6 hover:bg-gray-50/30 transition-colors duration-200">
              <div class="flex justify-between items-start">
                <div>
                  <p class="text-xs font-semibold text-gray-800">#{{ item.id }}</p>
                  <p class="text-[11px] text-gray-500 font-medium mt-1 tracking-wide">{{ item.reservationDate }} | メニュー: {{ item.menuId }} × {{ item.quantity || 1 }} | {{ paymentMethodLabel(item.paymentMethod) }}</p>
                </div>
                <div class="flex gap-2">
                  <span :class="statusPill(item.orderStatus, 'order')">{{ orderStatusLabel(item.orderStatus) }}</span>
                  <span :class="statusPill(item.paymentStatus, 'payment')">{{ paymentStatusLabel(item.paymentStatus) }}</span>
                </div>
              </div>
              <div class="mt-4 flex gap-2.5">
                <button v-if="item.orderStatus === 'RESERVED'" type="button" @click="confirmReservation(item.id)" class="rounded-xl border border-orange-100/80 bg-orange-50/50 px-4 py-2 text-[11px] font-semibold text-orange-600 hover:bg-orange-100/50 transition-colors">確認</button>
                <button v-if="!isPaymentActionDisabled(item)" type="button" @click="markPaid(item.id)" class="rounded-xl border border-gray-100 bg-gray-50/50 px-4 py-2 text-[11px] font-semibold text-gray-600 hover:bg-gray-100/50 hover:text-gray-800 transition-colors">{{ paymentActionLabel(item) }}</button>
              </div>
            </div>
          </div>
          <div v-else class="p-10 text-center text-sm font-medium text-gray-400">対応中の注文はありません。</div>
        </article>
      </div>

      <!-- Flow Guide -->
      <article class="rounded-[2rem] border border-gray-100 bg-white shadow-sm h-fit sticky top-24 overflow-hidden">
        <h3 class="text-sm font-bold text-gray-900 border-b border-gray-50 px-8 py-5 bg-gray-50/30 tracking-wide">注文の進み方</h3>
        <div class="p-8 space-y-6 relative before:absolute before:inset-y-10 before:left-[39px] before:w-px before:bg-gray-200">
          <div class="relative flex gap-4">
            <div class="z-10 flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-gray-900 text-[10px] font-bold text-white ring-4 ring-white">1</div>
            <div>
              <p class="text-[13px] font-bold text-gray-900 tracking-wide">予約済み</p>
              <p class="text-[11px] text-gray-500 font-medium mt-1 leading-relaxed">注文作成後の初期状態。</p>
            </div>
          </div>
          <div class="relative flex gap-4">
            <div class="z-10 flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-white text-[10px] font-bold text-gray-600 ring-4 ring-white border border-gray-300">2</div>
            <div>
              <p class="text-[13px] font-bold text-gray-900 tracking-wide">確認済み</p>
              <p class="text-[11px] text-gray-500 font-medium mt-1 leading-relaxed">内容を確認した状態。</p>
            </div>
          </div>
          <div class="relative flex gap-4">
            <div class="z-10 flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-white text-[10px] font-bold text-gray-600 ring-4 ring-white border border-gray-300">3</div>
            <div>
              <p class="text-[13px] font-bold text-gray-900 tracking-wide">支払済み</p>
              <p class="text-[11px] text-gray-500 font-medium mt-1 leading-relaxed">支払完了。履歴へ移動します。</p>
            </div>
          </div>
        </div>
      </article>
    </section>

    <!-- Profile Tab -->
    <section v-else-if="activeTab === 'profile'" class="grid gap-6 md:grid-cols-[1fr_2fr]">
      <!-- Summary Card -->
      <article class="rounded-[2rem] border border-gray-100 bg-white shadow-sm overflow-hidden flex flex-col items-center p-8 text-center">
        <div class="w-24 h-24 rounded-full bg-gray-100 flex items-center justify-center text-gray-400 text-3xl font-black mb-4">
          {{ profile?.name ? profile.name.charAt(0) : '?' }}
        </div>
        <h3 class="text-xl font-black text-gray-900">{{ profile?.name || 'ゲストユーザー' }}</h3>
        <p class="text-sm font-medium text-gray-500 mt-1">{{ profile?.email || '登録済みメールアドレス' }}</p>
        
        <div class="w-full h-px bg-gray-100 my-6"></div>
        
        <div class="w-full grid grid-cols-2 gap-4">
          <div class="bg-white rounded-2xl p-4 border border-gray-100 shadow-sm">
            <p class="text-[10px] font-bold text-gray-400 uppercase tracking-widest">総注文数</p>
            <p class="text-2xl font-black text-gray-900 mt-1">{{ reservations.length }}</p>
          </div>
          <div class="bg-white rounded-2xl p-4 border border-gray-100 shadow-sm">
            <p class="text-[10px] font-bold text-gray-400 uppercase tracking-widest flex items-center justify-center gap-1"><span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>支払済み</p>
            <p class="text-2xl font-black text-gray-900 mt-1">{{ paidReservations.length }}</p>
          </div>
        </div>
      </article>

      <div class="space-y-6">
        <form @submit.prevent="submitNameUpdate" class="rounded-3xl border border-gray-100 bg-white p-6 sm:p-8 shadow-sm hover:shadow-md transition-shadow relative overflow-hidden group">
          <div class="absolute top-0 right-0 w-32 h-32 bg-orange-50 rounded-bl-[100px] -z-10 group-hover:scale-110 transition-transform duration-500"></div>
          <div class="flex items-center gap-3 mb-6">
            <div class="w-10 h-10 rounded-full bg-orange-100 text-bento-orange flex items-center justify-center">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" /></svg>
            </div>
            <div>
              <h3 class="text-lg font-bold text-gray-900">表示名の変更</h3>
              <p class="text-xs text-gray-500">システム上で表示される名前を更新します</p>
            </div>
          </div>
          <div class="flex flex-col sm:flex-row gap-3">
            <input v-model="name" placeholder="氏名を入力" required class="flex-1 rounded-xl border border-gray-200 px-4 py-3 text-sm font-medium outline-none focus:border-bento-orange focus:ring-2 focus:ring-orange-100 transition-all bg-gray-50/50" />
            <button type="submit" class="rounded-xl bg-gray-900 px-6 py-3 text-sm font-bold text-white hover:bg-gray-800 hover:shadow-lg hover:shadow-gray-900/20 transition-all active:scale-95 whitespace-nowrap">更新する</button>
          </div>
        </form>

        <form @submit.prevent="submitPasswordUpdate" class="rounded-3xl border border-gray-100 bg-white p-6 sm:p-8 shadow-sm hover:shadow-md transition-shadow relative overflow-hidden group">
          <div class="absolute top-0 right-0 w-32 h-32 bg-gray-50 rounded-bl-[100px] -z-10 group-hover:scale-110 transition-transform duration-500"></div>
          <div class="flex items-center gap-3 mb-6">
            <div class="w-10 h-10 rounded-full bg-gray-100 text-gray-600 flex items-center justify-center">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" /></svg>
            </div>
            <div>
              <h3 class="text-lg font-bold text-gray-900">パスワード変更（メール認証）</h3>
              <p class="text-xs text-gray-500">登録済みメールアドレス宛に、安全な変更リンクを送信します</p>
            </div>
          </div>
          <div class="rounded-2xl border border-gray-100 bg-gradient-to-br from-gray-50/80 to-white px-5 py-4 shadow-sm">
            <div class="flex items-start gap-3">
              <div class="mt-0.5 flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-orange-50 text-bento-orange border border-orange-100">
                <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 11c0-3.314 2.686-6 6-6m0 0v4m0-4h-4M6 13a6 6 0 1012 0 6 6 0 00-12 0zm3 0l2 2 4-4" /></svg>
              </div>
              <div class="space-y-1.5">
                <p class="text-sm font-semibold text-gray-800">変更リンクをメールで送信します</p>
                <p class="text-xs leading-6 text-gray-500">{{ profile?.email || '登録済みメールアドレス' }} 宛に、1回限り有効なパスワード再設定リンクを送ります。メール内のページで新しいパスワードを設定してください。</p>
              </div>
            </div>
          </div>
          <div class="mt-5 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
            <p class="text-[11px] text-gray-400">メール認証済みアカウントのみ利用できます。</p>
            <button type="submit" class="inline-flex items-center justify-center rounded-xl bg-gray-900 px-6 py-3 text-sm font-bold text-white hover:bg-gray-800 hover:shadow-lg hover:shadow-gray-900/20 transition-all active:scale-95 whitespace-nowrap">変更リンクをメール送信</button>
          </div>
        </form>
      </div>
    </section>

    <!-- Diet Tab -->
    <section v-else-if="activeTab === 'diet'" class="grid gap-6 md:grid-cols-[1fr_2fr]">
      <article class="rounded-[2rem] border border-gray-100 bg-white shadow-sm overflow-hidden flex flex-col items-center p-8 text-center">
        <div class="w-20 h-20 rounded-full bg-orange-50 flex items-center justify-center text-orange-500 text-2xl font-black mb-4 border border-orange-100">
          <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 18.657A8 8 0 016.343 7.343S7 9 9 10c0-2 .5-5 2.986-7C14 5 16.09 5.777 17.656 7.343A7.975 7.975 0 0120 13a7.975 7.975 0 01-2.343 5.657z" /></svg>
        </div>
        <h3 class="text-xl font-black text-gray-900">飲食設定</h3>
        <p class="text-sm font-medium text-gray-500 mt-1">熱量目標とアレルゲンを設定すると、メニューに自動で注意喚起が表示されます。</p>
        <div class="w-full h-px bg-gray-100 my-6"></div>
        <div class="w-full space-y-3 text-left">
          <div v-if="targetCalorie" class="flex items-center justify-between rounded-xl bg-orange-50 px-4 py-3 border border-orange-100">
            <span class="text-xs font-bold text-orange-600">目標熱量</span>
            <span class="text-sm font-black text-orange-800">{{ targetCalorie }} kcal</span>
          </div>
          <div v-if="allergenSettings.length > 0" class="flex flex-wrap gap-1">
            <span v-for="a in allergenSettings" :key="a" class="inline-flex items-center rounded-md bg-red-50 px-2 py-1 text-xs font-bold text-red-600 ring-1 ring-inset ring-red-500/10">
              {{ a }}
            </span>
          </div>
          <div v-if="!targetCalorie && allergenSettings.length === 0" class="text-center text-xs text-gray-400">
            まだ設定されていません
          </div>
        </div>
      </article>

      <div class="space-y-6">
        <form @submit.prevent="submitDietUpdate" class="rounded-3xl border border-gray-100 bg-white p-6 sm:p-8 shadow-sm hover:shadow-md transition-shadow relative overflow-hidden group">
          <div class="absolute top-0 right-0 w-32 h-32 bg-orange-50 rounded-bl-[100px] -z-10 group-hover:scale-110 transition-transform duration-500"></div>
          <div class="flex items-center gap-3 mb-6">
            <div class="w-10 h-10 rounded-full bg-orange-100 text-bento-orange flex items-center justify-center">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 18.657A8 8 0 016.343 7.343S7 9 9 10c0-2 .5-5 2.986-7C14 5 16.09 5.777 17.656 7.343A7.975 7.975 0 0120 13a7.975 7.975 0 01-2.343 5.657z" /></svg>
            </div>
            <div>
              <h3 class="text-lg font-bold text-gray-900">目標熱量</h3>
              <p class="text-xs text-gray-500">1日の目標摂取カロリーを設定します（任意）</p>
            </div>
          </div>
          <div class="flex items-center gap-3">
            <input v-model.number="targetCalorie" type="number" min="0" placeholder="例: 850" class="flex-1 rounded-xl border border-gray-200 px-4 py-3 text-sm font-medium outline-none focus:border-bento-orange focus:ring-2 focus:ring-orange-100 transition-all bg-gray-50/50" />
            <span class="text-sm font-bold text-gray-500">kcal</span>
          </div>

          <div class="mt-8">
            <div class="flex items-center gap-3 mb-4">
              <div class="w-10 h-10 rounded-full bg-red-50 text-red-500 flex items-center justify-center">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
              </div>
              <div>
                <h3 class="text-lg font-bold text-gray-900">アレルゲン設定</h3>
                <p class="text-xs text-gray-500">避けるべきアレルゲンを選択します</p>
              </div>
            </div>
            <div class="flex flex-wrap gap-2">
              <label v-for="allergen in ALLERGEN_OPTIONS" :key="allergen" class="cursor-pointer select-none">
                <input type="checkbox" :value="allergen" v-model="allergenSettings" class="peer sr-only" />
                <span class="inline-flex items-center rounded-xl border border-gray-200 bg-white px-3 py-1.5 text-xs font-bold text-gray-600 transition-all peer-checked:border-red-300 peer-checked:bg-red-50 peer-checked:text-red-700 hover:border-red-200">
                  {{ allergen }}
                </span>
              </label>
            </div>
            <p v-if="allergenSettings.length > 0" class="mt-3 text-[11px] font-medium text-red-500">
              選択中: {{ allergenSettings.join('、') }}
            </p>
          </div>

          <div class="mt-8 flex justify-end">
            <button type="submit" class="rounded-xl bg-gray-900 px-6 py-3 text-sm font-bold text-white hover:bg-gray-800 hover:shadow-lg hover:shadow-gray-900/20 transition-all active:scale-95 whitespace-nowrap">設定を保存</button>
          </div>
        </form>
      </div>
    </section>

    <!-- Payments Tab -->
    <section v-else class="space-y-6">
      <!-- Quick Stats -->
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div class="bg-white rounded-[1.5rem] p-6 border border-gray-100 shadow-sm transition-shadow hover:shadow-md">
          <p class="text-[10px] font-bold text-gray-400 uppercase tracking-widest mb-2 flex items-center gap-1.5">全注文</p>
          <p class="text-3xl font-black text-gray-900">{{ reservations.length }}<span class="text-[11px] text-gray-400 font-bold ml-1.5">件</span></p>
        </div>
        <div class="bg-white rounded-[1.5rem] p-6 border border-gray-100 shadow-sm transition-shadow hover:shadow-md">
          <p class="text-[10px] font-bold text-gray-400 uppercase tracking-widest mb-2 flex items-center gap-1.5"><span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>支払済み</p>
          <p class="text-3xl font-black text-gray-900">{{ paidReservations.length }}<span class="text-[11px] text-gray-400 font-bold ml-1.5">件</span></p>
        </div>
        <div class="bg-white rounded-[1.5rem] p-6 border border-gray-100 shadow-sm transition-shadow hover:shadow-md">
          <p class="text-[10px] font-bold text-gray-400 uppercase tracking-widest mb-2 flex items-center gap-1.5"><span class="w-1.5 h-1.5 rounded-full bg-yellow-400"></span>未払い</p>
          <p class="text-3xl font-black text-gray-900">{{ unpaidReservations.length }}<span class="text-[11px] text-gray-400 font-bold ml-1.5">件</span></p>
        </div>
        <div class="bg-white rounded-[1.5rem] p-6 border border-gray-100 shadow-sm transition-shadow hover:shadow-md">
          <p class="text-[10px] font-bold text-gray-400 uppercase tracking-widest mb-2 flex items-center gap-1.5"><span class="w-1.5 h-1.5 rounded-full bg-blue-400"></span>確認済み</p>
          <p class="text-3xl font-black text-gray-900">{{ reservations.filter(r => r.orderStatus === 'CONFIRMED').length }}<span class="text-[11px] text-gray-400 font-bold ml-1.5">件</span></p>
        </div>
      </div>

      <div class="rounded-3xl border border-gray-100 bg-white shadow-sm overflow-hidden">
        <div class="flex items-center justify-between border-b border-gray-50 px-6 py-5 bg-gradient-to-r from-gray-50/80 to-white">
          <div>
            <h3 class="text-base font-bold text-gray-900">すべての履歴</h3>
            <p class="text-[11px] text-gray-500 font-semibold mt-0.5">これまでの注文と支払いの履歴一覧</p>
          </div>
          <div class="flex gap-2 hidden sm:flex">
            <span class="text-xs font-bold text-emerald-700 bg-emerald-50 px-3 py-1 rounded-xl border border-emerald-100 shadow-sm shadow-emerald-500/5">支払済み {{ paidReservations.length }}</span>
            <span class="text-xs font-bold text-orange-600 bg-orange-50 px-3 py-1 rounded-xl border border-orange-100 shadow-sm shadow-orange-500/5">未払い {{ unpaidReservations.length }}</span>
          </div>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full text-left text-sm whitespace-nowrap">
            <thead class="bg-gray-50/50 text-[11px] font-bold text-gray-500 border-b border-gray-100 uppercase tracking-wider">
              <tr>
                <th class="px-6 py-4">注文番号</th>
                <th class="px-6 py-4">予約日</th>
                <th class="px-6 py-4">内容</th>
                <th class="px-6 py-4">支払方法</th>
                <th class="px-6 py-4 text-center">予約状態</th>
                <th class="px-6 py-4 text-center">支払状態</th>
              </tr>
            </thead>
            <tbody v-if="reservations.length" class="divide-y divide-gray-50">
              <tr v-for="item in reservations" :key="item.id" class="hover:bg-gray-50/80 transition-colors group">
                <td class="px-6 py-4">
                  <span class="font-bold text-gray-900 group-hover:text-bento-orange transition-colors">#{{ item.id }}</span>
                </td>
                <td class="px-6 py-4 font-semibold text-gray-600">{{ item.reservationDate }}</td>
                <td class="px-6 py-4">
                  <div class="flex items-center gap-2">
                    <div class="w-6 h-6 rounded bg-orange-100 text-orange-600 flex items-center justify-center border border-orange-200">
                      <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" /></svg>
                    </div>
                    <span class="font-bold text-gray-700">メニュー {{ item.menuId }}</span>
                    <span class="text-xs font-bold text-gray-400 bg-gray-100 px-1.5 py-0.5 rounded-md">× {{ item.quantity || 1 }}</span>
                  </div>
                </td>
                <td class="px-6 py-4">
                  <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-gray-50 border border-gray-200 text-xs font-bold text-gray-700 shadow-sm">
                    <svg v-if="item.paymentMethod === 'CARD'" class="w-3 h-3 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" /></svg>
                    <div v-else-if="item.paymentMethod === 'PAYPAY'" class="w-3 h-3 rounded-full bg-emerald-500 flex items-center justify-center text-[8px] font-black italic text-white leading-none">P</div>
                    <svg v-else class="w-3 h-3 text-emerald-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" /></svg>
                    {{ paymentMethodLabel(item.paymentMethod) }}
                  </span>
                </td>
                <td class="px-6 py-4 text-center">
                  <span :class="statusPill(item.orderStatus, 'order')">{{ orderStatusLabel(item.orderStatus) }}</span>
                </td>
                <td class="px-6 py-4 text-center">
                  <span :class="statusPill(item.paymentStatus, 'payment')">{{ paymentStatusLabel(item.paymentStatus) }}</span>
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr>
                <td colspan="6" class="px-6 py-16 text-center">
                  <div class="flex flex-col items-center justify-center">
                    <div class="w-16 h-16 bg-gray-50 rounded-full flex items-center justify-center mb-4">
                      <svg class="w-8 h-8 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" /></svg>
                    </div>
                    <p class="text-sm font-bold text-gray-900 mb-1">支払い履歴がありません</p>
                    <p class="text-xs text-gray-500">注文を完了すると、ここに履歴が表示されます。</p>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'

import { useAuthStore } from '../stores/auth'
import { fetchUserProfile, fetchUserReservations, markReservationPaid, requestUserPasswordReset, updateReservationStatus, updateUserName, updateUserDietPreferences } from '../api/user'

const tabs = [
  { key: 'orders', label: '注文一覧' },
  { key: 'profile', label: '個人情報' },
  { key: 'diet', label: '飲食設定' },
  { key: 'payments', label: '支払い履歴' },
]

const ALLERGEN_OPTIONS = [
  'えび', 'かに', '小麦', 'そば', '卵', '乳', '落花生', 'くるみ',
  'あわび', 'いか', 'いくら', 'オレンジ', 'カシューナッツ', 'キウイ',
  '牛肉', '鶏肉', '豚肉', 'まつたけ', 'もも', 'やまいも', 'りんご', 'ゼラチン'
]

const authStore = useAuthStore()
const activeTab = ref('orders')
const currentReservation = ref(null)
const reservations = ref([])
const profile = ref(null)
const name = ref('')
const password = ref('')
const showPassword = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const loadingMessage = ref('')
const targetCalorie = ref(null)
const allergenSettings = ref([])

const paidReservations = computed(() => reservations.value.filter((item) => item.paymentStatus === 'PAID'))
const unpaidReservations = computed(() => reservations.value.filter((item) => item.paymentStatus !== 'PAID' && item.paymentStatus !== 'REFUNDED'))
const refundedReservations = computed(() => reservations.value.filter((item) => item.paymentStatus === 'REFUNDED'))
const pendingReservations = computed(() => reservations.value.filter((item) => item.orderStatus !== 'CANCELLED' && item.orderStatus !== 'REFUNDED' && item.paymentStatus !== 'PAID' && item.paymentStatus !== 'REFUNDED'))
const isEmailVerified = computed(() => profile.value?.emailVerified === true)

async function loadProfile() {
  try {
    const response = await fetchUserProfile()
    profile.value = response.data
    name.value = response.data?.name || ''
    targetCalorie.value = response.data?.targetCalorie || null
    allergenSettings.value = response.data?.allergenSettings
      ? response.data.allergenSettings.split(',').filter(Boolean)
      : []
  } catch (error) {
    errorMessage.value = 'ユーザー情報の取得に失敗しました。'
  }
}

async function loadReservation() {
  try {
    loadingMessage.value = '読込中...'
    const response = await fetchUserReservations()
    reservations.value = response.data || []
    currentReservation.value = reservations.value.find((item) => item.paymentStatus !== 'PAID') || reservations.value[0] || null
    errorMessage.value = ''
  } catch (error) {
    errorMessage.value = error.message || '予約情報の取得に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

async function markPaid(reservationId = currentReservation.value?.id) {
  if (!reservationId) return
  const reservation = reservations.value.find((item) => item.id === reservationId)
  if (!reservation || isPaymentActionDisabled(reservation)) return
  const requestedStatus = reservation.paymentMethod === 'CASH' ? 'PAYMENT_REQUESTED' : 'PAID'
  const confirmMessage = requestedStatus === 'PAYMENT_REQUESTED'
    ? '現金支払いの確認申請を送信しますか？'
    : '支払済みに更新しますか？'
  if (!window.confirm(confirmMessage)) return

  try {
    loadingMessage.value = '更新中...'
    await markReservationPaid(reservationId, requestedStatus)
    await loadReservation()
    successMessage.value = requestedStatus === 'PAYMENT_REQUESTED'
      ? '現金支払いの確認申請を送信しました。'
      : '支払状態を更新しました。'
    errorMessage.value = ''
    setTimeout(() => { successMessage.value = '' }, 3000)
  } catch (error) {
    errorMessage.value = error.message || '支払状態の更新に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

async function confirmReservation(reservationId = currentReservation.value?.id) {
  if (!reservationId) return
  if (!window.confirm('この注文内容で確定しますか？')) return

  try {
    loadingMessage.value = '確認中...'
    await updateReservationStatus(reservationId, 'CONFIRMED')
    await loadReservation()
    successMessage.value = '注文内容を確認しました。次にお支払い状況を更新できます。'
    errorMessage.value = ''
    setTimeout(() => { successMessage.value = '' }, 3000)
  } catch (error) {
    errorMessage.value = error.message || '注文確認に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

async function submitNameUpdate() {
  if (!name.value.trim()) {
    errorMessage.value = '氏名を入力してください。'
    return
  }

  try {
    loadingMessage.value = '更新中...'
    const response = await updateUserName({ name: name.value })
    profile.value = response.data
    authStore.setUserName(response.data?.name)
    successMessage.value = '氏名を更新しました。'
    errorMessage.value = ''
    setTimeout(() => { successMessage.value = '' }, 3000)
  } catch (error) {
    errorMessage.value = '氏名の更新に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

async function submitPasswordUpdate() {
  if (!isEmailVerified.value) {
    errorMessage.value = 'メール認証完了後にパスワード変更が可能です。登録メールの認証リンクを先に完了してください。'
    return
  }

  try {
    loadingMessage.value = '確認メールを送信中...'
    await requestUserPasswordReset()
    password.value = ''
    successMessage.value = 'パスワード変更用の確認メールを送信しました。メール内リンクから新しいパスワードを設定してください。'
    errorMessage.value = ''
    setTimeout(() => { successMessage.value = '' }, 3000)
  } catch (error) {
    errorMessage.value = error.message || 'パスワード更新に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

async function submitDietUpdate() {
  try {
    loadingMessage.value = '更新中...'
    const payload = {
      targetCalorie: targetCalorie.value || null,
      allergenSettings: allergenSettings.value.length > 0 ? allergenSettings.value.join(',') : null,
    }
    const response = await updateUserDietPreferences(payload)
    profile.value = response.data
    // 同步更新 allergenSettings 陣列，確保畫面顯示與後端一致
    allergenSettings.value = response.data?.allergenSettings
      ? response.data.allergenSettings.split(',').filter(Boolean)
      : []
    targetCalorie.value = response.data?.targetCalorie || null
    successMessage.value = '飲食設定を更新しました。'
    errorMessage.value = ''
    setTimeout(() => { successMessage.value = '' }, 3000)
  } catch (error) {
    errorMessage.value = error.message || '飲食設定の更新に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

function orderStatusLabel(status) {
  if (status === 'CONFIRMED') return '確認済み'
  if (status === 'CANCELLED') return 'キャンセル'
  return '予約済み'
}

function paymentStatusLabel(status) {
  if (status === 'PAID') return '支払済み'
  if (status === 'PAYMENT_REQUESTED') return '確認待ち'
  if (status === 'REFUNDED') return '返金済み'
  return '未払い'
}

function paymentMethodLabel(method) {
  if (method === 'CARD') return 'クレジットカード'
  if (method === 'PAYPAY') return 'PayPay'
  return '現金'
}

function statusPill(status, type) {
  if (type === 'payment') {
    if (status === 'PAID') {
      return 'rounded-full border border-emerald-200 bg-emerald-50 px-3 py-1 text-xs font-bold text-emerald-700'
    }
    if (status === 'PAYMENT_REQUESTED') {
      return 'rounded-full border border-blue-200 bg-blue-50 px-3 py-1 text-xs font-bold text-blue-700'
    }
    if (status === 'REFUNDED') {
      return 'rounded-full border border-purple-200 bg-purple-50 px-3 py-1 text-xs font-bold text-purple-700'
    }
    return 'rounded-full border border-orange-200 bg-orange-50 px-3 py-1 text-xs font-bold text-orange-600'
  }

  if (status === 'CONFIRMED') return 'rounded-full border border-blue-200 bg-blue-50 px-3 py-1 text-xs font-bold text-blue-700'
  if (status === 'CANCELLED') return 'rounded-full border border-gray-200 bg-gray-100 px-3 py-1 text-xs font-bold text-gray-600'
  return 'rounded-full border border-yellow-200 bg-yellow-50 px-3 py-1 text-xs font-bold text-yellow-700'
}

function isPaymentActionDisabled(item) {
  if (!item) return true
  return item.paymentStatus === 'PAID' || item.paymentStatus === 'PAYMENT_REQUESTED' || item.paymentStatus === 'REFUNDED'
}

function paymentActionLabel(item) {
  if (!item) return '支払済みにする'
  if (item.paymentMethod === 'CASH') return '支払い確認を申請'
  return '支払済みにする'
}

onMounted(async () => {
  await loadProfile()
  await loadReservation()
})
</script>
