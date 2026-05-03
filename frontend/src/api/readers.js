import request from '@/utils/request'

export function fetchReaders(params) {
  return request.get('/readers', { params })
}

export function createReader(data) {
  return request.post('/readers', data)
}

export function updateReader(id, data) {
  return request.put(`/readers/${id}`, data)
}

export function deleteReader(id) {
  return request.delete(`/readers/${id}`)
}
