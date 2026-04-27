import { defineStore } from 'pinia'

const STORAGE_KEY = 'bento-cart-items'

function loadStoredItems() {
  if (typeof window === 'undefined') return []

  try {
    const raw = window.localStorage.getItem(STORAGE_KEY)
    const parsed = raw ? JSON.parse(raw) : []
    if (!Array.isArray(parsed) || parsed.length === 0) return []

    const first = parsed[0]
    return [{
      menuId: first.menuId,
      name: first.name,
      price: first.price,
      imageUrl: first.imageUrl || '',
      availableDate: first.availableDate,
      quantity: 1,
    }]
  } catch {
    return []
  }
}

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: loadStoredItems(),
  }),
  getters: {
    totalQuantity: (state) => state.items.reduce((sum, item) => sum + item.quantity, 0),
    itemCount: (state) => state.items.length,
  },
  actions: {
    persist() {
      if (typeof window !== 'undefined') {
        window.localStorage.setItem(STORAGE_KEY, JSON.stringify(this.items))
      }
    },
    addItem(menu) {
      const existing = this.items[0]
      const replacing = existing && existing.menuId !== menu.id

      this.items = [{
        menuId: menu.id,
        name: menu.name,
        price: menu.price,
        imageUrl: menu.imageUrl || '',
        availableDate: menu.availableDate,
        quantity: 1,
      }]

      this.persist()
      return { replacing }
    },
    increment(menuId) {
      this.items = this.items.map((item) => {
        if (item.menuId !== menuId) return item
        return {
          ...item,
          quantity: Math.min(item.quantity + 1, 99),
        }
      })
      this.persist()
    },
    decrement(menuId) {
      this.items = this.items.map((item) => {
        if (item.menuId !== menuId) return item
        return {
          ...item,
          quantity: Math.max(item.quantity - 1, 1),
        }
      })
      this.persist()
    },
    removeItem(menuId) {
      this.items = this.items.filter((item) => item.menuId !== menuId)
      this.persist()
    },
    clear() {
      this.items = []
      this.persist()
    },
  },
})
