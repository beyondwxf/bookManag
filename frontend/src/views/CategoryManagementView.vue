<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from '@/utils/element'
import { createCategory, deleteCategory, fetchCategories, updateCategory } from '@/api/books'

const loading = ref(false)
const categories = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const form = reactive({ name: '', code: '', sortOrder: 0, description: '' })

async function loadCategories() {
  loading.value = true
  try {
    categories.value = await fetchCategories()
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  Object.assign(form, { name: '', code: '', sortOrder: 0, description: '' })
  dialogVisible.value = true
}

function openEdit(row) {
  editingId.value = row.id
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

async function submit() {
  if (editingId.value) {
    await updateCategory(editingId.value, form)
    ElMessage.success('分类更新成功')
  } else {
    await createCategory(form)
    ElMessage.success('分类创建成功')
  }
  dialogVisible.value = false
  await loadCategories()
}

async function remove(row) {
  await ElMessageBox.confirm(`确认删除分类 ${row.name} 吗？`, '提示', { type: 'warning' })
  await deleteCategory(row.id)
  ElMessage.success('删除成功')
  await loadCategories()
}

onMounted(loadCategories)
</script>

<template>
  <div class="page-shell">
    <div class="panel" style="padding: 20px">
      <div class="panel-header">
        <div>
          <h3 style="margin: 0">图书分类</h3>
          <p style="margin: 6px 0 0; color: var(--muted)">维护图书目录的分类体系</p>
        </div>
        <el-button type="primary" @click="openCreate">新增分类</el-button>
      </div>

      <el-table :data="categories" v-loading="loading">
        <el-table-column prop="name" label="分类名称" min-width="180" />
        <el-table-column prop="code" label="分类编码" min-width="140" />
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column prop="description" label="说明" min-width="240" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑分类' : '新增分类'" width="520px">
      <el-form :model="form" label-position="top">
        <div class="form-grid">
          <el-form-item label="分类名称"><el-input v-model="form.name" /></el-form-item>
          <el-form-item label="分类编码"><el-input v-model="form.code" /></el-form-item>
          <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" style="width: 100%" /></el-form-item>
          <el-form-item label="说明"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
