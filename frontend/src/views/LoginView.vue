<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from '@/utils/element'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const formRef = ref()
const form = reactive({
  username: 'admin',
  password: 'Admin@123'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    await authStore.login(form)
    ElMessage.success('登录成功')
    router.push(authStore.roleCode === 'READER' ? '/catalog' : '/dashboard')
  } catch (error) {
    // The request layer already surfaces a user-facing message.
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-shell">
    <section class="login-aside">
      <div>
        <h1 style="margin: 0; font-size: 44px">图书管理系统</h1>
        <p style="max-width: 460px; margin-top: 18px; line-height: 1.7; font-size: 16px">
          面向管理员、图书管理员和读者的一体化借阅平台，覆盖图书目录、实体副本、借还流转、逾期管理与基础统计。
        </p>
      </div>
      <div>
        <p style="margin: 0; opacity: 0.8">演示账号</p>
        <p style="margin: 8px 0 0">管理员: admin / Admin@123</p>
        <p style="margin: 8px 0 0">图书管理员: librarian / Admin@123</p>
        <p style="margin: 8px 0 0">读者: reader01 / Admin@123</p>
      </div>
    </section>

    <section class="login-panel">
      <div class="login-card">
        <div class="panel-header">
          <div>
            <h2 style="margin: 0">欢迎登录</h2>
            <p style="margin: 8px 0 0; color: var(--muted)">请输入账号密码进入系统</p>
          </div>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" @keyup.enter="submit" />
          </el-form-item>
          <el-button type="primary" size="large" :loading="loading" style="width: 100%" @click="submit">
            登录系统
          </el-button>
        </el-form>
      </div>
    </section>
  </div>
</template>
