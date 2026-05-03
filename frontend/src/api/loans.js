import request from '@/utils/request'

export function borrowBook(data) {
  return request.post('/loans/borrow', data)
}

export function returnBook(data) {
  return request.post('/loans/return', data)
}

export function fetchCurrentLoans(params) {
  return request.get('/loans/current', { params })
}

export function fetchLoanHistory(params) {
  return request.get('/loans/history', { params })
}

export function fetchOverdueLoans(params) {
  return request.get('/loans/overdue', { params })
}
