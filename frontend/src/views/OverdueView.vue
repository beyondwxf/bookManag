<script setup>
import { onMounted, reactive, ref } from 'vue'
import { fetchOverdueLoans } from '@/api/loans'

const loading = ref(false)
const records = ref([])
const pagination = reactive({ current: 1, size: 10, total: 0 })

async function load() {
  loading.value = true
  try {
    const data = await fetchOverdueLoans({ current: pagination.current, size: pagination.size })
    records.value = data.records
    pagination.total = data.total
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page-shell">
    <div class="panel" style="padding: 20px">
      <div class="panel-header">
        <div>
          <h3 style="margin: 0">逾期管理</h3>
          <p style="margin: 6px 0 0; color: var(--muted)">查看所有逾期未归还记录</p>
        </div>
      </div>
      <el-table :data="records" v-loading="loading">
        <el-table-column prop="loanNo" label="借阅单号" min-width="160" />
        <el-table-column prop="readerName" label="读者" min-width="120" />
        <el-table-column prop="bookTitle" label="图书" min-width="180" />
        <el-table-column prop="barcode" label="副本条码" min-width="140" />
        <el-table-column prop="dueDate" label="应还日期" min-width="120" />
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag type="danger">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div style="display: flex; justify-content: flex-end; margin-top: 16px">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          layout="total, prev, pager, next"
          :total="pagination.total"
          @current-change="load"
        />
      </div>
    </div>
  </div>
</template>
