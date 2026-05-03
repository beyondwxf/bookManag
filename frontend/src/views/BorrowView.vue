<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from '@/utils/element'
import { fetchCopies } from '@/api/books'
import { borrowBook } from '@/api/loans'
import { fetchReaders } from '@/api/readers'

const readers = ref([])
const copies = ref([])
const loading = ref(false)
const form = reactive({
  readerId: '',
  bookCopyId: '',
  dueDays: 30
})

async function loadOptions() {
  const [readerPage, copyPage] = await Promise.all([
    fetchReaders({ current: 1, size: 200, status: 'ACTIVE' }),
    fetchCopies({ current: 1, size: 500, status: 'AVAILABLE' })
  ])
  readers.value = readerPage.records
  copies.value = copyPage.records
}

async function submit() {
  loading.value = true
  try {
    await borrowBook(form)
    ElMessage.success('借阅登记成功')
    Object.assign(form, { readerId: '', bookCopyId: '', dueDays: 30 })
    await loadOptions()
  } finally {
    loading.value = false
  }
}

onMounted(loadOptions)
</script>

<template>
  <div class="page-shell">
    <div class="panel" style="padding: 24px; max-width: 760px">
      <div class="panel-header">
        <div>
          <h3 style="margin: 0">借阅登记</h3>
          <p style="margin: 6px 0 0; color: var(--muted)">为读者办理图书借出，默认借期 30 天</p>
        </div>
      </div>
      <el-form :model="form" label-position="top">
        <div class="form-grid">
          <el-form-item label="选择读者">
            <el-select v-model="form.readerId" filterable placeholder="请选择读者">
              <el-option v-for="reader in readers" :key="reader.id" :label="`${reader.name} (${reader.readerNo})`" :value="reader.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="选择副本">
            <el-select v-model="form.bookCopyId" filterable placeholder="请选择可借副本">
              <el-option
                v-for="copy in copies"
                :key="copy.id"
                :label="`${copy.bookTitle} / ${copy.barcode}`"
                :value="copy.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="借阅天数">
            <el-input-number v-model="form.dueDays" :min="1" :max="90" style="width: 100%" />
          </el-form-item>
        </div>
      </el-form>
      <el-button type="primary" :loading="loading" @click="submit">提交借阅</el-button>
    </div>
  </div>
</template>
