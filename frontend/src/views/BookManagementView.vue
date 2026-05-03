<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from '@/utils/element'
import { createBook, deleteBook, fetchBooks, fetchCategories, updateBook } from '@/api/books'

const loading = ref(false)
const dialogVisible = ref(false)
const editingId = ref(null)
const books = ref([])
const categories = ref([])
const pagination = reactive({ current: 1, size: 10, total: 0 })
const filters = reactive({ keyword: '', categoryId: '' })
const form = reactive({
  categoryId: '',
  isbn: '',
  title: '',
  author: '',
  publisher: '',
  publishDate: '',
  description: ''
})

async function loadCategories() {
  categories.value = await fetchCategories()
}

async function loadBooks() {
  loading.value = true
  try {
    const data = await fetchBooks({
      current: pagination.current,
      size: pagination.size,
      keyword: filters.keyword || undefined,
      categoryId: filters.categoryId || undefined
    })
    books.value = data.records
    pagination.total = data.total
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  Object.assign(form, { categoryId: '', isbn: '', title: '', author: '', publisher: '', publishDate: '', description: '' })
  dialogVisible.value = true
}

function openEdit(row) {
  editingId.value = row.id
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

async function submit() {
  if (editingId.value) {
    await updateBook(editingId.value, form)
    ElMessage.success('图书更新成功')
  } else {
    await createBook(form)
    ElMessage.success('图书创建成功')
  }
  dialogVisible.value = false
  await loadBooks()
}

async function remove(row) {
  await ElMessageBox.confirm(`确认删除图书《${row.title}》吗？`, '提示', { type: 'warning' })
  await deleteBook(row.id)
  ElMessage.success('删除成功')
  await loadBooks()
}

onMounted(async () => {
  await loadCategories()
  await loadBooks()
})
</script>

<template>
  <div class="page-shell">
    <div class="panel" style="padding: 20px">
      <div class="panel-header">
        <div>
          <h3 style="margin: 0">图书管理</h3>
          <p style="margin: 6px 0 0; color: var(--muted)">维护图书主档、ISBN 与目录信息</p>
        </div>
        <el-button type="primary" @click="openCreate">新增图书</el-button>
      </div>
      <div class="toolbar" style="margin-bottom: 16px">
        <el-input v-model="filters.keyword" placeholder="书名/作者/ISBN" clearable style="width: 240px" />
        <el-select v-model="filters.categoryId" placeholder="分类" clearable style="width: 200px">
          <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-button type="primary" plain @click="loadBooks">查询</el-button>
      </div>

      <el-table :data="books" v-loading="loading">
        <el-table-column prop="title" label="书名" min-width="180" />
        <el-table-column prop="author" label="作者" min-width="120" />
        <el-table-column prop="isbn" label="ISBN" min-width="150" />
        <el-table-column prop="categoryName" label="分类" min-width="140" />
        <el-table-column prop="totalCopies" label="副本数" width="100" />
        <el-table-column prop="availableCopies" label="可借" width="100" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="display: flex; justify-content: flex-end; margin-top: 16px">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          layout="total, prev, pager, next"
          :total="pagination.total"
          @current-change="loadBooks"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑图书' : '新增图书'" width="660px">
      <el-form :model="form" label-position="top">
        <div class="form-grid">
          <el-form-item label="分类">
            <el-select v-model="form.categoryId">
              <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="ISBN"><el-input v-model="form.isbn" /></el-form-item>
          <el-form-item label="书名"><el-input v-model="form.title" /></el-form-item>
          <el-form-item label="作者"><el-input v-model="form.author" /></el-form-item>
          <el-form-item label="出版社"><el-input v-model="form.publisher" /></el-form-item>
          <el-form-item label="出版日期"><el-date-picker v-model="form.publishDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
          <el-form-item label="简介" style="grid-column: 1 / -1"><el-input v-model="form.description" type="textarea" :rows="4" /></el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
