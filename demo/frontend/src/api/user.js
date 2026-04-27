import { apiRequest } from './http'

export function loginUser(payload) {
  return apiRequest('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function registerUser(payload) {
  return apiRequest('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function verifyEmail(token) {
  return apiRequest(`/api/auth/verify-email?token=${encodeURIComponent(token)}`)
}

export function loginAdmin(payload) {
  return apiRequest('/api/auth/admin/login', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function requestPasswordReset(payload) {
  return apiRequest('/api/auth/password/reset-request', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function resetPassword(payload) {
  return apiRequest('/api/auth/password/reset', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function fetchTodayMenus() {
  return apiRequest('/api/user/menus/today')
}

export function fetchHomeMenus() {
  return apiRequest('/api/user/menus/home')
}

export function fetchMenuById(menuId) {
  return apiRequest(`/api/user/menus/${menuId}`)
}

export function createReservation(payload) {
  return apiRequest('/api/user/reservations', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function checkoutCart(payload) {
  return apiRequest('/api/user/reservations/cart-checkout', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function fetchTodayReservations() {
  return apiRequest('/api/user/reservations/today')
}

export function fetchUserReservations() {
  return apiRequest('/api/user/reservations')
}

export function fetchUserProfile() {
  return apiRequest('/api/user/profile?_t=' + Date.now())
}

export function updateUserName(payload) {
  return apiRequest('/api/user/profile/name', {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function updateUserPassword(payload) {
  return apiRequest('/api/user/profile/password', {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function requestUserPasswordReset() {
  return apiRequest('/api/user/profile/password/reset-request', {
    method: 'POST',
  })
}

export function markReservationPaid(reservationId, paymentStatus = 'PAID') {
  return apiRequest(`/api/user/reservations/${reservationId}/payment`, {
    method: 'PATCH',
    body: JSON.stringify({ paymentStatus }),
  })
}

export function updateReservationStatus(reservationId, orderStatus) {
  return apiRequest(`/api/user/reservations/${reservationId}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ orderStatus }),
  })
}

export function updateUserDietPreferences(payload) {
  return apiRequest('/api/user/profile/diet-preferences', {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}
