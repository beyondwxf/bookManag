<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from '@/utils/element'
import { fetchCurrentLoans, returnBook } from '@/api/loans'

const loading = ref(false)
const loans = ref([])

async function loadLoans() {
  loading.value = true
  try {
    const data = await fetchCurrentLoans({ current: 1, size: 100 })
    loans.value = data.records
  } finally {
    loading.value = false
  }
}

async function handleReturn(row) {
  await returnBook({ loanId: row.id })
  ElMessage.success('归还成功')
  await loadLoans()
}

onMounted(loadLoans)
</script>

<template>
  <div class="page-shell">
    <div class="panel" style="padding: 20px">
      <div class="panel-header">
        <div>
          <h3 style="margin: 0">归还登记</h3>
          <p style="margin: 6px 0 0; color: var(--muted)">从当前借阅列表中办理归还</p>
        </div>
      </div>
      <el-table :data="loans" v-loading="loading">
        <el-table-column prop="loanNo" label="借阅单号" min-width="160" />
        <el-table-column prop="readerName" label="读者" min-width="120" />
        <el-table-column prop="bookTitle" label="图书" min-width="180" />
        <el-table-column prop="barcode" label="条码" min-width="140" />
        <el-table-column prop="dueDate" label="应还日期" min-width="120" />
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'OVERDUE' ? 'danger' : 'primary'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleReturn(row)">归还</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
