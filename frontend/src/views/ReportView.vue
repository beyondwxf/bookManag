<script setup>
import { onMounted, ref } from 'vue'
import { fetchDashboard, fetchLoanReport } from '@/api/reports'

const loading = ref(false)
const dashboard = ref({})
const trend = ref([])

async function load() {
  loading.value = true
  try {
    const [dashboardData, trendData] = await Promise.all([fetchDashboard(), fetchLoanReport()])
    dashboard.value = dashboardData
    trend.value = trendData
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page-shell" v-loading="loading">
    <div class="stat-grid">
      <div class="stat-card">
        <h4>图书总数</h4>
        <strong>{{ dashboard.totalBooks || 0 }}</strong>
      </div>
      <div class="stat-card">
        <h4>可借副本</h4>
        <strong>{{ dashboard.availableCopies || 0 }}</strong>
      </div>
      <div class="stat-card">
        <h4>借出副本</h4>
        <strong>{{ dashboard.borrowedCopies || 0 }}</strong>
      </div>
      <div class="stat-card">
        <h4>逾期数量</h4>
        <strong>{{ dashboard.overdueCount || 0 }}</strong>
      </div>
    </div>

    <div class="panel" style="padding: 20px">
      <div class="panel-header">
        <div>
          <h3 style="margin: 0">借阅趋势</h3>
          <p style="margin: 6px 0 0; color: var(--muted)">最近 14 个借阅日统计</p>
        </div>
      </div>
      <el-table :data="trend">
        <el-table-column prop="borrowDate" label="日期" min-width="180" />
        <el-table-column prop="borrowCount" label="借阅次数" min-width="120" />
      </el-table>
    </div>
  </div>
</template>
