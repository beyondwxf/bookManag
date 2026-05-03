<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const menuItems = computed(() =>
  router.getRoutes()
    .filter((item) => item.meta?.title && item.path.startsWith('/') && !item.meta?.hidden)
    .filter((item) => !item.meta?.roles || item.meta.roles.includes(authStore.roleCode))
    .filter((item) => item.path !== '/login')
)

const groupedMenu = computed(() => {
  const adminPaths = ['/dashboard', '/roles', '/users', '/readers', '/categories', '/books', '/copies', '/borrow', '/return', '/current-loans', '/loan-history', '/overdue', '/reports']
  const readerPaths = ['/catalog', '/current-loans', '/loan-history', '/profile', '/password']
  const targetPaths = authStore.roleCode === 'READER' ? readerPaths : adminPaths.concat(['/profile', '/password'])
  return menuItems.value.filter((item) => targetPaths.includes(item.path))
})

async function handleLogout() {
  await authStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="layout-shell">
    <aside class="layout-aside">
      <div class="layout-brand">
        <h2>图书管理系统</h2>
        <p>{{ authStore.user?.roleName }} · {{ authStore.displayName }}</p>
      </div>
      <el-menu
        :default-active="route.path"
        router
        background-color="transparent"
        text-color="#d7e8fb"
        active-text-color="#ffffff"
      >
        <el-menu-item v-for="item in groupedMenu" :key="item.path" :index="item.path">
          <span>{{ item.meta.title }}</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <main class="layout-main">
      <div class="layout-topbar">
        <div class="layout-title">
          <h3>{{ route.meta.title || '图书管理系统' }}</h3>
          <p>欢迎回来，{{ authStore.displayName || '用户' }}</p>
        </div>
        <div class="toolbar">
          <el-button @click="router.push('/profile')">个人中心</el-button>
          <el-button type="primary" plain @click="router.push('/password')">修改密码</el-button>
          <el-button type="danger" plain @click="handleLogout">退出登录</el-button>
        </div>
      </div>
      <router-view />
    </main>
  </div>
</template>
