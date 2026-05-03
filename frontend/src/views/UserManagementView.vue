<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from '@/utils/element'
import { createUser, deleteUser, fetchRoles, fetchUsers, updateUser } from '@/api/users'

const loading = ref(false)
const dialogVisible = ref(false)
const roles = ref([])
const users = ref([])
const pagination = reactive({ current: 1, size: 10, total: 0 })
const filters = reactive({ keyword: '', status: '', roleId: '' })
const formRef = ref()
const editingId = ref(null)
const form = reactive({
  username: '',
  password: '',
  displayName: '',
  roleId: '',
  phone: '',
  status: 'ACTIVE'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  displayName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

async function loadRoles() {
  roles.value = await fetchRoles()
}

async function loadUsers() {
  loading.value = true
  try {
    const data = await fetchUsers({
      current: pagination.current,
      size: pagination.size,
      keyword: filters.keyword || undefined,
      status: filters.status || undefined,
      roleId: filters.roleId || undefined
    })
    users.value = data.records
    pagination.total = data.total
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  Object.assign(form, { username: '', password: '', displayName: '', roleId: '', phone: '', status: 'ACTIVE' })
  dialogVisible.value = true
}

function openEdit(row) {
  editingId.value = row.id
  Object.assign(form, { username: row.username, password: '', displayName: row.displayName, roleId: row.roleId, phone: row.phone, status: row.status })
  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()
  if (editingId.value) {
    await updateUser(editingId.value, {
      displayName: form.displayName,
      roleId: form.roleId,
      phone: form.phone,
      status: form.status
    })
    ElMessage.success('用户更新成功')
  } else {
    await createUser(form)
    ElMessage.success('用户创建成功')
  }
  dialogVisible.value = false
  await loadUsers()
}

async function remove(row) {
  await ElMessageBox.confirm(`确认删除用户 ${row.displayName} 吗？`, '提示', { type: 'warning' })
  await deleteUser(row.id)
  ElMessage.success('删除成功')
  await loadUsers()
}

onMounted(async () => {
  await loadRoles()
  await loadUsers()
})
</script>

<template>
  <div class="page-shell">
    <div class="panel" style="padding: 20px">
      <div class="panel-header">
        <div>
          <h3 style="margin: 0">用户管理</h3>
          <p style="margin: 6px 0 0; color: var(--muted)">维护系统管理员与图书管理员账号</p>
        </div>
        <el-button type="primary" @click="openCreate">新增用户</el-button>
      </div>

      <div class="toolbar" style="margin-bottom: 16px">
        <el-input v-model="filters.keyword" placeholder="用户名/姓名" style="width: 220px" clearable />
        <el-select v-model="filters.roleId" placeholder="角色" clearable style="width: 180px">
          <el-option v-for="role in roles" :key="role.id" :label="role.name" :value="role.id" />
        </el-select>
        <el-select v-model="filters.status" placeholder="状态" clearable style="width: 160px">
          <el-option label="ACTIVE" value="ACTIVE" />
          <el-option label="DISABLED" value="DISABLED" />
        </el-select>
        <el-button type="primary" plain @click="loadUsers">查询</el-button>
      </div>

      <el-table :data="users" v-loading="loading">
        <el-table-column prop="username" label="用户名" min-width="140" />
        <el-table-column prop="displayName" label="姓名" min-width="120" />
        <el-table-column prop="roleName" label="角色" min-width="140" />
        <el-table-column prop="phone" label="电话" min-width="140" />
        <el-table-column prop="status" label="状态" min-width="100" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="display: flex; justify-content: flex-end; margin-top: 16px">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          layout="total, prev, pager, next"
          :total="pagination.total"
          @current-change="loadUsers"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑用户' : '新增用户'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="form-grid">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" :disabled="Boolean(editingId)" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password :placeholder="editingId ? '编辑时无需修改' : '请输入初始密码'" />
          </el-form-item>
          <el-form-item label="姓名" prop="displayName">
            <el-input v-model="form.displayName" />
          </el-form-item>
          <el-form-item label="角色" prop="roleId">
            <el-select v-model="form.roleId">
              <el-option v-for="role in roles" :key="role.id" :label="role.name" :value="role.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="电话">
            <el-input v-model="form.phone" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status">
              <el-option label="ACTIVE" value="ACTIVE" />
              <el-option label="DISABLED" value="DISABLED" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
