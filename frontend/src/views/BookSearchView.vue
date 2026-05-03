<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchBooks, fetchCategories } from '@/api/books'

const router = useRouter()
const loading = ref(false)
const books = ref([])
const categories = ref([])
const filters = reactive({ keyword: '', categoryId: '' })

async function loadCategories() {
  categories.value = await fetchCategories()
}

async function loadBooks() {
  loading.value = true
  try {
    const data = await fetchBooks({ current: 1, size: 60, keyword: filters.keyword || undefined, categoryId: filters.categoryId || undefined })
    books.value = data.records
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadCategories()
  await loadBooks()
})
</script>

<template>
  <div class="page-shell">
    <div class="panel" style="padding: 20px">
      <div class="panel-header">
        <div>
          <h3 style="margin: 0">图书检索</h3>
          <p style="margin: 6px 0 0; color: var(--muted)">按书名、作者或分类查找可借图书</p>
        </div>
      </div>
      <div class="toolbar" style="margin-bottom: 18px">
        <el-input v-model="filters.keyword" placeholder="书名 / 作者 / ISBN" clearable style="width: 240px" />
        <el-select v-model="filters.categoryId" placeholder="分类" clearable style="width: 200px">
          <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-button type="primary" plain @click="loadBooks">搜索</el-button>
      </div>
      <div class="book-grid" v-loading="loading">
        <div v-for="book in books" :key="book.id" class="book-card">
          <div>
            <h3 style="margin: 0 0 8px">{{ book.title }}</h3>
            <div class="book-meta">{{ book.author }} · {{ book.publisher || '未知出版社' }}</div>
          </div>
          <div class="book-meta">ISBN: {{ book.isbn }}</div>
          <div class="book-meta">分类: {{ book.categoryName }}</div>
          <div class="book-meta">可借副本: {{ book.availableCopies }} / {{ book.totalCopies }}</div>
          <el-button type="primary" plain @click="router.push(`/catalog/${book.id}`)">查看详情</el-button>
        </div>
      </div>
    </div>
  </div>
</template>
