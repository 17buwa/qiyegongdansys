<template>
  <div class="page-container">
    <!-- 筛选栏 -->
    <el-card shadow="never" class="mb-16">
      <div class="filter-bar" style="margin-bottom: 0">
        <el-input
          v-model="query.keyword"
          placeholder="搜索标题 / 工单编号"
          clearable
          style="width: 220px"
          @keyup.enter="search"
          @clear="search"
        />
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 140px" @change="search">
          <el-option v-for="(v, k) in STATUS_CONFIG" :key="k" :label="v.label" :value="k" />
        </el-select>
        <el-select v-model="query.priority" placeholder="优先级" clearable style="width: 140px" @change="search">
          <el-option v-for="(v, k) in PRIORITY_CONFIG" :key="k" :label="v.label" :value="k" />
        </el-select>
        <el-button type="primary" @click="search">查询</el-button>
        <div class="flex-spacer" />
        <el-button type="success" :icon="Plus" @click="router.push('/tickets/create')">创建工单</el-button>
      </div>
    </el-card>

    <!-- 工单表格 -->
    <el-card shadow="never">
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="ticketNo" label="工单号" width="170" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="typeName" label="类型" width="120" />
        <el-table-column label="优先级" width="90">
          <template #default="{ row }">
            <el-tag :type="priorityType(row.priority)" size="small">{{ priorityLabel(row.priority) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="creatorName" label="创建人" width="110" />
        <el-table-column prop="assigneeName" label="处理人" width="110" />
        <el-table-column prop="createdAt" label="创建时间" width="170" />
        <el-table-column label="操作" width="110" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="router.push(`/tickets/${row.id}`)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="load"
          @current-change="load"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { pageTickets } from '@/api/ticket'
import { STATUS_CONFIG, PRIORITY_CONFIG, statusLabel, statusType, priorityLabel, priorityType } from '@/utils/constants'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: '',
  priority: ''
})

const cleanQuery = () => {
  const q = { ...query }
  if (!q.keyword) delete q.keyword
  if (!q.status) delete q.status
  if (!q.priority) delete q.priority
  return q
}

const load = async () => {
  loading.value = true
  try {
    const res = await pageTickets(cleanQuery())
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

<style scoped>
.flex-spacer {
  flex: 1;
}
</style>