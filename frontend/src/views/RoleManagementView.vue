<script setup>
import { onMounted, ref } from 'vue'
import { fetchRoles } from '@/api/users'

const loading = ref(false)
const roles = ref([])

async function load() {
  loading.value = true
  try {
    roles.value = await fetchRoles()
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page-shell">
    <div class="panel" style="padding: 20px" v-loading="loading">
      <div class="panel-header">
        <div>
          <h3 style="margin: 0">角色管理</h3>
          <p style="margin: 6px 0 0; color: var(--muted)">当前系统内置角色清单</p>
        </div>
      </div>
      <el-table :data="roles">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="角色名称" min-width="160" />
        <el-table-column prop="code" label="角色编码" min-width="140" />
        <el-table-column prop="description" label="说明" min-width="220" />
      </el-table>
    </div>
  </div>
</template>
