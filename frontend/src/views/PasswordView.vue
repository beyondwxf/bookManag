<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from '@/utils/element'
import { changePassword } from '@/api/auth'

const formRef = ref()
const loading = ref(false)
const form = reactive({
  oldPassword: '',
  newPassword: ''
})

const rules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }]
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    await changePassword(form)
    ElMessage.success('密码修改成功')
    form.oldPassword = ''
    form.newPassword = ''
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page-shell">
    <div class="panel" style="padding: 24px; max-width: 560px">
      <div class="panel-header">
        <div>
          <h3 style="margin: 0">修改密码</h3>
          <p style="margin: 6px 0 0; color: var(--muted)">建议定期更换登录密码</p>
        </div>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="form.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="form.newPassword" type="password" show-password />
        </el-form-item>
        <el-button type="primary" :loading="loading" @click="submit">保存修改</el-button>
      </el-form>
    </div>
  </div>
</template>
