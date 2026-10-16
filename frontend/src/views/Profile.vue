<template>
  <div class="page-container">
    <el-card shadow="never" style="max-width: 560px; margin: 0 auto">
      <template #header>个人中心</template>
      <div class="profile-user">
        <el-avatar :size="72" class="big-avatar">{{ firstChar }}</el-avatar>
        <div>
          <div class="name">{{ user?.name || user?.username }}</div>
          <el-tag :type="roleType(user?.roleCode)" size="small">{{ roleLabel(user?.roleCode) }}</el-tag>
        </div>
      </div>

      <el-divider />

      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ user?.username }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ user?.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ user?.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ user?.createdAt || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">我的权限</el-divider>
      <div class="perm-tags">
        <el-tag v-for="p in permissions" :key="p" style="margin: 0 8px 8px 0">{{ p }}</el-tag>
        <span v-if="permissions.length === 0" class="text-muted">暂无额外权限</span>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { roleLabel, roleType } from '@/utils/constants'

const store = useUserStore()
const user = computed(() => store.user)
const permissions = computed(() => store.permissions)
const firstChar = computed(() => (user.value?.name || user.value?.username || 'U').charAt(0).toUpperCase())

onMounted(() => {
  if (!store.permissions.length) {
    store.loadProfile()
  }
})
</script>

<style scoped>
.profile-user {
  display: flex;
  align-items: center;
  gap: 20px;
}
.big-avatar {
  background: #409eff;
  font-size: 30px;
}
.name {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 6px;
}
</style>