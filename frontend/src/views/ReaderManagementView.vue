<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from '@/utils/element'
import { createReader, deleteReader, fetchReaders, updateReader } from '@/api/readers'

const loading = ref(false)
const readers = ref([])
const dialogVisible = ref(false)
const formRef = ref()
const editingId = ref(null)
const pagination = reactive({ current: 1, size: 10, total: 0 })
const filters = reactive({ keyword: '', status: '' })
const form = reactive({
  username: '',
  password: '',
  name: '',
  phone: '',
  email: '',
  idCard: '',
  status: 'ACTIVE'
})

async function loadReaders() {
  loading.value = true
  try {
    const data = await fetchReaders({
      current: pagination.current,
      size: pagination.size,
      keyword: filters.keyword || undefined,
      status: filters.status || undefined
    })
    readers.value = data.records
    pagination.total = data.total
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  Object.assign(form, { username: '', password: '', name: '', phone: '', email: '', idCard: '', status: 'ACTIVE' })
  dialogVisible.value = true
}

function openEdit(row) {
  editingId.value = row.id
  Object.assign(form, {
    username: row.username,
    password: '',
    name: row.name,
    phone: row.phone,
    email: row.email,
    idCard: row.idCard,
    status: row.status
  })
  dialogVisible.value = true
}

async function submit() {
  if (editingId.value) {
    await updateReader(editingId.value, form)
    ElMessage.success('读者更新成功')
  } else {
    await createReader(form)
    ElMessage.success('读者创建成功')
  }
  dialogVisible.value = false
  await loadReaders()
}

async function remove(row) {
  await ElMessageBox.confirm(`确认删除读者 ${row.name} 吗？`, '提示', { type: 'warning' })
  await deleteReader(row.id)
  ElMessage.success('删除成功')
  await loadReaders()
}

onMounted(loadReaders)
</script>

<template>
  <div class="page-shell">
    <div class="panel" style="padding: 20px">
      <div class="panel-header">
        <div>
          <h3 style="margin: 0">读者管理</h3>
          <p style="margin: 6px 0 0; color: var(--muted)">维护读者账号和档案信息</p>
        </div>
        <el-button type="primary" @click="openCreate">新增读者</el-button>
      </div>

      <div class="toolbar" style="margin-bottom: 16px">
        <el-input v-model="filters.keyword" placeholder="姓名/读者编号" clearable style="width: 220px" />
        <el-select v-model="filters.status" placeholder="状态" clearable style="width: 160px">
          <el-option label="ACTIVE" value="ACTIVE" />
          <el-option label="DISABLED" value="DISABLED" />
        </el-select>
        <el-button type="primary" plain @click="loadReaders">查询</el-button>
      </div>

      <el-table :data="readers" v-loading="loading">
        <el-table-column prop="readerNo" label="读者编号" min-width="120" />
        <el-table-column prop="username" label="账号" min-width="120" />
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column prop="phone" label="电话" min-width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="currentLoanCount" label="当前借阅" min-width="100" />
        <el-table-column prop="status" label="状态" min-width="100" />
        <el-table-column label="操作" width="180">
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
          @current-change="loadReaders"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑读者' : '新增读者'" width="560px">
      <el-form ref="formRef" :model="form" label-position="top">
        <div class="form-grid">
          <el-form-item label="登录账号" v-if="!editingId">
            <el-input v-model="form.username" />
          </el-form-item>
          <el-form-item label="初始密码" v-if="!editingId">
            <el-input v-model="form.password" type="password" show-password />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="电话">
            <el-input v-model="form.phone" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="form.email" />
          </el-form-item>
          <el-form-item label="证件号">
            <el-input v-model="form.idCard" />
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
