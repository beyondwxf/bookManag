import request from '@/utils/request'

export function fetchCategories() {
  return request.get('/categories')
}

export function createCategory(data) {
  return request.post('/categories', data)
}

export function updateCategory(id, data) {
  return request.put(`/categories/${id}`, data)
}

export function deleteCategory(id) {
  return request.delete(`/categories/${id}`)
}

export function fetchBooks(params) {
  return request.get('/books', { params })
}

export function fetchBookDetail(id) {
  return request.get(`/books/${id}`)
}

export function createBook(data) {
  return request.post('/books', data)
}

export function updateBook(id, data) {
  return request.put(`/books/${id}`, data)
}

export function deleteBook(id) {
  return request.delete(`/books/${id}`)
}

export function fetchCopies(params) {
  return request.get('/book-copies', { params })
}

export function createCopy(data) {
  return request.post('/book-copies', data)
}

export function updateCopy(id, data) {
  return request.put(`/book-copies/${id}`, data)
}

export function deleteCopy(id) {
  return request.delete(`/book-copies/${id}`)
}
