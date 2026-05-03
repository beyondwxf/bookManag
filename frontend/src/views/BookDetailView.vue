<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchBookDetail } from '@/api/books'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const book = ref({})

async function load() {
  loading.value = true
  try {
    book.value = await fetchBookDetail(route.params.id)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page-shell">
    <div class="panel" style="padding: 24px" v-loading="loading">
      <div class="panel-header">
        <div>
          <h3 style="margin: 0">{{ book.title }}</h3>
          <p style="margin: 6px 0 0; color: var(--muted)">{{ book.author }} · {{ book.publisher || '未知出版社' }}</p>
        </div>
        <el-button @click="router.push('/catalog')">返回检索</el-button>
      </div>

      <div class="detail-grid" style="margin-bottom: 18px">
        <div class="detail-item">
          <span>ISBN</span>
          <strong>{{ book.isbn }}</strong>
        </div>
        <div class="detail-item">
          <span>分类</span>
          <strong>{{ book.categoryName }}</strong>
        </div>
        <div class="detail-item">
          <span>出版日期</span>
          <strong>{{ book.publishDate || '-' }}</strong>
        </div>
        <div class="detail-item">
          <span>馆藏副本</span>
          <strong>{{ book.totalCopies || 0 }}</strong>
        </div>
        <div class="detail-item">
          <span>当前可借</span>
          <strong>{{ book.availableCopies || 0 }}</strong>
        </div>
      </div>

      <el-descriptions title="内容简介" :column="1" border>
        <el-descriptions-item>{{ book.description || '暂无简介' }}</el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>
