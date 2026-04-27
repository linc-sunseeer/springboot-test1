import { createRouter, createWebHistory } from 'vue-router'

import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import ForgotPasswordView from '../views/ForgotPasswordView.vue'
import ResetPasswordView from '../views/ResetPasswordView.vue'
import VerifyEmailView from '../views/VerifyEmailView.vue'
import TodayMenusView from '../views/TodayMenusView.vue'
import MenuDetailView from '../views/MenuDetailView.vue'
import MyPageView from '../views/MyPageView.vue'
import CartView from '../views/CartView.vue'
import AdminLoginView from '../views/admin/AdminLoginView.vue'
import AdminDashboardView from '../views/admin/AdminDashboardView.vue'
import AdminMenusView from '../views/admin/AdminMenusView.vue'
import AdminDailyView from '../views/admin/AdminDailyView.vue'
import AdminMonthlyView from '../views/admin/AdminMonthlyView.vue'
import AdminSettingsView from '../views/admin/AdminSettingsView.vue'
import AdminUsersView from '../views/admin/AdminUsersView.vue'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/', redirect: '/menus/today' },
  { path: '/admin', component: AdminDashboardView, meta: { requiresAuth: true, role: 'ADMIN' } },
  { path: '/login', component: LoginView },
  { path: '/register', component: RegisterView },
  { path: '/forgot-password', component: ForgotPasswordView },
  { path: '/reset-password', component: ResetPasswordView },
  { path: '/verify-email', component: VerifyEmailView },
  { path: '/menus/today', component: TodayMenusView, meta: { requiresAuth: true, role: 'USER' } },
  { path: '/menus/:menuId', component: MenuDetailView, meta: { requiresAuth: true, role: 'USER' } },
  { path: '/cart', component: CartView, meta: { requiresAuth: true, role: 'USER' } },
  { path: '/mypage', component: MyPageView, meta: { requiresAuth: true, role: 'USER' } },
  { path: '/admin/login', component: AdminLoginView },
  { path: '/admin/menus', component: AdminMenusView, meta: { requiresAuth: true, role: 'ADMIN' } },
  { path: '/admin/aggregates/daily', component: AdminDailyView, meta: { requiresAuth: true, role: 'ADMIN' } },
  { path: '/admin/aggregates/monthly', component: AdminMonthlyView, meta: { requiresAuth: true, role: 'ADMIN' } },
  { path: '/admin/settings', component: AdminSettingsView, meta: { requiresAuth: true, role: 'ADMIN' } },
  { path: '/admin/users', component: AdminUsersView, meta: { requiresAuth: true, role: 'ADMIN' } },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  const authStore = useAuthStore()

  if (!to.meta.requiresAuth) {
    return true
  }

  if (!authStore.isAuthenticated) {
    return to.meta.role === 'ADMIN' ? '/admin/login' : '/login'
  }

  if (to.meta.role && authStore.userType !== to.meta.role) {
    return authStore.userType === 'ADMIN' ? '/admin' : '/menus/today'
  }

  return true
})

export default router
