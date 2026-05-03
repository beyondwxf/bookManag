import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { public: true, title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'dashboard', component: () => import('@/views/DashboardView.vue'), meta: { title: '仪表盘', roles: ['ADMIN', 'LIBRARIAN'] } },
      { path: 'roles', name: 'roles', component: () => import('@/views/RoleManagementView.vue'), meta: { title: '角色管理', roles: ['ADMIN'] } },
      { path: 'users', name: 'users', component: () => import('@/views/UserManagementView.vue'), meta: { title: '用户管理', roles: ['ADMIN'] } },
      { path: 'readers', name: 'readers', component: () => import('@/views/ReaderManagementView.vue'), meta: { title: '读者管理', roles: ['ADMIN', 'LIBRARIAN'] } },
      { path: 'categories', name: 'categories', component: () => import('@/views/CategoryManagementView.vue'), meta: { title: '图书分类', roles: ['ADMIN'] } },
      { path: 'books', name: 'books', component: () => import('@/views/BookManagementView.vue'), meta: { title: '图书管理', roles: ['ADMIN'] } },
      { path: 'copies', name: 'copies', component: () => import('@/views/BookCopyManagementView.vue'), meta: { title: '图书副本', roles: ['ADMIN', 'LIBRARIAN'] } },
      { path: 'borrow', name: 'borrow', component: () => import('@/views/BorrowView.vue'), meta: { title: '借阅登记', roles: ['ADMIN', 'LIBRARIAN'] } },
      { path: 'return', name: 'return', component: () => import('@/views/ReturnView.vue'), meta: { title: '归还登记', roles: ['ADMIN', 'LIBRARIAN'] } },
      { path: 'current-loans', name: 'current-loans', component: () => import('@/views/LoanCurrentView.vue'), meta: { title: '当前借阅', roles: ['ADMIN', 'LIBRARIAN', 'READER'] } },
      { path: 'loan-history', name: 'loan-history', component: () => import('@/views/LoanHistoryView.vue'), meta: { title: '借阅历史', roles: ['ADMIN', 'LIBRARIAN', 'READER'] } },
      { path: 'overdue', name: 'overdue', component: () => import('@/views/OverdueView.vue'), meta: { title: '逾期管理', roles: ['ADMIN', 'LIBRARIAN'] } },
      { path: 'reports', name: 'reports', component: () => import('@/views/ReportView.vue'), meta: { title: '统计报表', roles: ['ADMIN', 'LIBRARIAN'] } },
      { path: 'catalog', name: 'catalog', component: () => import('@/views/BookSearchView.vue'), meta: { title: '图书检索', roles: ['READER'] } },
      { path: 'catalog/:id', name: 'catalog-detail', component: () => import('@/views/BookDetailView.vue'), meta: { title: '图书详情', roles: ['READER'], hidden: true } },
      { path: 'profile', name: 'profile', component: () => import('@/views/ProfileView.vue'), meta: { title: '个人中心', roles: ['ADMIN', 'LIBRARIAN', 'READER'] } },
      { path: 'password', name: 'password', component: () => import('@/views/PasswordView.vue'), meta: { title: '修改密码', roles: ['ADMIN', 'LIBRARIAN', 'READER'] } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()

  if (to.meta.public) {
    if (authStore.isLoggedIn && to.path === '/login') {
      return authStore.roleCode === 'READER' ? '/catalog' : '/dashboard'
    }
    return true
  }

  if (!authStore.isLoggedIn) {
    return '/login'
  }

  if (!authStore.user) {
    try {
      await authStore.fetchProfile()
    } catch (error) {
      authStore.clear()
      return '/login'
    }
  }

  if (to.meta.roles && !to.meta.roles.includes(authStore.roleCode)) {
    return authStore.roleCode === 'READER' ? '/catalog' : '/dashboard'
  }

  return true
})

export default router
