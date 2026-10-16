<template>
  <div class="page-container">
    <!-- 概览卡片 -->
    <el-row :gutter="16" class="mb-16">
      <el-col :span="6" v-for="card in cards" :key="card.key">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" :style="{ background: card.bg, color: card.color }">
              <el-icon :size="26"><component :is="card.icon" /></el-icon>
            </div>
            <div>
              <div class="stat-value">{{ card.value }}</div>
              <div class="stat-label">{{ card.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>近 7 天工单趋势</template>
          <div ref="trendRef" class="chart" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>工单状态分布</template>
          <div ref="statusRef" class="chart" />
        </el-card>
      </el-col>
      <el-col :span="12" class="mt-16">
        <el-card shadow="hover">
          <template #header>工单优先级分布</template>
          <div ref="priorityRef" class="chart" />
        </el-card>
      </el-col>
      <el-col :span="12" class="mt-16">
        <el-card shadow="hover">
          <template #header>工单类型分布</template>
          <div ref="typeRef" class="chart" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import {
  getOverview,
  getTrend,
  getByStatus,
  getByPriority,
  getByType
} from '@/api/statistics'
import { STATUS_CONFIG, PRIORITY_CONFIG } from '@/utils/constants'

const trendRef = ref()
const statusRef = ref()
const priorityRef = ref()
const typeRef = ref()

const charts = []
const cards = ref([
  { key: 'total', label: '工单总数', value: 0, icon: 'Tickets', color: '#409eff', bg: 'rgba(64,158,255,.12)' },
  { key: 'new', label: '待处理(新建)', value: 0, icon: 'Bell', color: '#e6a23c', bg: 'rgba(230,162,60,.12)' },
  { key: 'processing', label: '处理中', value: 0, icon: 'Loading', color: '#f56c6c', bg: 'rgba(245,108,108,.12)' },
  { key: 'closed', label: '已关闭', value: 0, icon: 'CircleCheck', color: '#67c23a', bg: 'rgba(103,194,58,.12)' }
])

const renderChart = (el, option) => {
  if (!el) return
  const chart = echarts.init(el)
  chart.setOption(option)
  charts.push(chart)
}

onMounted(async () => {
  try {
    const [overview, trend, byStatus, byPriority, byType] = await Promise.all([
      getOverview(),
      getTrend(),
      getByStatus(),
      getByPriority(),
      getByType()
    ])
    const data = overview.data
    cards.value.forEach((c) => {
      c.value = data[c.key] ?? 0
    })

    renderChart(trendRef.value, {
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, top: 30, bottom: 30 },
      xAxis: { type: 'category', data: trend.data.map((i) => i.date.slice(5)) },
      yAxis: { type: 'value', minInterval: 1 },
      series: [
        {
          type: 'line',
          smooth: true,
          areaStyle: { opacity: 0.15 },
          data: trend.data.map((i) => i.count),
          itemStyle: { color: '#409eff' }
        }
      ]
    })

    renderChart(statusRef.value, {
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [
        {
          type: 'pie',
          radius: ['40%', '65%'],
          data: byStatus.data.map((i) => ({
            name: STATUS_CONFIG[i.name]?.label || i.name,
            value: i.value
          })),
          emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,.3)' } }
        }
      ]
    })

    renderChart(priorityRef.value, {
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [
        {
          type: 'pie',
          radius: '62%',
          data: byPriority.data.map((i) => ({
            name: PRIORITY_CONFIG[i.name]?.label || i.name,
            value: i.value
          }))
        }
      ]
    })

    renderChart(typeRef.value, {
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: 40, right: 20, top: 30, bottom: 30 },
      xAxis: { type: 'category', data: byType.data.map((i) => i.name) },
      yAxis: { type: 'value', minInterval: 1 },
      series: [{ type: 'bar', barWidth: 40, data: byType.data.map((i) => i.value), itemStyle: { color: '#67c23a', borderRadius: [4, 4, 0, 0] } }]
    })
  } catch (e) {
    // 错误已由拦截器提示
  }
})

onBeforeUnmount(() => {
  charts.forEach((c) => c.dispose())
})
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.stat-value {
  font-size: 26px;
  font-weight: 700;
  color: #303133;
}
.stat-label {
  font-size: 13px;
  color: #909399;
}
.chart {
  height: 300px;
}
</style>