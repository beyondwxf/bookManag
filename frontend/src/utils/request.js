import { ElMessage } from '@/utils/element'

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'
const hasAbsoluteBase = /^https?:\/\//i.test(baseURL)

function joinUrl(url, params) {
  const normalizedBase = baseURL.endsWith('/') ? baseURL : `${baseURL}/`
  const normalizedPath = url.startsWith('/') ? url.slice(1) : url
  const originBase = hasAbsoluteBase ? normalizedBase : `${window.location.origin}${normalizedBase}`
  const target = new URL(normalizedPath, originBase)
  if (params) {
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        target.searchParams.set(key, value)
      }
    })
  }
  return hasAbsoluteBase ? target.toString() : target.pathname + target.search
}

async function request(url, options = {}) {
  const token = localStorage.getItem('library-token')
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {})
  }
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  const response = await fetch(joinUrl(url, options.params), {
    method: options.method || 'GET',
    headers,
    body: options.body ? JSON.stringify(options.body) : undefined
  })

  if (response.status === 401) {
    localStorage.removeItem('library-token')
    localStorage.removeItem('library-user')
    window.location.href = '/login'
    throw new Error('Unauthorized')
  }

  const payload = await response.json()
  if (!payload.success) {
    ElMessage.error(payload.message || 'Request failed')
    throw new Error(payload.message || 'Request failed')
  }
  return payload.data
}

export default {
  get(url, config = {}) {
    return request(url, { ...config, method: 'GET' })
  },
  post(url, body, config = {}) {
    return request(url, { ...config, method: 'POST', body })
  },
  put(url, body, config = {}) {
    return request(url, { ...config, method: 'PUT', body })
  },
  delete(url, config = {}) {
    return request(url, { ...config, method: 'DELETE' })
  }
}
