import { defineStore } from 'pinia'
import { getProfile, login as loginApi, logout as logoutApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('library-token') || '',
    user: JSON.parse(localStorage.getItem('library-user') || 'null')
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token),
    roleCode: (state) => state.user?.roleCode || '',
    displayName: (state) => state.user?.displayName || ''
  },
  actions: {
    async login(payload) {
      const response = await loginApi(payload)
      this.token = response.token
      this.user = response.user
      localStorage.setItem('library-token', response.token)
      localStorage.setItem('library-user', JSON.stringify(response.user))
    },
    async fetchProfile() {
      if (!this.token) return null
      const profile = await getProfile()
      this.user = profile
      localStorage.setItem('library-user', JSON.stringify(profile))
      return profile
    },
    async logout() {
      if (this.token) {
        try {
          await logoutApi()
        } catch (error) {
          // Ignore logout transport failures and clear local session anyway.
        }
      }
      this.clear()
    },
    clear() {
      this.token = ''
      this.user = null
      localStorage.removeItem('library-token')
      localStorage.removeItem('library-user')
    }
  }
})
