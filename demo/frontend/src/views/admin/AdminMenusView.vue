<template>
  <div class="space-y-6">
    <div class="bg-white rounded-[2rem] shadow-[0_8px_30px_rgb(0,0,0,0.04)] border border-gray-50/50 p-6 md:p-10">
      <div class="space-y-8">
        <!-- Alerts -->
        <div v-if="loadingMessage" class="p-4 text-sm text-blue-800 rounded-xl bg-blue-50/80 border border-blue-100" role="alert">
          {{ loadingMessage }}
        </div>
        <div v-if="successMessage" class="p-4 text-sm text-green-800 rounded-xl bg-green-50/80 border border-green-100" role="alert">
          {{ successMessage }}
        </div>
        <div v-if="errorMessage" class="p-4 text-sm text-red-800 rounded-xl bg-red-50/80 border border-red-100" role="alert">
          {{ errorMessage }}
        </div>

        <!-- Form -->
        <form ref="menuFormRef" class="bg-gray-50/50 p-6 md:p-10 rounded-[2rem] border border-gray-100/50 shadow-[inset_0_2px_4px_rgb(0,0,0,0.02)]" @submit.prevent="submitMenu">
          <div class="flex items-center justify-between mb-8">
            <h3 class="text-xl font-medium text-gray-800 tracking-normal">{{ editingId ? 'メニューを編集' : '新規メニュー追加' }}</h3>
            <span v-if="editingId" class="px-4 py-1.5 bg-blue-50/80 text-blue-600 text-[11px] font-bold rounded-full tracking-normal border border-blue-100/50 uppercase">編集中</span>
          </div>
          
          <div class="grid gap-8 lg:grid-cols-12 lg:items-start">
            <div class="space-y-6 lg:col-span-8">
              <div class="bg-white p-8 rounded-[1.5rem] border border-gray-50/80 shadow-[0_4px_20px_rgb(0,0,0,0.03)] space-y-6">
                <div class="grid gap-6 md:grid-cols-2">
                  <div class="md:col-span-2">
                    <label class="block mb-2 text-[11px] font-bold text-gray-400 uppercase tracking-normal">メニュー名 <span class="text-orange-400">*</span></label>
                    <input v-model="form.name" class="bg-gray-50/50 border border-gray-100 text-gray-800 text-sm font-medium rounded-xl focus:ring-4 focus:ring-orange-50 focus:border-orange-200 block w-full p-3.5 transition-all outline-none" placeholder="例: ハンバーグ弁当" required />
                  </div>
                  <div>
                    <label class="block mb-2 text-[11px] font-bold text-gray-400 uppercase tracking-normal">価格 (円) <span class="text-orange-400">*</span></label>
                    <div class="relative">
                      <span class="absolute inset-y-0 left-0 flex items-center pl-4 text-gray-400 font-bold">¥</span>
                      <input v-model.number="form.price" type="number" min="0" class="bg-gray-50/50 border border-gray-100 text-gray-800 text-sm font-medium rounded-xl focus:ring-4 focus:ring-orange-50 focus:border-orange-200 block w-full pl-9 p-3.5 transition-all outline-none" placeholder="0" required />
                    </div>
                  </div>
                  <div>
                    <label class="block mb-2 text-[11px] font-bold text-gray-400 uppercase tracking-normal">熱量 (kcal)</label>
                    <input v-model.number="form.calorie" type="number" min="0" class="bg-gray-50/50 border border-gray-100 text-gray-800 text-sm font-medium rounded-xl focus:ring-4 focus:ring-orange-50 focus:border-orange-200 block w-full p-3.5 transition-all outline-none" placeholder="例: 850" />
                  </div>
                  <div>
                    <label class="block mb-2 text-[11px] font-bold text-gray-400 uppercase tracking-normal">提供日 <span class="text-orange-400">*</span></label>
                    <input v-model="form.availableDate" type="date" class="bg-gray-50/50 border border-gray-100 text-gray-800 text-sm font-medium rounded-xl focus:ring-4 focus:ring-orange-50 focus:border-orange-200 block w-full p-3.5 transition-all outline-none" required />
                  </div>
                </div>

                <div>
                  <label class="block mb-2 text-[11px] font-bold text-gray-400 uppercase tracking-normal">説明</label>
                  <textarea v-model="form.description" rows="3" class="block p-3.5 w-full text-sm font-medium text-gray-800 bg-gray-50/50 rounded-xl border border-gray-100 focus:ring-4 focus:ring-orange-50 focus:border-orange-200 transition-all resize-none outline-none" placeholder="メニューの説明文を入力"></textarea>
                </div>

                <div>
                  <label class="block mb-3 text-[11px] font-bold text-gray-400 uppercase tracking-normal">含有するアレルゲン</label>
                  <div class="flex flex-wrap gap-2">
                    <label
                      v-for="allergen in ALLERGEN_OPTIONS"
                      :key="allergen"
                      class="cursor-pointer select-none"
                    >
                      <input
                        type="checkbox"
                        :value="allergen"
                        v-model="form.allergens"
                        class="peer sr-only"
                      />
                      <span class="inline-flex items-center rounded-xl border border-gray-200 bg-white px-3 py-1.5 text-xs font-bold text-gray-600 transition-all peer-checked:border-orange-300 peer-checked:bg-orange-50 peer-checked:text-orange-700 hover:border-orange-200">
                        {{ allergen }}
                      </span>
                    </label>
                  </div>
                  <p v-if="form.allergens.length > 0" class="mt-2 text-[11px] font-medium text-orange-500">
                    選択中: {{ form.allergens.join('、') }}
                  </p>
                </div>
              </div>
            </div>

            <div class="lg:col-span-4 h-full">
              <div class="bg-white rounded-[1.5rem] border border-gray-50/80 shadow-[0_4px_20px_rgb(0,0,0,0.03)] flex flex-col h-full overflow-hidden">
                <div class="p-5 border-b border-gray-50/80 bg-gray-50/30">
                  <label class="block text-[11px] font-bold text-gray-400 uppercase tracking-normal">画像アップロード</label>
                </div>
                <div class="p-6 flex flex-col gap-4 flex-grow justify-center">
                  <input ref="imageInputRef" type="file" accept="image/*" @change="onImageSelected" class="hidden" />
                  
                  <!-- Preview Area if image exists -->
                  <div v-if="form.imageUrl" class="relative group rounded-[1rem] overflow-hidden border border-gray-100 aspect-[4/3] bg-gray-50 flex-shrink-0 shadow-sm">
                    <img :src="displayImageUrl(form.imageUrl)" alt="menu preview" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105" />
                    <div class="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex flex-col items-center justify-center gap-3 backdrop-blur-[2px]">
                      <button type="button" @click="openImagePicker" class="px-5 py-2.5 bg-white/95 rounded-xl text-[13px] font-medium text-gray-800 hover:bg-white hover:scale-105 transition-all shadow-lg border border-white/50">変更する</button>
                      <button type="button" @click="clearImage" class="px-5 py-2.5 bg-gray-900/90 rounded-xl text-[13px] font-medium text-white hover:bg-gray-900 hover:scale-105 transition-all shadow-lg border border-gray-700/50">削除する</button>
                    </div>
                  </div>

                  <!-- Upload Button Area if no image -->
                  <div v-else class="rounded-[1rem] border-2 border-dashed border-gray-200/80 bg-gray-50/50 p-6 flex flex-col items-center justify-center text-center transition-all duration-300 hover:bg-orange-50/50 hover:border-orange-200 cursor-pointer group/upload aspect-[4/3]" @click="openImagePicker">
                    <div class="h-14 w-14 rounded-full bg-white border border-gray-100/80 flex items-center justify-center text-gray-300 mb-4 shadow-sm group-hover/upload:text-orange-400 group-hover/upload:border-orange-100 group-hover/upload:shadow-md group-hover/upload:scale-110 transition-all duration-300">
                      <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" /></svg>
                    </div>
                    <span class="text-sm font-medium text-gray-400 group-hover/upload:text-orange-500 transition-colors tracking-wide">画像を選択</span>
                    <p class="mt-1.5 text-[10px] font-bold text-gray-300 uppercase tracking-normal">PNG / JPG / WEBP</p>
                  </div>

                  <!-- Status / File Name -->
                  <div v-if="selectedImageName && !form.imageUrl" class="flex items-center gap-2 p-2.5 rounded-lg bg-gray-50 border border-gray-200">
                    <svg class="w-4 h-4 text-emerald-500 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                    <span class="text-xs font-medium text-gray-700 truncate" :title="selectedImageName">{{ selectedImageName }}</span>
                  </div>
                  
                  <div v-if="editingId && form.imageUrl" class="text-center text-xs font-bold text-emerald-600 bg-emerald-50 py-1.5 rounded-md border border-emerald-100">
                    ✓ 保存済み
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="mt-8 flex flex-wrap items-center justify-end gap-4 border-t border-gray-100/80 pt-8">
            <button
              v-if="editingId"
              type="button"
              @click="resetForm"
              class="inline-flex items-center justify-center rounded-xl border border-gray-200/80 bg-white px-6 py-3 text-[13px] font-medium text-gray-600 transition-all duration-300 hover:border-gray-300 hover:bg-gray-50/50 hover:text-gray-800 focus:ring-4 focus:ring-gray-50 active:scale-[0.98]"
            >
              キャンセル
            </button>
            <button
              type="submit"
              class="inline-flex items-center justify-center rounded-xl bg-gray-900 px-8 py-3 text-[13px] font-medium text-white transition-all duration-300 hover:bg-gray-800 hover:shadow-lg hover:shadow-gray-900/20 focus:ring-4 focus:ring-gray-100 disabled:cursor-not-allowed disabled:bg-gray-300 disabled:text-gray-500 disabled:shadow-none active:scale-[0.98]"
              :disabled="isUploadingImage"
            >
              <svg v-if="isUploadingImage" class="animate-spin -ml-1 mr-2.5 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
              {{ editingId ? '更新を保存' : '追加して保存' }}
            </button>
          </div>
        </form>

        <!-- Empty State -->
        <div v-if="!loadingMessage && !menus.length" class="text-center p-8 border-2 border-dashed border-gray-300 rounded-xl bg-gray-50">
          <p class="text-gray-500 font-medium">登録済みメニューはありません。</p>
        </div>

        <!-- Table -->
        <div v-else class="relative overflow-x-auto shadow-[0_4px_20px_rgb(0,0,0,0.03)] sm:rounded-[1.5rem] border border-gray-50/80">
          <table class="w-full text-sm text-left text-gray-500 font-medium">
            <thead class="text-[11px] text-gray-400 font-bold uppercase tracking-normal bg-gray-50/50 border-b border-gray-100/80">
              <tr>
                <th scope="col" class="px-6 py-4 w-28">画像</th>
                <th scope="col" class="px-6 py-4">名称</th>
                <th scope="col" class="px-6 py-4">価格</th>
                <th scope="col" class="px-6 py-4">熱量</th>
                <th scope="col" class="px-6 py-4">アレルゲン</th>
                <th scope="col" class="px-6 py-4">提供日</th>
                <th scope="col" class="px-6 py-4">操作</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              <tr v-for="menu in menus" :key="menu.id" class="bg-white hover:bg-gray-50/50 transition-colors duration-200">
                <td class="px-6 py-4">
                  <div class="w-14 h-14 rounded-xl bg-gray-50/80 border border-gray-100/50 overflow-hidden flex items-center justify-center shrink-0 shadow-[inset_0_2px_4px_rgb(0,0,0,0.02)]">
                    <img v-if="menu.imageUrl" :src="displayImageUrl(menu.imageUrl)" class="w-full h-full object-cover" :alt="menu.name" @error="$event.target.style.display='none'">
                    <svg v-else class="w-6 h-6 text-gray-300" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>
                  </div>
                </td>
                <td class="px-6 py-4 font-medium text-gray-800 tracking-normal">
                  <div class="flex flex-col gap-1">
                    <span>{{ menu.name }}</span>
                    <span class="text-[11px] text-gray-400 font-bold tracking-normal truncate max-w-[180px]">{{ menu.description || '説明なし' }}</span>
                  </div>
                </td>
                <td class="px-6 py-4 font-bold text-gray-700 tracking-normal">¥{{ menu.price.toLocaleString() }}</td>
                <td class="px-6 py-4">
                  <span v-if="menu.calorie" class="inline-flex items-center gap-1 text-xs font-bold text-orange-600">
                    <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 18.657A8 8 0 016.343 7.343S7 9 9 10c0-2 .5-5 2.986-7C14 5 16.09 5.777 17.656 7.343A7.975 7.975 0 0120 13a7.975 7.975 0 01-2.343 5.657z" /></svg>
                    {{ menu.calorie }} kcal
                  </span>
                  <span v-else class="text-xs text-gray-300">—</span>
                </td>
                <td class="px-6 py-4">
                  <div v-if="menu.allergens" class="flex flex-wrap gap-1">
                    <span v-for="a in menu.allergens.split(',').filter(Boolean)" :key="a" class="inline-flex items-center rounded-md bg-red-50 px-2 py-0.5 text-[10px] font-bold text-red-600 ring-1 ring-inset ring-red-500/10">
                      {{ a }}
                    </span>
                  </div>
                  <span v-else class="text-xs text-gray-300">—</span>
                </td>
                <td class="px-6 py-4">
                  <span class="bg-gray-50 text-gray-600 text-[11px] font-bold px-3 py-1.5 rounded-xl border border-gray-100/80 tracking-normal">
                    {{ menu.availableDate }}
                  </span>
                </td>
                <td class="px-6 py-4">
                  <div class="flex items-center gap-4">
                    <button type="button" @click="startEdit(menu)" class="font-medium text-blue-600 hover:text-blue-700 hover:underline transition-colors text-[13px]">編集</button>
                    <button type="button" @click="removeMenu(menu.id, menu.name)" class="font-medium text-gray-400 hover:text-orange-600 hover:underline transition-colors text-[13px]">削除</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { nextTick, onMounted, reactive, ref } from 'vue'

import { createAdminMenu, deleteAdminMenu, fetchAdminMenuReservationSummary, fetchAdminMenus, updateAdminMenu, uploadAdminMenuImage } from '../../api/admin'

const menus = ref([])
const editingId = ref(null)
const errorMessage = ref('')
const successMessage = ref('')
const loadingMessage = ref('読込中...')
const isUploadingImage = ref(false)
const selectedImageName = ref('')
const imageInputRef = ref(null)
const menuFormRef = ref(null)

const ALLERGEN_OPTIONS = [
  'えび', 'かに', '小麦', 'そば', '卵', '乳', '落花生', 'くるみ',
  'あわび', 'いか', 'いくら', 'オレンジ', 'カシューナッツ', 'キウイ',
  '牛肉', '鶏肉', '豚肉', 'まつたけ', 'もも', 'やまいも', 'りんご', 'ゼラチン'
]

const form = reactive({
  name: '',
  price: 0,
  calorie: null,
  allergens: [],
  availableDate: '',
  description: '',
  imageUrl: '',
})

async function loadMenus() {
  try {
    loadingMessage.value = '読込中...'
    const response = await fetchAdminMenus()
    menus.value = response.data || []
    errorMessage.value = ''
  } catch (error) {
    errorMessage.value = 'メニュー一覧の取得に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

function backendOrigin() {
  return import.meta.env.VITE_PUBLIC_BACKEND_ORIGIN
    || import.meta.env.VITE_API_PROXY_TARGET
    || 'http://localhost:8080'
}

function displayImageUrl(imageUrl) {
  if (!imageUrl) {
    return ''
  }

  if (/^https?:\/\//.test(imageUrl)) {
    return imageUrl
  }

  if (imageUrl.startsWith('/')) {
    return `${backendOrigin()}${imageUrl}`
  }

  return `${backendOrigin()}/${imageUrl}`
}

function openImagePicker() {
  imageInputRef.value?.click()
}

async function saveEditedMenuImage(imageUrl, fileName) {
  const payload = {
    name: form.name,
    price: form.price,
    availableDate: form.availableDate,
    description: form.description,
    imageUrl,
  }

  await updateAdminMenu(editingId.value, payload)
  await loadMenus()
  successMessage.value = `「${form.name}」の画像を更新しました。`
  selectedImageName.value = fileName
}

function resetForm() {
  editingId.value = null
  form.name = ''
  form.price = 0
  form.calorie = null
  form.allergens = []
  form.availableDate = ''
  form.description = ''
  form.imageUrl = ''
  selectedImageName.value = ''
}

function startEdit(menu) {
  editingId.value = menu.id
  form.name = menu.name ?? ''
  form.price = Number(menu.price ?? 0)
  form.calorie = menu.calorie ?? null
  form.allergens = menu.allergens ? menu.allergens.split(',').filter(Boolean) : []
  form.availableDate = menu.availableDate ?? ''
  form.description = menu.description ?? ''
  form.imageUrl = menu.imageUrl ?? ''
  selectedImageName.value = ''
  nextTick(() => {
    menuFormRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  })
}

async function onImageSelected(event) {
  const [file] = event.target.files || []
  if (!file) {
    return
  }

  isUploadingImage.value = true
  errorMessage.value = ''

  try {
    const response = await uploadAdminMenuImage(file)
    form.imageUrl = response.data?.imageUrl ?? ''
    if (editingId.value) {
      await saveEditedMenuImage(form.imageUrl, file.name)
    } else {
      selectedImageName.value = file.name
      successMessage.value = '画像をアップロードしました。メニューを追加すると保存されます。'
    }
  } catch (error) {
    errorMessage.value = error.message || '画像アップロードに失敗しました。'
  } finally {
    isUploadingImage.value = false
    event.target.value = ''
  }
}

function clearImage() {
  form.imageUrl = ''
  selectedImageName.value = ''
}

async function submitMenu() {
  if (!form.name.trim()) {
    errorMessage.value = 'メニュー名を入力してください。'
    return
  }

  if (!form.availableDate) {
    errorMessage.value = '提供日を入力してください。'
    return
  }

  if (Number(form.price) <= 0) {
    errorMessage.value = '価格は 0 より大きく入力してください。'
    return
  }

  if (isUploadingImage.value) {
    errorMessage.value = '画像アップロードが完了するまでお待ちください。'
    return
  }

  const payload = {
    name: form.name,
    price: form.price,
    calorie: form.calorie,
    allergens: form.allergens.length > 0 ? form.allergens.join(',') : null,
    availableDate: form.availableDate,
    description: form.description,
    imageUrl: form.imageUrl,
  }

  try {
    loadingMessage.value = editingId.value ? '更新中...' : '追加中...'
    if (editingId.value) {
      await updateAdminMenu(editingId.value, payload)
    } else {
      await createAdminMenu(payload)
    }
    await loadMenus()
    resetForm()
    successMessage.value = 'メニューを保存しました。'
  } catch (error) {
    errorMessage.value = 'メニューの保存に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

async function removeMenu(menuId, menuName) {
  let activeReservationCount = 0

  try {
    const response = await fetchAdminMenuReservationSummary(menuId)
    activeReservationCount = Number(response.data?.activeReservationCount || 0)
  } catch (error) {
    errorMessage.value = '削除前の注文確認に失敗しました。'
    return
  }

  const reservationMessage = activeReservationCount > 0
    ? `このメニューには現在 ${activeReservationCount} 件の注文があります。削除すると一覧や集計結果に影響します。`
    : 'このメニューに紐づく有効な注文は現在ありません。'

  const confirmed = window.confirm(
    `「${menuName}」を削除します。\n\n${reservationMessage}\n削除後に必要になった場合は、新しいメニューとして再登録してください。\n\nこのまま削除しますか？`
  )

  if (!confirmed) {
    return
  }

  try {
    loadingMessage.value = '削除中...'
    await deleteAdminMenu(menuId)
    menus.value = menus.value.filter((menu) => menu.id !== menuId)
    successMessage.value = `「${menuName}」を削除しました。${activeReservationCount > 0 ? `削除時点で ${activeReservationCount} 件の注文がありました。` : ''}必要な場合はメニュー管理から再登録してください。`
    errorMessage.value = ''
  } catch (error) {
    errorMessage.value = 'メニューの削除に失敗しました。'
  } finally {
    loadingMessage.value = ''
  }
}

onMounted(loadMenus)
</script>

<style scoped>
/* Scoped styles removed in favor of Tailwind CSS utility classes */
</style>
