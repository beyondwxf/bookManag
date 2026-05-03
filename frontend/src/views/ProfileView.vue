<script setup>
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const profile = computed(() => authStore.user || {})
</script>

<template>
  <div class="page-shell">
    <div class="panel" style="padding: 24px">
      <div class="panel-header">
        <div>
          <h3 style="margin: 0">个人中心</h3>
          <p style="margin: 6px 0 0; color: var(--muted)">查看当前登录账号的基础信息</p>
        </div>
      </div>
      <div class="detail-grid">
        <div class="detail-item">
          <span>用户 ID</span>
          <strong>{{ profile.id }}</strong>
        </div>
        <div class="detail-item">
          <span>用户名</span>
          <strong>{{ profile.username }}</strong>
        </div>
        <div class="detail-item">
          <span>姓名</span>
          <strong>{{ profile.displayName }}</strong>
        </div>
        <div class="detail-item">
          <span>角色</span>
          <strong>{{ profile.roleName }}</strong>
        </div>
        <div class="detail-item" v-if="profile.readerId">
          <span>读者档案 ID</span>
          <strong>{{ profile.readerId }}</strong>
        </div>
      </div>
    </div>
  </div>
</template>
