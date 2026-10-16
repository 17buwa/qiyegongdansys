import { defineStore } from 'pinia'
import { login as loginApi, getProfile } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null'),
    permissions: []
  }),
  getters: {
    isAdmin: (state) => state.user?.roleCode === 'ADMIN',
    isAgent: (state) => state.user?.roleCode === 'AGENT',
    isCustomer: (state) => state.user?.roleCode === 'CUSTOMER'
  },
  actions: {
    async login(loginForm) {
      const res = await loginApi(loginForm)
      const { token, user } = res.data
      this.token = token
      this.user = user
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(user))
      return user
    },
    hasPerm(permCode) {
      return this.permissions.includes(permCode)
    },
    async loadProfile() {
      try {
        const res = await getProfile()
        this.user = res.data.user
        this.permissions = res.data.permissions || []
        localStorage.setItem('user', JSON.stringify(this.user))
      } catch (e) {
        // 忽略，交由拦截器处理
      }
    },
    logout() {
      this.token = ''
      this.user = null
      this.permissions = []
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})