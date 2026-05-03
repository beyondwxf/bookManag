<script setup>
import { onMounted, ref } from 'vue'
import { fetchDashboard } from '@/api/reports'
import { fetchCurrentLoans } from '@/api/loans'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const dashboard = ref({
  totalBooks: 0,
  totalCopies: 0,
  availableCopies: 0,
  borrowedCopies: 0,
  overdueCount: 0,
  totalReaders: 0
})
const currentLoans = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    if (authStore.roleCode !== 'READER') {
      dashboard.value = await fetchDashboard()
    }
    const loanPage = await fetchCurrentLoans({ current: 1, size: 6 })
    currentLoans.value = loanPage.records
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page-shell" v-loading="loading">
    <div v-if="authStore.roleCode !== 'READER'" class="stat-grid">
      <div class="stat-card">
        <h4>图书总数</h4>
        <strong>{{ dashboard.totalBooks }}</strong>
      </div>
      <div class="stat-card">
        <h4>馆藏副本</h4>
        <strong>{{ dashboard.totalCopies }}</strong>
      </div>
      <div class="stat-card">
        <h4>可借副本</h4>
        <strong>{{ dashboard.availableCopies }}</strong>
      </div>
      <div class="stat-card">
        <h4>借出副本</h4>
        <strong>{{ dashboard.borrowedCopies }}</strong>
      </div>
      <div class="stat-card">
        <h4>逾期数量</h4>
        <strong>{{ dashboard.overdueCount }}</strong>
      </div>
      <div class="stat-card">
        <h4>读者总数</h4>
        <strong>{{ dashboard.totalReaders }}</strong>
      </div>
    </div>

    <div class="panel" style="padding: 20px">
      <div class="panel-header">
        <div>
          <h3 style="margin: 0">当前借阅概览</h3>
          <p style="margin: 6px 0 0; color: var(--muted)">展示最近的在借记录</p>
        </div>
      </div>
      <el-table :data="currentLoans">
        <el-table-column prop="loanNo" label="借阅单号" min-width="160" />
        <el-table-column prop="readerName" label="读者" min-width="120" />
        <el-table-column prop="bookTitle" label="图书" min-width="180" />
        <el-table-column prop="barcode" label="副本条码" min-width="140" />
        <el-table-column prop="dueDate" label="应还日期" min-width="120" />
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'OVERDUE' ? 'danger' : 'primary'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
