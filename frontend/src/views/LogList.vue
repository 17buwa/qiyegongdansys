<template>
  <div class="page-container">
    <el-card shadow="never" class="mb-16">
      <div class="filter-bar" style="margin-bottom: 0">
        <el-input v-model="query.username" placeholder="操作人" clearable style="width: 160px" @keyup.enter="search" @clear="search" />
        <el-select v-model="query.module" placeholder="模块" clearable style="width: 140px" @change="search">
          <el-option v-for="m in MODULES" :key="m.value" :label="m.label" :value="m.value" />
        </el-select>
        <el-button type="primary" @click="search">查询</el-button>
      </div>
    </el-card>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column label="模块" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ moduleLabel(row.module) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="action" label="操作" width="130" />
        <el-table-column prop="ip" label="IP" width="140" />
        <el-table-column prop="createdAt" label="时间" width="180" />
      </el-table>

      <div class="pager">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          layout="total, prev, pager, next, jumper"
          @current-change="load"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { pageLogs } from '@/api/log'

const MODULES = [
  { value: 'ticket', label: '工单' },
  { value: 'user', label: '用户' },
  { value: 'role', label: '角色' },
  { value: 'ticket_type', label: '工单类型' },
  { value: 'attachment', label: '附件' }
]

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, username: '', module: '' })

const moduleLabel = (m) => MODULES.find((x) => x.value === m)?.label || m

const load = async () => {
  loading.value = true
  try {
    const clean = { ...query }
    if (!clean.username) delete clean.username
    if (!clean.module) delete clean.module
    const res = await pageLogs(clean)
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const search = () => {
  query.pageNum = 1
  load()
}

onMounted(load)
</script>