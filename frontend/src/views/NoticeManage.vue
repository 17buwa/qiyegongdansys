<template>
  <div class="page-container">
    <el-card shadow="never" class="mb-16">
      <div class="filter-bar" style="margin-bottom: 0">
        <el-input v-model="query.keyword" placeholder="公告标题" clearable style="width: 200px" @keyup.enter="search" @clear="search" />
        <el-select v-model="query.type" placeholder="公告类型" clearable style="width: 140px" @change="search">
          <el-option v-for="t in NOTICE_TYPES" :key="t.value" :label="t.label" :value="t.value" />
        </el-select>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button type="primary" plain icon="Plus" @click="handleCreate">发布公告</el-button>
      </div>
    </el-card>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip />
        <el-table-column label="类型" width="110">
          <template #default="{ row }">
            <el-tag size="small" :type="noticeType(row.type)">{{ noticeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 'ON' ? 'success' : 'info'">
              {{ row.status === 'ON' ? '已发布' : '已下线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisher" label="发布人" width="120" />
        <el-table-column prop="publishTime" label="发布时间" width="180" />
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
import { ElMessage } from 'element-plus'

const NOTICE_TYPES = [
  { value: 'SYSTEM', label: '系统公告' },
  { value: 'MAINTENANCE', label: '维护通知' },
  { value: 'RULES', label: '制度规范' },
  { value: 'OTHER', label: '其他' }
]

// 模拟数据，后续可对接后端公告接口
const mockList = [
  { id: 1, title: '企业工单系统 v1.0 正式上线！', type: 'SYSTEM', status: 'ON', publisher: '系统管理员', publishTime: '2026-10-01 09:00:00' },
  { id: 2, title: '本周五 22:00 系统例行维护', type: 'MAINTENANCE', status: 'ON', publisher: '运维组', publishTime: '2026-10-15 18:30:00' },
  { id: 3, title: '工单处理时效管理规范（修订版）', type: 'RULES', status: 'ON', publisher: '管理部', publishTime: '2026-10-10 10:00:00' },
  { id: 4, title: '国庆假期服务窗口安排', type: 'OTHER', status: 'OFF', publisher: '客服组', publishTime: '2026-09-28 15:00:00' }
]

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '', type: '' })

const noticeLabel = (t) => NOTICE_TYPES.find((x) => x.value === t)?.label || t
const noticeType = (t) => {
  const map = { SYSTEM: 'danger', MAINTENANCE: 'warning', RULES: 'primary', OTHER: 'info' }
  return map[t] || 'info'
}

const filterList = () => {
  let data = [...mockList]
  if (query.keyword) {
    data = data.filter((n) => n.title.includes(query.keyword))
  }
  if (query.type) {
    data = data.filter((n) => n.type === query.type)
  }
  const start = (query.pageNum - 1) * query.pageSize
  return {
    total: data.length,
    records: data.slice(start, start + query.pageSize)
  }
}

const load = async () => {
  loading.value = true
  try {
    const res = filterList()
    list.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const search = () => {
  query.pageNum = 1
  load()
}

const handleCreate = () => {
  ElMessage.info('公告发布功能开发中，敬请期待')
}

onMounted(load)
</script>