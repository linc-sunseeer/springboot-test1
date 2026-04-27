import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: '',
    userType: '',
    userName: '',
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.token),
  },
  actions: {
    loginAsUser(payload = {}) {
      this.token = payload.accessToken || ''
      this.userType = payload.userType || 'USER'
      this.userName = payload.name || ''
    },
    loginAsAdmin(payload = {}) {
      this.token = payload.accessToken || ''
      this.userType = payload.userType || 'ADMIN'
      this.userName = payload.name || ''
    },
    setUserName(name) {
      this.userName = name || this.userName
    },
    logout() {
      this.token = ''
      this.userType = ''
      this.userName = ''
    },
  },
})