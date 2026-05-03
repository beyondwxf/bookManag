<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from '@/utils/element'
import { createCopy, deleteCopy, fetchBooks, fetchCopies, updateCopy } from '@/api/books'

const loading = ref(false)
const books = ref([])
const copies = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const pagination = reactive({ current: 1, size: 10, total: 0 })
const filters = reactive({ keyword: '', bookId: '', status: '' })
const form = reactive({ bookId: '', barcode: '', locationCode: '', status: 'AVAILABLE' })

async function loadBooks() {
  const data = await fetchBooks({ current: 1, size: 200 })
  books.value = data.records
}

async function loadCopies() {
  loading.value = true
  try {
    const data = await fetchCopies({
      current: pagination.current,
      size: pagination.size,
      keyword: filters.keyword || undefined,
      bookId: filters.bookId || undefined,
      status: filters.status || undefined
    })
    copies.value = data.records
    pagination.total = data.total
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  Object.assign(form, { bookId: '', barcode: '', locationCode: '', status: 'AVAILABLE' })
  dialogVisible.value = true
}

function openEdit(row) {
  editingId.value = row.id
  Object.assign(form, { bookId: row.bookId, barcode: row.barcode, locationCode: row.locationCode, status: row.status })
  dialogVisible.value = true
}

async function submit() {
  if (editingId.value) {
    await updateCopy(editingId.value, form)
    ElMessage.success('副本更新成功')
  } else {
    await createCopy(form)
    ElMessage.success('副本创建成功')
  }
  dialogVisible.value = false
  await loadCopies()
}

async function remove(row) {
  await ElMessageBox.confirm(`确认删除副本 ${row.barcode} 吗？`, '提示', { type: 'warning' })
  await deleteCopy(row.id)
  ElMessage.success('删除成功')
  await loadCopies()
}

onMounted(async () => {
  await loadBooks()
  await loadCopies()
})
</script>

<template>
  <div class="page-shell">
    <div class="panel" style="padding: 20px">
      <div class="panel-header">
        <div>
          <h3 style="margin: 0">图书副本管理</h3>
          <p style="margin: 6px 0 0; color: var(--muted)">维护馆藏条码、位置和流转状态</p>
        </div>
        <el-button type="primary" @click="openCreate">新增副本</el-button>
      </div>
      <div class="toolbar" style="margin-bottom: 16px">
        <el-input v-model="filters.keyword" placeholder="条码" clearable style="width: 220px" />
        <el-select v-model="filters.bookId" placeholder="图书" clearable style="width: 220px">
          <el-option v-for="book in books" :key="book.id" :label="book.title" :value="book.id" />
        </el-select>
        <el-select v-model="filters.status" placeholder="状态" clearable style="width: 160px">
          <el-option label="AVAILABLE" value="AVAILABLE" />
          <el-option label="BORROWED" value="BORROWED" />
          <el-option label="LOST" value="LOST" />
          <el-option label="REPAIRING" value="REPAIRING" />
        </el-select>
        <el-button type="primary" plain @click="loadCopies">查询</el-button>
      </div>

      <el-table :data="copies" v-loading="loading">
        <el-table-column prop="barcode" label="副本条码" min-width="150" />
        <el-table-column prop="bookTitle" label="图书" min-width="180" />
        <el-table-column prop="locationCode" label="馆藏位置" min-width="120" />
        <el-table-column prop="status" label="状态" min-width="100" />
        <el-table-column prop="version" label="版本" width="90" />
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
          @current-change="loadCopies"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑副本' : '新增副本'" width="560px">
      <el-form :model="form" label-position="top">
        <div class="form-grid">
          <el-form-item label="图书">
            <el-select v-model="form.bookId">
              <el-option v-for="book in books" :key="book.id" :label="book.title" :value="book.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="副本条码"><el-input v-model="form.barcode" /></el-form-item>
          <el-form-item label="馆藏位置"><el-input v-model="form.locationCode" /></el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status">
              <el-option label="AVAILABLE" value="AVAILABLE" />
              <el-option label="BORROWED" value="BORROWED" />
              <el-option label="LOST" value="LOST" />
              <el-option label="REPAIRING" value="REPAIRING" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
