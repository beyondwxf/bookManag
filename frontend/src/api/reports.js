import request from '@/utils/request'

export function fetchDashboard() {
  return request.get('/reports/dashboard')
}

export function fetchLoanReport() {
  return request.get('/reports/loans')
}
