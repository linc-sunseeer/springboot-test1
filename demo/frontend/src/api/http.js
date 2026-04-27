import { useAuthStore } from '../stores/auth'

const API_BASE_URL = ''

function isStaleAuthError(path, message, status) {
  if (path.startsWith('/api/auth/')) {
    return false
  }

  if (status === 401 || status === 403) {
    return true
  }

  return message === 'ユーザーが存在しません' || message === 'ログイン情報を確認できません。再度ログインしてください。'
}

function redirectToLogin(authStore) {
  const nextRoute = authStore.userType === 'ADMIN' ? '/admin/login' : '/login'
  authStore.logout()
  window.location.href = nextRoute
}

export async function apiRequest(path, options = {}) {
  const authStore = useAuthStore()
  const headers = new Headers(options.headers || {})
  const responseType = options.responseType || 'json'

  if (!headers.has('Content-Type') && options.body && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json')
  }

  if (authStore.token) {
    headers.set('Authorization', `Bearer ${authStore.token}`)
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
  })

  if (!response.ok) {
    const contentType = response.headers.get('content-type') || ''

    if (contentType.includes('application/json')) {
      const errorPayload = await response.json()
      const message = errorPayload.message || `Request failed: ${response.status}`

      if (isStaleAuthError(path, message, response.status)) {
        redirectToLogin(authStore)
      }

      throw new Error(message)
    }

    const message = await response.text()

    if (isStaleAuthError(path, message, response.status)) {
      redirectToLogin(authStore)
    }

    throw new Error(message || `Request failed: ${response.status}`)
  }

  if (responseType === 'arrayBuffer') {
    return response.arrayBuffer()
  }

  const contentType = response.headers.get('content-type') || ''
  if (contentType.includes('text/csv')) {
    return response.text()
  }

  return response.json()
}
