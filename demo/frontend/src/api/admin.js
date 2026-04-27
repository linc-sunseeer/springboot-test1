import { apiRequest } from './http'

export function fetchAdminDashboard() {
  return apiRequest('/api/admin/dashboard')
}

export function fetchAdminMenus() {
  return apiRequest('/api/admin/menus')
}

export function createAdminMenu(payload) {
  return apiRequest('/api/admin/menus', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function uploadAdminMenuImage(file) {
  const formData = new FormData()
  formData.append('file', file)

  return apiRequest('/api/admin/menus/upload', {
    method: 'POST',
    body: formData,
  })
}

export function updateAdminMenu(menuId, payload) {
  return apiRequest(`/api/admin/menus/${menuId}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function deleteAdminMenu(menuId) {
  return apiRequest(`/api/admin/menus/${menuId}`, {
    method: 'DELETE',
  })
}

export function fetchAdminMenuReservationSummary(menuId) {
  return apiRequest(`/api/admin/menus/${menuId}/reservation-summary`)
}

export function fetchDailyAggregate(date) {
  return apiRequest(`/api/admin/aggregates/daily?date=${date}`)
}

export function fetchMonthlyAggregate(yearMonth) {
  return apiRequest(`/api/admin/aggregates/monthly?yearMonth=${yearMonth}`)
}

export function exportDailyAggregateCsv(date) {
  return apiRequest(`/api/admin/aggregates/daily/export?date=${date}`, {
    responseType: 'arrayBuffer',
  })
}

export function fetchDailyReservationUsers(date) {
  return apiRequest(`/api/admin/reservations/daily-users?date=${date}`)
}

export function fetchReservationCutoffTime() {
  return apiRequest('/api/admin/settings/reservation-time')
}

export function updateReservationCutoffTime(time) {
  return apiRequest('/api/admin/settings/reservation-time', {
    method: 'PUT',
    body: JSON.stringify({ time }),
  })
}

export function fetchMinReservationThreshold() {
  return apiRequest('/api/admin/settings/min-threshold')
}

export function updateMinReservationThreshold(value) {
  return apiRequest('/api/admin/settings/min-threshold', {
    method: 'PUT',
    body: JSON.stringify({ value }),
  })
}

export function fetchAdminUsers() {
  return apiRequest('/api/admin/users')
}

export function fetchAdminUserReservations(userId) {
  return apiRequest(`/api/admin/users/${userId}/reservations`)
}

export function updateAdminReservationStatus(reservationId, orderStatus) {
  return apiRequest(`/api/admin/reservations/${reservationId}/status`, {
    method: 'PUT',
    body: JSON.stringify({ orderStatus }),
  })
}

export function approveAdminReservationPayment(reservationId) {
  return apiRequest(`/api/admin/reservations/${reservationId}/payment`, {
    method: 'PATCH',
  })
}

export function confirmAllReservations(date) {
  return apiRequest(`/api/admin/reservations/confirm-all?date=${date}`, {
    method: 'POST',
  })
}

export function cancelAllReservations(date) {
  return apiRequest(`/api/admin/reservations/cancel-all?date=${date}`, {
    method: 'POST',
  })
}

export function refundReservation(reservationId, reason) {
  return apiRequest(`/api/admin/reservations/${reservationId}/refund`, {
    method: 'PATCH',
    body: JSON.stringify({ refundReason: reason }),
  })
}

export function deleteAdminUser(userId, adminPassword) {
  return apiRequest(`/api/admin/users/${userId}`, {
    method: 'DELETE',
    body: JSON.stringify({ adminPassword }),
  })
}

export function exportMonthlyAggregateCsv(yearMonth) {
  return apiRequest(`/api/admin/aggregates/monthly/export?yearMonth=${yearMonth}`, {
    responseType: 'arrayBuffer',
  })
}
